package com.greenbasket.core.service;

// Всё, что связано с товарами, категориями, сортировками, фильтрами.
// Можно ли менять, как менять, что ещё при этом происходит


import com.greenbasket.core.domain.Category;
import com.greenbasket.core.domain.Comment;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.CategoryInterface;
import com.greenbasket.core.repository.CommentInterface;
import com.greenbasket.core.repository.ProductInterface;
import com.greenbasket.core.repository.UserInterface;
import com.greenbasket.core.util.AppException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.Comparator;
import java.util.List;

import static com.greenbasket.core.util.Validators.*;

public class CatalogService {
    private final ProductInterface productRepository;
    private final CategoryInterface categoryRepository;
    private final CommentInterface commentRepository;
    private final UserInterface userRepository;

    public enum Direction {
        ASC, // по возрастанию
        DESC // по убыванию
    }

    public CatalogService(ProductInterface productRepository, CategoryInterface categoryRepository, CommentInterface commentRepository, UserInterface userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }


    private Product requireProduct(Long id) throws InstanceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("продукт не найден"));
    }

    private Comment requireComment(Long commentId) throws InstanceNotFoundException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new InstanceNotFoundException("комментарий не найден"));
    }

    private User requireUser(Long id
    ) throws InstanceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("пользователь не найден"));
    }

    private Category requireCategory(Long id
    ) throws InstanceNotFoundException {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("категория не найдена"));
    }

// В публичных методах сервиса:
//никогда не возвращать null;
//либо нормальное значение (Product, List<Product>),
//либо Optional<Product>, если «может не быть».
//
//Использовать чёткое разделение:
//Методы findXxx → ничего не кидают, если не нашли; возвращают Optional или пустой список.
//Методы getXxx / requireXxx → гарантируют, что нашли, и кидают исключение, если нет.

    // TODO сортировка по сроку годности, в зависимость от количества
    //  рейтинг / цена, количество отзывов
    //  только товары со скидкой, сортировка по рейтингу, средняя цена бренда, количество заказов
    //  “Быстрая доставка”: сначала товары, доступные в нужном магазине/складе, потом по времени доставки, потом по цене.
}