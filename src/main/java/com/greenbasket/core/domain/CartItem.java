package com.greenbasket.core.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString
@NoArgsConstructor(force = true)
@SuperBuilder
public class CartItem {
    private final Long productId;
    private int quantity;

    public record CartItemView(
            Long productId,
            String name,
            int price,
            int quantity
    ) {}


    public CartItem(Long productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("productId не должно быть null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.productId = productId;
        this.quantity = quantity;
    }


    void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.quantity = newQuantity;
    }

    void increase(int delta) {
        int result = this.quantity + delta;
        if (result <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.quantity = result;
    }

    void decrease(int delta) {
        if (delta <= 0) throw new IllegalArgumentException("delta должно быть > 0");
        int result = this.quantity - delta;
        if (result <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.quantity = result;
    }

//    int getPrice(){
//        Product product =
//        int totalPrice = product.getPrice() * quantity;
//        if (product.getDiscount() != 0){
//            totalPrice = totalPrice * product.getDiscount() / 100;
//        }
//        return totalPrice;
//    }

}
