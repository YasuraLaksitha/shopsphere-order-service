package com.shopsphere.order_service.services;

import com.stripe.model.Event;

public interface IEventService {

    /**
     *
     */
    void handlePaymentEvent(final Event event);
}
