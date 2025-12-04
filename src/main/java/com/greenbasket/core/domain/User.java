package com.greenbasket.core.domain;
//import jakarta.persistence.*;

import lombok.experimental.SuperBuilder;
import lombok.*;


import java.util.List;

//@Entity.
//@Table(name = "users")
@Setter
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    private String username;
//    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    private List<Order> orders;
    private String email;
    private String phone;

//    @lombok.Setter private List<Product> wishlist;
    // TODO: отслеживать все комментарии этого юзера List<Comment> comments

}
