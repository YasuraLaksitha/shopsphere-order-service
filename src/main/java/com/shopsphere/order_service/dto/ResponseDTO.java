package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(
        name = "Response",
        description = "Schema to hold success response information"
)
public class ResponseDTO {

    @Schema(description = "Success status code")
    private HttpStatus status;

    @Schema(description = "Success status message")
    private String message;

    @Schema(description = "Timestamp when response generated")
    private LocalDateTime timestamp;
}
