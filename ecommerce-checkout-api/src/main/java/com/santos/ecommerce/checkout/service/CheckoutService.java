package com.santos.ecommerce.checkout.service;

import com.santos.ecommerce.checkout.entity.CheckoutEntity;
import com.santos.ecommerce.checkout.resource.checkout.CheckoutRequest;

import java.util.Optional;

public interface CheckoutService {

    Optional<CheckoutEntity> create(CheckoutRequest checkoutRequest);

    Optional<CheckoutEntity> updateStatus(String checkoutCode, CheckoutEntity.Status status);
}
