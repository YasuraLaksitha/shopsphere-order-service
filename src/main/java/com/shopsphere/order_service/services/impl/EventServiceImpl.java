package com.shopsphere.order_service.services.impl;

import com.shopsphere.order_service.context.UserContext;
import com.shopsphere.order_service.services.IEventService;
import com.shopsphere.order_service.services.IOrderService;
import com.shopsphere.order_service.utils.OrderStatus;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements IEventService {

    private final IOrderService orderService;

    @Override
    public void handlePaymentEvent(Event event) {

        event.getDataObjectDeserializer().getObject().ifPresent(stripeObject -> {

            if (stripeObject instanceof Session session) {
                final long orderId = Long.parseLong(session.getMetadata().get("orderId"));

                switch (event.getType()) {
                    case "checkout.session.completed" -> {
                        final String userId = session.getMetadata().get("userId");
                        UserContext.set(userId);

                        orderService.updateOrderStatus(orderId, OrderStatus.PAID.name());
                        orderService.sendProductUpdateRequest(orderId);
                        log.info("Before Feign call - UserContext: {}", UserContext.get());

                        try {
                            orderService.handleShippingRequest(orderId);
                        } catch (RuntimeException e) {
                            orderService.updateOrderStatus(orderId, OrderStatus.SHIPPING_FAILED.name());
                            throw new RuntimeException(e);
                        }
                    }

                    case "payment_intent.payment_failed",
                         "checkout.session.async_payment_failed",
                         "charge.failed" -> orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_FAILED.name());
                }
            }
        });
    }
}
