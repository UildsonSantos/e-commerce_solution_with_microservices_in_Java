package com.santos.ecommerce.payment.service;

import com.antonio.ecommerce.checkout.event.CheckoutCreatedEvent;
import com.santos.ecommerce.payment.entity.PaymentEntity;

import java.util.Optional;

public interface PaymentService {

    Optional<PaymentEntity> create(CheckoutCreatedEvent checkoutCreatedEvent);
}
