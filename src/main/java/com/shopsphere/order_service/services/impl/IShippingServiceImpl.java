package com.shopsphere.order_service.services.impl;

import com.shopsphere.order_service.dto.FailureEventDTO;
import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IShippingService;
import com.shopsphere.order_service.services.client.ShippingFeignClient;
import com.shopsphere.order_service.utils.OrderStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IShippingServiceImpl implements IShippingService {

    private final ShippingFeignClient shippingFeignClient;

    private final OrderRepository orderRepository;

    private final StreamBridge streamBridge;

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

        final FailureEventDTO<ShippingRequestDTO> failureEventDTO = FailureEventDTO.<ShippingRequestDTO>builder()
                .reason("Service unavailable")
                .failureObject(shippingRequestDTO)
                .timestamp(LocalDateTime.now())
                .build();

        streamBridge.send("shippingFailure-out-0", failureEventDTO);

        return null;
    }
}
