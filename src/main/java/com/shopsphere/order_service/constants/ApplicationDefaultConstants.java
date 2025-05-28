package com.shopsphere.order_service.constants;

import com.shopsphere.order_service.utils.OrderStatus;

public final class ApplicationDefaultConstants {

    private ApplicationDefaultConstants() {}

    public static final String RESPONSE_MESSAGE_201 = "Resource created successfully";

    public static final OrderStatus ORDER_STATUS_INIT = OrderStatus.INITIATED;

    public static final Double ORDER_PRICE_INIT = 0.00 ;
}
