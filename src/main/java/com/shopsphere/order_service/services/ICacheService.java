package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.ShippingRequestDTO;

public interface ICacheService {

    /**
     *
     * @param shippingRequestDTO - shipping request
     * @param orderId            - orderID
     * @param userId             - user id
     */
    void saveShippingDetailsIntoCache(final ShippingRequestDTO shippingRequestDTO, final Long orderId, final String userId);

    /**
     *
     * @param orderId - orderId
     * @return - shippingRequest object
     */
    ShippingRequestDTO retrieveShippingRequestByOrderId(final Long orderId);
}
