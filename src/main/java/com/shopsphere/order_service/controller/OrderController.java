package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.constants.ApplicationDefaultConstants;
import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.PaginationResponseDTO;
import com.shopsphere.order_service.dto.ResponseDTO;
import com.shopsphere.order_service.dto.StripeResponseDTO;
import com.shopsphere.order_service.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping("/user/filter")
    public ResponseEntity<PaginationResponseDTO<OrderRequestDTO>> fetch(
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.PAGE_NUMBER) Integer page,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.PAGE_COUNT) Integer count,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.ORDER_SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = ApplicationDefaultConstants.ORDER_SORT_ORDER) String sortOrder,
            @RequestParam(required = false) String orderDate
    ) {
        final PaginationResponseDTO<OrderRequestDTO> responseDTO = orderService.filterOrders(sortBy, sortOrder, page, count, orderDate);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("user/remove")
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
