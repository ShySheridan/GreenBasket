package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderInterface {
    public Order save(Order order);

    public void remove(Long id);

    public Optional<Order> findById(Long id);

    public List<Order> findAll();

    public List<Order> findByUserId(Long userId);

    public List<Order> findByStatus(Order.OrderStatus status);

}
