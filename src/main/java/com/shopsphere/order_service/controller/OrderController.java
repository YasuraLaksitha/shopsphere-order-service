package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.services.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(
        name = "Order controller",
        description = "REST APIs for perform CRUD operations on user orders"
)

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final IOrderService orderService;

    @Operation(
            summary = "Place order",
            description = "REST API to place a new order"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status Code CREATED"
    )
    @ApiResponse(
            responseCode = "409",
            description = "HTTP Status Code CONFLICT",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PostMapping("/user/create")
    public ResponseEntity<Object> placeOrder(@Valid @RequestBody final OrderRequestDTO orderRequestDTO) {
        final StripeResponseDTO stripeResponseDTO = orderService.placeOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stripeResponseDTO);
    }

    @Operation(
            summary = "Filter user orders",
            description = "REST API to filter user orders"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @GetMapping("/user/filter")
    public ResponseEntity<PaginationResponseDTO<OrderRequestDTO>> filterUserOrders(
            @RequestHeader(name = "X-User-Id") final String userId,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.PAGE_NUMBER) Integer page,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.PAGE_COUNT) Integer count,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.ORDER_SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.ORDER_SORT_ORDER) String sortOrder,
            @RequestParam(required = false) String orderDate
    ) {
        final PaginationResponseDTO<OrderRequestDTO> responseDTO = orderService.filterOrders(sortBy, sortOrder, page, count, orderDate,userId);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Remove order",
            description = "REST API to remove order by order code"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status Code OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "HTTP Status Code NOT_FOUND",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @ApiResponse(
            responseCode = "417",
            description = "HTTP Status Code EXCEPTION_FAILED",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
    )
    @PutMapping("user/remove")
    public ResponseEntity<ResponseDTO> removeByCode(@RequestParam String orderCode) {
        return orderService.deleteByOrderCode(orderCode) ?
                ResponseEntity.ok(ResponseDTO.builder()
                        .status(HttpStatus.OK)
                        .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                        .timestamp(LocalDateTime.now())
                        .build()) :
                ResponseEntity.badRequest()
                        .body(ResponseDTO.builder()
                                .status(HttpStatus.EXPECTATION_FAILED)
                                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_417)
                                .timestamp(LocalDateTime.now())
                                .build());
    }
}
