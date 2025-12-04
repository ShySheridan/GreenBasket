package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Delivery;

import java.util.List;
import java.util.Optional;

public interface DeliveryInterface {

    Delivery save(Delivery delivery);

    void remove(Long id);

    Optional<Delivery> findById(Long id);

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findAll();

}
