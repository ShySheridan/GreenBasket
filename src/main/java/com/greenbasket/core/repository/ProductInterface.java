package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Category;
import com.greenbasket.core.domain.Product;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Optional;

/*
Контракт доступа к данным. Чётко описывает, какие запросы нужны сервисам.

Что внутри:

find(String q), get(long id), insert(entity), update(entity), delete(id).

Что изучить: Репозиторий-паттерн, границы слоя данных.

Что можно дописать:

Методы с пагинацией (find(q, offset, limit)).

Методы для связей (присвоение категорий продукту).
 */


public interface ProductInterface {

    // Создать или обновить товар
    Product save(Product product) throws InstanceAlreadyExistsException;

    void remove(Long id);

    Optional<Product> findByName(String name);

    Optional<Product> findById(Long id);

    List<Product> findByCategoryId(Long categoryId); // список всех продуктов по категории

    List<Product> findAll();

    int countByCategoryId(Long categoryId); // количество продуктов в категории

    boolean isProductExistsByFields(String name, String brand);
}
