package com.shopsphere.order_service.services.client;

import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "shipping")
public interface ShippingFeighClient {

    @PostMapping("/api/shipping/create")
    ResponseEntity<ShippingResponseDTO> createShippingObject(@RequestBody final ShippingRequestDTO shippingRequest);

}