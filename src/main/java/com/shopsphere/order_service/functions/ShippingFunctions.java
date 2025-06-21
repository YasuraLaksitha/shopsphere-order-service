package com.shopsphere.order_service.functions;

import com.shopsphere.order_service.dto.FailureEventDTO;
import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;
import com.shopsphere.order_service.services.IOrderService;
import com.shopsphere.order_service.services.IShippingService;
import com.shopsphere.order_service.utils.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class ShippingFunctions {

    @Bean
    public Consumer<FailureEventDTO<ShippingRequestDTO>> handleRetryShippingEvent(
            final IOrderService orderService,
            final IShippingService shippingService) {
        return event -> {
            final ShippingRequestDTO failureObject = event.getFailureObject();

            orderService.updateOrderStatus(failureObject.getOrderId(), OrderStatus.RETRY.name());
            final ShippingResponseDTO shippingResponseDTO = shippingService.sendShippingRequest(failureObject);

            if (Objects.nonNull(shippingResponseDTO) &&
                    shippingResponseDTO.getStatus().is2xxSuccessful()) {

                log.debug("Shipping request processed successfully: {}", shippingResponseDTO);
                orderService.updateOrderStatus(failureObject.getOrderId(), OrderStatus.LABELED.name());
            }
        };
    }
}
