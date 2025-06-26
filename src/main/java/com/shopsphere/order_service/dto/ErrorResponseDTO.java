package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(
        name = "Error Response",
        description = "Schema to hold error response information"
)
public class ErrorResponseDTO {

    @Schema(description = "Error response status")
    private String status;

    @Schema(description = "URL for error response")
    private String path;

    @Schema(description = "Reason why error occurred")
    private String message;

    @Schema(description = "Timestamp when error response generated")
    private LocalDateTime timestamp;
}
