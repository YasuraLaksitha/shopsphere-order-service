package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.StripeResponseDTO;
import com.shopsphere.order_service.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/user/create")
    public ResponseEntity<Object> placeOrder(@Valid @RequestBody final OrderRequestDTO orderRequestDTO) {
        final StripeResponseDTO stripeResponseDTO = orderService.placeOrder(orderRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stripeResponseDTO);
    }
}
