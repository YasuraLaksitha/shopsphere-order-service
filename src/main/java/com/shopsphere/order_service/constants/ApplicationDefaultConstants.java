package com.shopsphere.order_service.constants;

import com.shopsphere.order_service.utils.OrderStatus;

public final class ApplicationDefaultConstants {

    public static final String  ORDER_SORT_ORDER = "ASC";

    public static final OrderStatus ORDER_STATUS_INIT = OrderStatus.INITIATED;

    public static final Double ORDER_PRICE_INIT = 0.00 ;

    public static final String PAGE_NUMBER = "0";

    public static final String PAGE_COUNT = "50";

    public static final String ORDER_SORT_BY = "createdAt";

    private ApplicationDefaultConstants() {}
}
