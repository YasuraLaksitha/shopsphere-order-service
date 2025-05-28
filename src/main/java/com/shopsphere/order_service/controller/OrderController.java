package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.ResponseDTO;
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

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/user/create")
    public ResponseEntity<ResponseDTO> placeOrder(@Valid @RequestBody final OrderRequestDTO orderRequestDTO) {
        orderService.placeOrder(orderRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .status(HttpStatus.CREATED)
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_201)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
