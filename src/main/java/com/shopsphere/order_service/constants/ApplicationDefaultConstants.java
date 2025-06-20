package com.shopsphere.order_service.constants;

import com.shopsphere.order_service.utils.OrderStatus;

public final class ApplicationDefaultConstants {

    public static final String ORDER_SORT_ORDER = "ASC";

    public static final OrderStatus ORDER_STATUS_INIT = OrderStatus.INITIATED;

    public static final Double ORDER_PRICE_INIT = 0.00;

    public static final String PAGE_NUMBER = "0";

    public static final String PAGE_COUNT = "50";

    public static final String ORDER_SORT_BY = "createdAt";

    public static final String RESPONSE_MESSAGE_200 = "Process completed successfully";

    public static final String RESPONSE_MESSAGE_417 = "Something went wrong. Try again later";

    public static final String RESPONSE_MESSAGE_503 = "Service is temporary unavailable. try again later";

    public static final String RESPONSE_MESSAGE_201 = "Resource created successfully";

    private ApplicationDefaultConstants() {
    }
}
