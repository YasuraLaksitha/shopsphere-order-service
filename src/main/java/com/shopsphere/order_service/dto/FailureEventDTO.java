package com.shopsphere.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FailureEventDTO<T> {

    private T failureObject;

    private String reason;

    private LocalDateTime timestamp;
}
