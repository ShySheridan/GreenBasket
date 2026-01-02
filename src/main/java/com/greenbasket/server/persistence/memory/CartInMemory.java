package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Cart;
import com.greenbasket.core.repository.CartInterface;

import java.util.Optional;

public class CartInMemory implements CartInterface {
    public CartInMemory() {}

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
