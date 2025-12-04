package com.greenbasket.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
//@EqualsAndHashCode(callSuper = true)  // Учитываем поля родительского класса
@ToString
@NoArgsConstructor(force = true)
@SuperBuilder
public class Category extends BaseEntity {

    @Setter(AccessLevel.NONE)
    private final String name;

//    @lombok.Setter(AccessLevel.NONE)
//    private final String categoryType;
//    private List<Category> subCategories;

    @Setter
    private int discount = 0;

}