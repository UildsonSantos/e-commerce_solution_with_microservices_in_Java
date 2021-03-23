package com.santos.ecommerce.checkout.service;

import com.santos.ecommerce.checkout.entity.CheckoutEntity;
import com.santos.ecommerce.checkout.entity.CheckoutItemEntity;
import com.santos.ecommerce.checkout.entity.ShippingEntity;
import com.santos.ecommerce.checkout.event.CheckoutCreatedEvent;
import com.santos.ecommerce.checkout.repository.CheckoutRepository;
import com.santos.ecommerce.checkout.resource.checkout.CheckoutRequest;
import com.santos.ecommerce.checkout.streaming.CheckoutCreatedSource;
import com.santos.ecommerce.checkout.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service // criar uma instancia do servico e fazer a injecao de dependecia 
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    //@Autowired nao usar, o correto e usar anotacao na classe (RequiredArgsConstructor), pois facilita na implementacao dos testes
    private final CheckoutRepository checkoutRepository;
    private final CheckoutCreatedSource checkoutCreatedSource;
    private final UUIDUtil uuidUtil;

    @Override
    public Optional<CheckoutEntity> create(CheckoutRequest checkoutRequest) {
        log.info("M=create, checkoutRequest={}", checkoutRequest);
        final CheckoutEntity checkoutEntity = CheckoutEntity.builder()
                .code(uuidUtil.createUUID().toString())
                .status(CheckoutEntity.Status.CREATED)
                .saveAddress(checkoutRequest.getSaveAddress())
                .saveInformation(checkoutRequest.getSaveInfo())
                .shipping(ShippingEntity.builder()
                        .address(checkoutRequest.getAddress())
                        .complement(checkoutRequest.getComplement())
                        .country(checkoutRequest.getCountry())
                        .state(checkoutRequest.getState())
                        .cep(checkoutRequest.getCep())
                        .build())
                .build();
        checkoutEntity.setItems(checkoutRequest.getProducts()
                .stream()
                .map(product -> CheckoutItemEntity.builder()
                .checkout(checkoutEntity)
                .product(product)
                .build())
                .collect(Collectors.toList()));
        final CheckoutEntity entity = checkoutRepository.save(checkoutEntity);
        final CheckoutCreatedEvent checkoutCreatedEvent = CheckoutCreatedEvent.newBuilder()
                .setCheckoutCode(entity.getCode())
                .setStatus(entity.getStatus().name())
                .build();
        checkoutCreatedSource.output().send(MessageBuilder.withPayload(checkoutCreatedEvent).build());
        return Optional.of(entity);
    }

    @Override
    public Optional<CheckoutEntity> updateStatus(String checkoutCode, CheckoutEntity.Status status) {
        final CheckoutEntity checkoutEntity = checkoutRepository.findByCode(checkoutCode).orElse(CheckoutEntity.builder().build());
        checkoutEntity.setStatus(CheckoutEntity.Status.APPROVED);
        return Optional.of(checkoutRepository.save(checkoutEntity));
    }
}
