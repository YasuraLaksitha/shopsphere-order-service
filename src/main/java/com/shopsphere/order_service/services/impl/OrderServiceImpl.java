package com.shopsphere.order_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.entities.OrderEntity;
import com.shopsphere.order_service.entities.OrderItemEntity;
import com.shopsphere.order_service.exceptions.ResourceAlreadyExistException;
import com.shopsphere.order_service.repositories.OrderItemRepository;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void placeOrder(OrderRequestDTO orderRequest) {
        orderRepository.findByCode(orderRequest.getCode()).ifPresent(existingOrder -> {
            throw new ResourceAlreadyExistException("Order", " code", orderRequest.getCode());
        });

        final OrderEntity orderEntity = initializeOrder(orderRequest.getCode());

        orderRequest.getOrderItems().forEach(orderItemDTO -> {
            final OrderItemEntity newOrderItem = objectMapper.convertValue(orderItemDTO, OrderItemEntity.class);
            newOrderItem.setOrderId(orderEntity.getOrderId());

            final OrderItemEntity itemEntity = orderItemRepository.save(newOrderItem);
            orderEntity.getOrderItemIds().add(itemEntity.getOrderItemId());
        });

        orderRepository.save(orderEntity);
    }

    private OrderEntity initializeOrder(final String code) {
        final OrderEntity orderEntity = new OrderEntity();

        orderEntity.setCode(code);
        orderEntity.setOrderItemIds(new ArrayList<>());
        orderEntity.setOrderStatus(ApplicationDefaultConstants.ORDER_STATUS_INIT);
        orderEntity.setTotalOrderPrice(ApplicationDefaultConstants.ORDER_PRICE_INIT);

        return orderRepository.save(orderEntity);
    }
}
