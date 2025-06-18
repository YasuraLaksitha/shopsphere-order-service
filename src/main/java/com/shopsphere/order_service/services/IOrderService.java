package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.PaginationResponseDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;

public interface IOrderService {

    /**
     *
     * @param orderRequest - order request
     * @return - response from payment provider
     *
     */
    <T> T placeOrder(final OrderRequestDTO orderRequest);

    /**
     *
     * @param orderId - orderId
     * @param userId - user id
     */
    ShippingResponseDTO handleShippingRequest(final Long orderId,final String userId);

    /**
     *
     * @param orderId     - orderID
     * @param orderStatus - order status
     */
    void updateOrderStatus(Long orderId, String orderStatus);

    /**
     *
     * @param orderId - orderId
     */
    void sendProductUpdateRequest(final Long orderId);

    /**
     *
     * @param sortBy       - order field name
     * @param sortOrder    - asc,desc
     * @param pageNumber   - current page
     * @param pageSize - data count
     * @return pagination response
     */
    PaginationResponseDTO<OrderRequestDTO> filterOrders(final String sortBy, final String sortOrder, final int pageNumber, final int pageSize, final String orderDate);

    /**
     *
     * @param orderCode - order code
     * @return - true if deleted
     */
    boolean deleteByOrderCode(final String orderCode);
}
