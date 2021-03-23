package com.santos.ecommerce.checkout.listener;

import com.santos.ecommerce.checkout.entity.CheckoutEntity;
import com.santos.ecommerce.checkout.service.CheckoutService;
import com.santos.ecommerce.checkout.streaming.PaymentPaidSink;
import com.santos.ecommerce.payment.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPaidListener {

    private final CheckoutService checkoutService;

    @StreamListener(PaymentPaidSink.INPUT)
    public void handler(PaymentCreatedEvent paymentCreatedEvent) {
        checkoutService.updateStatus(paymentCreatedEvent.getCheckoutCode().toString(), CheckoutEntity.Status.APPROVED);
    }
}
