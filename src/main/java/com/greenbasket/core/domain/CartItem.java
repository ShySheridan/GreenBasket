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
    private final Product product;


    public record CartItemView(
            Long productId,
            String name,
            int price,
            int quantity
    ) {}


    public CartItem(Long productId, int quantity, Product product) {
        this.product = product;

        if (productId == null) {
            throw new IllegalArgumentException("productId не должно быть null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.productId = productId;
        this.quantity = quantity;
    }


    protected void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.quantity = newQuantity;
    }

    protected void increase(int delta) {
        int result = this.quantity + delta;
        if (result <= 0) {
            throw new IllegalArgumentException("количество продукта в корзине должно быть > 0");
        }
        this.quantity = result;
    }

    protected void decrease(int delta) {
        int result = this.quantity - delta;

    }

    public int getPrice(){
        assert product != null;
        int totalPrice = product.getPrice() * quantity;
        if (product.getDiscount() != 0){
            totalPrice = totalPrice * product.getDiscount() / 100;
        }
        return totalPrice;
    }



}
