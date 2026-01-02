package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Cart;
import com.greenbasket.core.repository.CartInterface;
import com.greenbasket.core.util.IdGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CartInMemory implements CartInterface {
    private final Map<Long, Cart> carts = new HashMap<>();
    private final IdGenerator idGenerator;

    public CartInMemory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return Optional.empty();
    }

    @Override
    public Cart save(Cart bucket) {
        return null;
    }

    @Override
    public void removeByUserId(Long userId) {

    }

    @Override
    public void removeAll() {

    }
}
