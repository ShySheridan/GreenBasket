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
public class CartService extends BaseEntity {
    private final Cart cart;
    private final Product product;
    private final CartInterface cartRepository;
    private final ProductInterface productRepository;
    private final UserInterface userRepository;
    private final OrderInterface orderRepository;

    public CartService(ProductInterface productRepository, CartInterface cartRepository, UserInterface userRepository,
                         Cart cart, Product product, OrderInterface orderRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cart = cart;
        this.product = product;
    }

//    Получить текущую корзину или создать
    public Cart getOrCreateCart(Long userId){
        Optional<Cart> opt = cartRepository.findById(userId);
        if (opt.isPresent()) {
            return opt.get();
        }

        Cart cart = new Cart();
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
        int totalsSum = 0;

        for (CartItem cartItem : cart.getItems().values()) {
            Product product = productRepository.findById(cartItem.getProductId()).orElseThrow(
                    () -> new NotFoundException("Продукт не найден")
            );
            int price = product.getPrice() + cartItem.getQuantity();
            totalsSum += price;

            items.add(new CartItem.CartItemView(product.getId(), product.getName(), price, cartItem.getQuantity()));
        }
        return new Cart.CartView(cart.getId(), items, totalsSum);
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


    public void clear(Long userId){
        Cart cart = getOrCreateCart(userId);
        if (!cart.getItems().isEmpty()) {
            cart.getItems().clear();
        }
    }

//    оформить заказ
    public Order checkout(Long userId){
        // проверить что корзина существует и не пустая
        // сформировать заказ и передать orderService
        // очистить корзину
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new NotFoundException("Корзина не найдена")
        );

        if (cart.getItems().isEmpty()) {
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
            total += item.getPrice();
        }

        return total;
    }
}
