package com.shopsphere.order_service.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ShippingResponseDTO {

    private HttpStatus status;

    private String message;

    private LocalDateTime timestamp;
}
