package com.shopsphere.order_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.CheckoutRequestDTO;
import com.shopsphere.order_service.dto.OrderItemDTO;
import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.entities.DestinationAddressEntity;
import com.shopsphere.order_service.entities.OrderEntity;
import com.shopsphere.order_service.entities.OrderItemEntity;
import com.shopsphere.order_service.entities.ShippingDetailsEntity;
import com.shopsphere.order_service.exceptions.ResourceAlreadyExistException;
import com.shopsphere.order_service.exceptions.ResourceNotFoundException;
import com.shopsphere.order_service.repositories.DestinationAddressCache;
import com.shopsphere.order_service.repositories.OrderItemRepository;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.repositories.ShippingDetailsCache;
import com.shopsphere.order_service.services.IOrderService;
import com.shopsphere.order_service.services.client.PaymentFeignClient;
import com.shopsphere.order_service.utils.OrderStatus;
import com.shopsphere.order_service.utils.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ObjectMapper objectMapper;

    private final PaymentFeignClient paymentFeignClient;

    private final DestinationAddressCache destinationAddressCache;

    private final ShippingDetailsCache shippingDetailsCache;

    @Override
    public <T> T placeOrder(OrderRequestDTO orderRequest) {
        orderRepository.findByCode(orderRequest.getCode()).ifPresent(existingOrder -> {
            throw new ResourceAlreadyExistException("Order", " code", orderRequest.getCode());
        });

        final OrderEntity orderEntity = initializeOrder(orderRequest);

        orderRequest.getOrderItems().forEach(orderItemDTO -> {
            final OrderItemEntity newOrderItem = objectMapper.convertValue(orderItemDTO, OrderItemEntity.class);
            newOrderItem.setOrderId(orderEntity.getOrderId());

            final OrderItemEntity itemEntity = orderItemRepository.save(newOrderItem);
            orderEntity.getOrderItemIds().add(itemEntity.getOrderItemId());
        });
        orderEntity.setOrderStatus(OrderStatus.PENDING);
        final OrderEntity savedOrder = orderRepository.save(orderEntity);

        saveShippingDetailsInRedisCache(orderRequest.getShippingRequest(), savedOrder.getOrderId());

        return getPaymentSessionURL(savedOrder);
    }

    private OrderEntity initializeOrder(final OrderRequestDTO request) {
        final OrderEntity orderEntity = new OrderEntity();

        orderEntity.setCode(request.getCode());
        orderEntity.setPaymentMethod(getPaymentMethod(request.getPaymentMethod()));
        orderEntity.setOrderItemIds(new ArrayList<>());
        orderEntity.setOrderStatus(ApplicationDefaultConstants.ORDER_STATUS_INIT);
        orderEntity.setTotalOrderPrice(ApplicationDefaultConstants.ORDER_PRICE_INIT);

        return orderRepository.save(orderEntity);
    }

    private PaymentMethod getPaymentMethod(final String paymentMethod) {

        return switch (paymentMethod.toLowerCase()) {
            case "applepay" -> PaymentMethod.APPLPAY;
            case "paypal" -> PaymentMethod.PAYPAL;
            case "stripe" -> PaymentMethod.STRIPE;

            default -> throw new ResourceNotFoundException("Payment Method", "payment method", paymentMethod);
        };
    }

    private <T> T getPaymentSessionURL(final OrderEntity order) {
        final List<OrderItemDTO> orderItems = order.getOrderItemIds().stream()
                .map(orderItemId -> objectMapper.convertValue(
                        orderItemRepository.findById(orderItemId), OrderItemDTO.class))
                .toList();

        final CheckoutRequestDTO checkoutRequest = CheckoutRequestDTO.builder()
                .orderId(order.getOrderId())
                .orderItems(orderItems)
                .userEmail(order.getCreatedBy())
                .paymentMethod(order.getPaymentMethod())
                .build();

        if (order.getPaymentMethod() == PaymentMethod.STRIPE)
            return (T) paymentFeignClient.handleStripeCheckoutRequest(checkoutRequest).getBody();

        throw new UnsupportedOperationException("Unsupported payment method");
    }

    private void saveShippingDetailsInRedisCache(final ShippingRequestDTO shippingRequestDTO, final Long orderId) {
        final ShippingDetailsEntity shippingDetailsEntity = initializeShippingDetailsEntity(shippingRequestDTO, orderId);

        final DestinationAddressEntity destinationAddressEntity = objectMapper
                .convertValue(shippingRequestDTO.getCustomerAddress(), DestinationAddressEntity.class);
        destinationAddressEntity.setCacheId(UUID.randomUUID());
        destinationAddressEntity.setShippingDetailsCacheId(shippingDetailsEntity.getCacheId());
        final DestinationAddressEntity savedAddressEntity = destinationAddressCache.save(destinationAddressEntity);

        shippingDetailsEntity.setAddressCacheId(savedAddressEntity.getCacheId());
        shippingDetailsCache.save(shippingDetailsEntity);
    }

    private ShippingDetailsEntity initializeShippingDetailsEntity(ShippingRequestDTO shippingRequestDTO, Long orderId) {
        final ShippingDetailsEntity shippingDetailsEntity = objectMapper.convertValue(shippingRequestDTO, ShippingDetailsEntity.class);
        shippingDetailsEntity.setCacheId(UUID.randomUUID());
        shippingDetailsEntity.setOrderId(orderId);

        return shippingDetailsCache.save(shippingDetailsEntity);
    }

    private ShippingRequestDTO formatShippingRequest(final ShippingRequestDTO shippingRequestDTO, final Long orderId) {
        shippingRequestDTO.setOrderId(orderId);
        return shippingRequestDTO;
    }
}
