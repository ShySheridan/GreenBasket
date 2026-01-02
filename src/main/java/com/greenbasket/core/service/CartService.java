package com.greenbasket.core.service;
import com.greenbasket.core.domain.*;
import com.greenbasket.core.exception.*;
import com.greenbasket.core.repository.CartInterface;
import com.greenbasket.core.repository.OrderInterface;
import com.greenbasket.core.repository.ProductInterface;
import com.greenbasket.core.repository.UserInterface;
import lombok.experimental.SuperBuilder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Setter
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
public class CartService {
    private final CartInterface cartRepository;
    private final ProductInterface productRepository;
    private final UserInterface userRepository;
    private final OrderInterface orderRepository;

    public CartService(ProductInterface productRepository, CartInterface cartRepository, UserInterface userRepository,
                         OrderInterface orderRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

//    Получить текущую корзину или создать
    public Cart getOrCreateCart(Long userId){
        Optional<Cart> opt = cartRepository.findByUserId(userId);
        if (opt.isPresent()) {
            return opt.get();
        }

        Cart cart = new Cart(userId);
        cartRepository.save(cart);
        return cart;
    }


    public void addToCart(long userId, long productId, int qty) {
        Cart cart = getOrCreateCart(userId);

        productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Продукт не найден")
        );
//        inventory.ensureAvailable(productId, qty); // иначе BusinessException // TODO check qty

        cart.addItem(productId, qty);
        cartRepository.save(cart);
    }


    //    Показать корзину (лучше вернуть DTO/представление, а не доменный объект наружу)
    public Cart.CartView viewCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Корзина не найдена")
        );

        List<CartItem.CartItemView> items = new ArrayList<>();
        int totalSum = 0;

        for (CartItem cartItem : cart.items()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(
                    () -> new NotFoundException("Продукт не найден")
            );
            int unitPrice = discountedUnitPrice(product);
            int lineTotal = unitPrice * cartItem.getQuantity();
            totalSum += lineTotal;

            items.add(new CartItem.CartItemView(product.getId(), product.getName(), unitPrice, cartItem.getQuantity()));
        }
        return new Cart.CartView(cart.getId(), items, totalSum);
    }


    public void changeQuantity(Long userId, Long productId, int qty){
        // если количество == 0 удалить товар из корзины
        // TODO проверить есть ли товары на складе
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Корзина не найдена")
        );
        cart.changeQuantity(productId, qty);
        cartRepository.save(cart);
    }


    public void removeItem(Long userId, Long productId){
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Корзина не найдена")
        );
        productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Продукт не найден"));
        cart.removeItem(productId);
        cartRepository.save(cart);
    }


    public void clear(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Корзина не найдена"));

        cart.clear();
        cartRepository.save(cart);
    }

//    оформить заказ
    public Order checkout(Long userId){
        // проверить что корзина существует и не пустая
        // сформировать заказ и передать orderService
        // очистить корзину
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Корзина не найдена")
        );

        if (cart.items().isEmpty()) {
            throw new BusinessException("Корзина пустая");
        }

        int price = calculatePrice(userId);
        Order order = Order.create(userId, price);
        Order savedOrder = orderRepository.save(order);

        cart.clear();
        cartRepository.save(cart);
        return savedOrder;
    }


    //    итого/скидки/доставка
    /**
     * Считает общую стоимость корзины пользователя по текущим ценам товаров.
     */
    public int calculatePrice(Long userId){
        // check product discount
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Корзина не найдена"));

        int total = 0;
        for (CartItem item : cart.items()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Продукт не найден"));

            total += discountedUnitPrice(product) * item.getQuantity();
        }

        return total;
    }


    private int discountedUnitPrice(Product product) {
        int price = product.getPrice();
        int discount = product.getDiscount(); // ожидаем 0..100

        if (discount <= 0) return price;
        if (discount >= 100) return 0;

        return price * (100 - discount) / 100;
    }
}
