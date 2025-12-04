package com.greenbasket.core.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
//@EqualsAndHashCode(callSuper = true)  // Учитываем поля родительского класса
@ToString
@NoArgsConstructor(force = true)
@SuperBuilder
public class Order extends BaseEntity {
    @Setter(AccessLevel.NONE)
    private final User user;

    @Setter
    private Comment comment;

    @Setter(AccessLevel.NONE)
    private final LocalDateTime orderTime;

    @Setter
    private double price;

    @Setter
    private List<Product> products;

    @Setter
    private OrderStatus status;

    @Setter
    private DeliveryType deliveryType;

//    @lombok.Setter
    private Delivery delivery;

// TODO добавить вес продуктов
// TODO добавить доставку или самовывоз

    public void setDelivery(Delivery delivery) {
        if (deliveryType == DeliveryType.DELIVERY) {
            this.delivery = delivery;
        } else {
            throw new IllegalStateException("Самовывоз не требует объекта доставки.");
        }
    }

    public enum OrderStatus {
        NEW, IN_PROGRESS, COMPLETED, CANCELLED;
    }

    public enum DeliveryType {
        DELIVERY, PICKUP  // Типы доставки
    }
}

