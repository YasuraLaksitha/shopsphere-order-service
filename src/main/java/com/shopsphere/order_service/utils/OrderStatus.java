package com.shopsphere.order_service.utils;

public enum OrderStatus {

    INITIATED,

    PENDING,

    PAID,

    LABELED,

    SHIPPED,

    DELIVERED,

    CANCELLED,

    SHIPPING_FAILED,

    PAYMENT_FAILED,

    RETURNED,

    RETRY
}
