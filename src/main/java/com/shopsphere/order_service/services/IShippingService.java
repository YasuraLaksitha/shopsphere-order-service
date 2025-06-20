package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.ShippingRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;

public interface IShippingService {

    ShippingResponseDTO sendShippingRequest(ShippingRequestDTO shippingRequestDTO);
}
