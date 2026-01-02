package com.greenbasket.core.domain;

import com.greenbasket.core.exception.AppException;
import lombok.experimental.SuperBuilder;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Операции внутри одной корзины + проверки количества
 */
@Setter
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
public class Cart extends BaseEntity {
    private final Long userId;
    // ключ — productId, значение — позиция в корзине
    private Map<Long, CartItem> items = new LinkedHashMap<>();

    public record CartView(
            long cartId,
            List<CartItem.CartItemView> items,
            int totalPrice
    ) {}

    public void addItem(Long productId, int qty) {
        if (qty <= 0) throw new AppException("Количество должно быть > 0");

        CartItem item = items.get(productId);
        if (item == null) {
            items.put(productId, new CartItem(productId, qty));
        } else {
            item.increase(qty);
        }
    }


    public void removeItem(Long productId) {
        if (!items.containsKey(productId)) {
            throw new AppException("Товара нет в корзине");
        }
        items.remove(productId);
    }


    public void clear(){
        if (items.isEmpty()) {
            throw new AppException("Корзина уже пуста");
        }
        items.clear();
    }


    /**
     * Изменить состояние корзины как “черновика заказа”
     */
    public void changeQuantity(long productId, int qty) {
        if (!items.containsKey(productId)) {
            throw new AppException("Товара нет в корзине. Сначала добавьте товар в корзину (add_item)");
        }

        if (qty < 0) {
            throw new IllegalArgumentException("Количество товара не может быть < 0 ");
        }

        if (qty == 0) {
            items.remove(productId);
        } else {
            CartItem item = items.get(productId);
            item.setQuantity(qty);
        }
    }


    public List<CartItem> items() {
        return List.copyOf(items.values()); // immutable snapshot
    }

//    public List<CartItem> getItems() {
//        // лист продукт id
//        return new ArrayList<>(items.keySet());
//    }
}
