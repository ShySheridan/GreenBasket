package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Cart;

import java.util.Optional;

/**
 * Репозиторий для корзин пользователей
 * Предполагаем, что у каждого пользователя одна "текущая" корзина
 */
public interface CartInterface {
    public Optional<Cart> findById(Long id);

    public Optional<Cart> findByUserId(Long userId);

    /**
     * Сохраняет (создаёт или обновляет) корзину пользователя в хранилище
     */
    public Cart save(Cart bucket);

    /**
     * Удаляет корзину пользователя в хранилище.
     */
    public void removeByUserId(Long userId);

    /**
     * Опционально: очистка всех корзин
     */
    public void removeAll();
}
