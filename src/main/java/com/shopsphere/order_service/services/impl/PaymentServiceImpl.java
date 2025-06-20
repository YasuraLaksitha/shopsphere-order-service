package com.shopsphere.order_service.services.impl;

import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.CheckoutRequestDTO;
import com.shopsphere.order_service.dto.StripeResponseDTO;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IPaymentService;
import com.shopsphere.order_service.services.client.PaymentFeignClient;
import com.shopsphere.order_service.utils.OrderStatus;
import com.shopsphere.order_service.utils.PaymentMethod;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentFeignClient paymentFeignClient;

    private final OrderRepository orderRepository;

    @CircuitBreaker(name = "stripeService", fallbackMethod = "createSessionURLFallback")
    @TimeLimiter(name = "stripeService")
    @Retry(name = "stripeService")
    @Override
    public CompletableFuture<StripeResponseDTO> createSessionURL(CheckoutRequestDTO checkoutRequestDTO, PaymentMethod method) {

        if (method == PaymentMethod.STRIPE)
            return CompletableFuture.supplyAsync(() -> {
                log.info(">> Attempt to create Stripe session at: {}", Instant.now());
                return paymentFeignClient.handleStripeCheckoutRequest(checkoutRequestDTO);
            }).thenApply(ResponseEntity::getBody);

        throw new UnsupportedOperationException("Unsupported payment method: " + method);
    }

    @SuppressWarnings("unused")
    public CompletableFuture<StripeResponseDTO> createSessionURLFallback(
            CheckoutRequestDTO checkoutRequestDTO, PaymentMethod ignoredMethod, Throwable ignoredThrowable) {

        orderRepository.findById(checkoutRequestDTO.getOrderId()).ifPresent(orderEntity -> {
            orderEntity.setOrderStatus(OrderStatus.FAILED);
            orderRepository.save(orderEntity);
        });

        return CompletableFuture.failedFuture(
                new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        ApplicationDefaultConstants.RESPONSE_MESSAGE_503
                )
        );
    }
}
