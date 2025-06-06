package com.shopsphere.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginationResponseDTO<T> {

    private String sortBy;

    private String sortOrder;

    private String pageNumber;

    private String pageSize;

    private boolean isLastPage;

    private List<T> data;
}

