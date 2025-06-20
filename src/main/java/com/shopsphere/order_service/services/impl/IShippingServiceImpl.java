package com.shopsphere.order_service.services.impl;

import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IShippingService;
import com.shopsphere.order_service.services.client.ShippingFeignClient;
import com.shopsphere.order_service.utils.OrderStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IShippingServiceImpl implements IShippingService {

    private final ShippingFeignClient shippingFeignClient;

    private final OrderRepository orderRepository;

    @CircuitBreaker(name = "shippingService", fallbackMethod = "sendShippingRequestFallback")
    @Override
    public ShippingResponseDTO sendShippingRequest(ShippingRequestDTO shippingRequestDTO) {
        return shippingFeignClient.createShippingObject(shippingRequestDTO).getBody();
    }

    @SuppressWarnings("unused")
    public ShippingResponseDTO sendShippingRequestFallback(ShippingRequestDTO shippingRequestDTO, Throwable throwable) {
        orderRepository.findById(shippingRequestDTO.getOrderId()).ifPresent(orderEntity -> {
            orderEntity.setOrderStatus(OrderStatus.SHIPPING_FAILED);
            orderRepository.save(orderEntity);
        });

        return ShippingResponseDTO.builder()
                .status(HttpStatus.CREATED)
                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_201)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
