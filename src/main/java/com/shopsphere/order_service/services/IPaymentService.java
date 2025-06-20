package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.CheckoutRequestDTO;
import com.shopsphere.order_service.dto.StripeResponseDTO;
import com.shopsphere.order_service.utils.PaymentMethod;

import java.util.concurrent.CompletableFuture;

public interface IPaymentService {

    CompletableFuture<StripeResponseDTO> createSessionURL(CheckoutRequestDTO checkoutRequestDTO, PaymentMethod method);
}
