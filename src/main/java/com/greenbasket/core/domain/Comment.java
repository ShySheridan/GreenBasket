package com.greenbasket.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
//@EqualsAndHashCode(callSuper = true)  // Учитываем поля родительского класса
@ToString
@NoArgsConstructor(force = true)
@SuperBuilder
public class Comment extends BaseEntity {
    @Setter
    private String text; //TODO: validate the comment size

    @Setter(AccessLevel.NONE)
    private final User user;

    @Setter(AccessLevel.NONE)
    private final Product product;

//    @lombok.Setter(AccessLevel.NONE)
//    private final Date created;

    @Setter
    private int score;  // TODO заменить на enum

}
