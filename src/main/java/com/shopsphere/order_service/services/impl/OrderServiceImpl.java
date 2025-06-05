package com.shopsphere.order_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.*;
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
import com.shopsphere.order_service.services.client.ICacheService;
import com.shopsphere.order_service.services.client.PaymentFeignClient;
import com.shopsphere.order_service.services.ShippingFeignClient;
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

    private final ShippingFeignClient shippingFeignClient;

    private final ICacheService cacheService;

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

        cacheService.saveIntoCache(orderRequest.getShippingRequest(), savedOrder.getOrderId());

        return getPaymentSessionURL(savedOrder);
    }

    @Override
    public ShippingResponseDTO handleShippingRequest(Long orderId) {
        return shippingFeignClient.createShippingObject(cacheService.retrieveByOrderId(orderId))
                .getBody();
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
}
