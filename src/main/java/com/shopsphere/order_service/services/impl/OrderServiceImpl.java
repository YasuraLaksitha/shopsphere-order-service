package com.shopsphere.order_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.entities.OrderEntity;
import com.shopsphere.order_service.entities.OrderItemEntity;
import com.shopsphere.order_service.exceptions.ResourceAlreadyExistException;
import com.shopsphere.order_service.exceptions.ResourceNotFoundException;
import com.shopsphere.order_service.repositories.OrderItemRepository;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IOrderService;
import com.shopsphere.order_service.services.ICacheService;
import com.shopsphere.order_service.services.IPaymentService;
import com.shopsphere.order_service.services.IShippingService;
import com.shopsphere.order_service.utils.OrderStatus;
import com.shopsphere.order_service.utils.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ObjectMapper objectMapper;

    private final IPaymentService paymentService;

    private final IShippingService shippingService;

    private final ICacheService cacheService;

    private final StreamBridge streamBridge;

    @Override
    public <T> T placeOrder(OrderRequestDTO orderRequest) {
        final Optional<OrderEntity> optionalOrder = orderRepository.findByCode(orderRequest.getCode());

        OrderEntity orderEntity;

        if (optionalOrder.isEmpty()) {
            orderEntity = initializeOrder(orderRequest);
            orderEntity.setOrderStatus(OrderStatus.PENDING);

        } else if (optionalOrder.get().getOrderStatus() == OrderStatus.PAYMENT_FAILED) {
            orderEntity = optionalOrder.get();
            this.updateOrderStatus(optionalOrder.get().getOrderId(), OrderStatus.RETRY.name());

        } else
            throw new ResourceAlreadyExistException("order", "order code", orderRequest.getCode());

        if (orderEntity.getOrderStatus() == OrderStatus.PENDING) {
            orderRequest.getOrderItems().forEach(orderItemDTO -> {
                final OrderItemEntity newOrderItem = objectMapper.convertValue(orderItemDTO, OrderItemEntity.class);
                newOrderItem.setOrderId(orderEntity.getOrderId());

                final OrderItemEntity itemEntity = orderItemRepository.save(newOrderItem);
                orderEntity.getOrderItemIds().add(itemEntity.getOrderItemId());
            });
        }

        final OrderEntity savedOrder = orderRepository.save(orderEntity);

        try {
            cacheService.saveShippingDetailsIntoCache(orderRequest.getShippingRequest(), savedOrder.getOrderId(), savedOrder.getCreatedBy());

            if (savedOrder.getOrderStatus() == OrderStatus.PAYMENT_FAILED ||
                    savedOrder.getOrderStatus() == OrderStatus.PENDING)
                return getPaymentSessionURL(savedOrder);

        } catch (Exception e) {
            this.updateOrderStatus(savedOrder.getOrderId(), OrderStatus.PAYMENT_FAILED.name());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ShippingResponseDTO handleShippingRequest(Long orderId) {
        final ShippingResponseDTO shippingResponseDTO = shippingService.sendShippingRequest(cacheService.retrieveShippingRequestByOrderId(orderId));

        final OrderStatus status = Objects.nonNull(shippingResponseDTO) ?
                OrderStatus.LABELED :
                OrderStatus.SHIPPING_FAILED;
        this.updateOrderStatus(orderId, status.name());

        return shippingResponseDTO;
    }

    /**
     *
     * @param request - order request Object
     * @return - initialized order entity
     */
    private OrderEntity initializeOrder(final OrderRequestDTO request) {
        final OrderEntity orderEntity = new OrderEntity();

        orderEntity.setCode(request.getCode());
        orderEntity.setPaymentMethod(getPaymentMethod(request.getPaymentMethod()));
        orderEntity.setOrderItemIds(new ArrayList<>());
        orderEntity.setOrderStatus(ApplicationDefaultConstants.ORDER_STATUS_INIT);
        orderEntity.setTotalOrderPrice(ApplicationDefaultConstants.ORDER_PRICE_INIT);

        return orderRepository.save(orderEntity);
    }

    /**
     *
     * @param paymentMethod - user preferred payment method
     * @return - payment method Object
     */
    private PaymentMethod getPaymentMethod(final String paymentMethod) {

        return switch (paymentMethod.toLowerCase()) {
            case "applepay" -> PaymentMethod.APPLPAY;
            case "paypal" -> PaymentMethod.PAYPAL;
            case "stripe" -> PaymentMethod.STRIPE;

            default -> throw new ResourceNotFoundException("Payment Method", "payment method", paymentMethod);
        };
    }

    private <T> T getPaymentSessionURL(final OrderEntity order) throws ExecutionException, InterruptedException {
        final List<OrderItemDTO> orderItems = order.getOrderItemIds().stream()
                .map(orderItemId -> objectMapper.convertValue(
                        orderItemRepository.findById(orderItemId), OrderItemDTO.class))
                .toList();

        final CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .orderId(order.getOrderId())
                .orderItems(orderItems)
                .userId(order.getCreatedBy())
                .paymentMethod(order.getPaymentMethod())
                .build();

        return (T) paymentService.createSessionURL(checkoutRequest, order.getPaymentMethod()).get();
    }

    @Override
    public void updateOrderStatus(final Long orderId, final String orderStatus) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {

            final String status = orderStatus.toUpperCase();

            final OrderStatus newOrderStatus = Arrays.stream(OrderStatus.values())
                    .filter(s -> s.name().equals(status))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("order status", "name", status));

            orderEntity.setOrderStatus(newOrderStatus);
            orderRepository.save(orderEntity);
        });
    }

    @Override
    public void sendProductUpdateRequest(Long orderId) {
        orderRepository.findById(orderId).ifPresent(orderEntity -> {
            final Map<String, Integer> productQuantityMap = new HashMap<>();

            orderItemRepository.findAllById(orderEntity.getOrderItemIds()).forEach(orderItemEntity ->
                    productQuantityMap.put(orderItemEntity.getProductName(), orderItemEntity.getQuantity()));

            streamBridge.send("productUpdate-out-0", productQuantityMap);
        });
    }

    @Override
    public PaginationResponseDTO<OrderRequestDTO> filterOrders
            (String sortBy, String sortOrder, int pageNumber, int pageSize, String orderDate, String userId) {

        Specification<OrderEntity> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("createdBy")), userId.toLowerCase());

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));

        if (StringUtils.hasText(orderDate)) {
            final LocalDate localDate = this.formatDate(orderDate);

            final Instant startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            final Instant endOfDay = localDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay));
        }

        final Sort.Direction sortDir = ApplicationDefaultConstants.ORDER_SORT_ORDER.equalsIgnoreCase(sortOrder) ?
                Sort.Direction.ASC :
                Sort.Direction.DESC;
        final Sort sortOrderBy = Sort.by(sortDir, sortBy);
        final PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sortOrderBy);

        Page<OrderEntity> orderEntityPage;
        orderEntityPage = orderRepository.findAll(spec, pageRequest);

        final List<OrderRequestDTO> requestDTOS = orderEntityPage.getContent().stream().map(orderEntity -> {
            final List<OrderItemDTO> itemDTOS = orderItemRepository.findAllById(orderEntity.getOrderItemIds())
                    .stream().map(orderItemEntity ->
                            objectMapper.convertValue(orderItemEntity, OrderItemDTO.class)
                    ).toList();

            final OrderRequestDTO orderRequestDTO = objectMapper.convertValue(orderEntity, OrderRequestDTO.class);
            orderRequestDTO.setOrderItems(itemDTOS);

            return orderRequestDTO;
        }).toList();

        return PaginationResponseDTO.<OrderRequestDTO>builder()
                .data(requestDTOS)
                .isLastPage(orderEntityPage.isLast())
                .sortOrder(sortOrder)
                .sortBy(sortBy)
                .pageNumber(String.valueOf(pageNumber))
                .pageSize(String.valueOf(pageSize))
                .build();
    }

    @Override
    public boolean deleteByOrderCode(String orderCode) {
        final OrderEntity orderEntity = orderRepository.findByCode(orderCode).orElseThrow(
                () -> new ResourceNotFoundException("Order", "order code", orderCode)
        );
        orderEntity.setDeleted(true);
        orderRepository.save(orderEntity);

        return orderEntity.isDeleted();
    }

    private LocalDate formatDate(final String orderDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(orderDate, formatter);
    }
}
