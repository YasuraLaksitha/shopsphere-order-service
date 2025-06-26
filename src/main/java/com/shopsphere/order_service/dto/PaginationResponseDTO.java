package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(
        name = "Pagination Response",
        description = "Schema to hold order pagination information"
)
public class PaginationResponseDTO<T> {

    @Schema(description = "Field to sort", example = "orderDate")
    private String sortBy;

    @Schema(description = "Sort order", example = "ASC/DESC")
    private String sortOrder;

    @Schema(description = "Page number ", example = "2")
    private String pageNumber;

    @Schema(description = "Page size", example = "ASC")
    private String pageSize;

    @Schema(description = "Check weather if it is the last page", example = "true")
    private boolean isLastPage;

    @Schema(description = "Data that user requested")
    private List<T> data;
}

