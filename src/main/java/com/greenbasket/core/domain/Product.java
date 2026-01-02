package com.greenbasket.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;


// не используем @Data для контроля сеттеров
@Getter
@NoArgsConstructor(force = true)
@ToString
@Builder
public class Product extends BaseEntity {

    @Setter(AccessLevel.NONE) // помечаем, что поле неизменяемо
    private final String name;

    @Setter(AccessLevel.NONE)
    private final String brand;

    @Setter
    private int price;

    @Setter
    private Category category; // TODO make category final

    @Setter
    private double averageRating = 0.0;

    @Setter
    private int discount = 0;

    public static class ProductBuilder {
        public Product build() {
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("название продукта не задано");
            if (brand == null || brand.trim().isEmpty()) throw new IllegalArgumentException("название бренда не задано");
            if (price <= 0) throw new IllegalArgumentException("цена должна быть > 0");
            if (category == null) throw new IllegalArgumentException("категория не задана");

            // нормализация
            this.name = name.trim().toLowerCase();
            this.brand = brand.trim().toLowerCase();

            return new Product(this.name, this.brand, this.price, this.category, averageRating, discount);
        }
    }


    private Product(String name, String brand, int price, Category category, double averageRating, int discount) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.category = category;
        this.averageRating = averageRating;
        this.discount = discount;
    }

}
