package com.greenbasket.core.repository;

import com.greenbasket.core.domain.WishList;

import java.util.List;
import java.util.Optional;

public interface WishListInterface {

    WishList saveWishList(WishList wishList);

    void removeWishList(Long id);

    List<WishList> findByUserId(Long userId);

    Optional<WishList> findById(Long id);

    List<WishList> findAll();
}
