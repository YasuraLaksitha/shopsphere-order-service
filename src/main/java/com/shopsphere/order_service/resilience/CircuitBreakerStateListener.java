package com.shopsphere.order_service.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binding.BindingsLifecycleController;
import org.springframework.cloud.stream.endpoint.BindingsEndpoint;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerStateListener {

    private final BindingsEndpoint bindingsEndpoint;

    private final CircuitBreakerRegistry registry;

    @PostConstruct
    public void init() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("shippingService");
        circuitBreaker.getEventPublisher().onStateTransition(this::onShippingServiceStateTransfer);
    }

    public void onShippingServiceStateTransfer(final CircuitBreakerOnStateTransitionEvent event) {
        final String bindingName = "orders.handleRetryShippingEvent-in-0";

        switch (event.getStateTransition().getToState()) {
            case OPEN -> {
                log.debug("CircuitBreaker is OPEN. Disabling message consumption from binding: {}", bindingName);
                bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STOPPED);
            }

            case FORCED_OPEN -> {
                log.debug("CircuitBreaker is FORCED OPEN. Halting consumer binding: {}", bindingName);
                bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STOPPED);
            }

            case HALF_OPEN -> {
                log.debug("CircuitBreaker is HALF_OPEN. binding disabled: {}", bindingName);
                bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STOPPED);
            }

            case CLOSED -> {
                log.debug("CircuitBreaker is CLOSED. Re-enabling binding: {}", bindingName);
                bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STARTED);
            }

            default ->
                bindingsEndpoint.changeState(bindingName, BindingsLifecycleController.State.STARTED);
        }
    }
}
