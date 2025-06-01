package com.shopsphere.order_service.services.client;

import com.shopsphere.order_service.dto.CheckoutRequestDTO;
import com.shopsphere.order_service.dto.StripeResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-gateway")
public interface PaymentFeignClient {

    @PostMapping("/api/payments/stripe/checkout")
     ResponseEntity<StripeResponseDTO> handleStripeCheckoutRequest(@RequestBody CheckoutRequestDTO checkoutRequestDTO);
}
