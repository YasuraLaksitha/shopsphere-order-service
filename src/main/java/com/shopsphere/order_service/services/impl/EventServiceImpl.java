package com.shopsphere.order_service.services.impl;

import com.shopsphere.order_service.entities.OrderEntity;
import com.shopsphere.order_service.exceptions.ResourceNotFoundException;
import com.shopsphere.order_service.repositories.OrderRepository;
import com.shopsphere.order_service.services.IEventService;
import com.shopsphere.order_service.utils.OrderStatus;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final OrderRepository orderRepository;

    @Override
    public void handlePaymentEvent(Event event) {

        event.getDataObjectDeserializer().getObject().ifPresent(stripeObject -> {

            if (stripeObject instanceof Session session) {
                final String orderId = session.getMetadata().get("orderId");

                switch (event.getType()) {
                    case "checkout.session.completed" -> {
                        final OrderEntity orderEntity = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(
                                () -> new ResourceNotFoundException("Order", "id", orderId)
                        );

                        orderEntity.setOrderStatus(OrderStatus.PAID);
                        orderRepository.save(orderEntity);
                    }

                    case "payment_intent.payment_failed" ,
                         "checkout.session.async_payment_failed",
                         "charge.failed" -> {
                        final OrderEntity orderEntity = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(
                                () -> new ResourceNotFoundException("Order", "id", orderId)
                        );

                        orderEntity.setOrderStatus(OrderStatus.FAILED);
                        orderRepository.save(orderEntity);
                    }
                }
            }
        });
    }
}
