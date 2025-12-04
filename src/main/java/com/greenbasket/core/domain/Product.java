package com.greenbasket.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;


// не используем @Data для контроля сеттеров
@Getter
@NoArgsConstructor(force = true)
@ToString
//@EqualsAndHashCode(callSuper = true)  // Учитываем поля родительского класса
@SuperBuilder
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
}
