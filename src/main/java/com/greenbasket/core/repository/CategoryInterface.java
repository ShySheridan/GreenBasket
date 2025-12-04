package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Category;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Optional;


public interface CategoryInterface {

    Category save(Category category) throws InstanceAlreadyExistsException;

    void remove(Long id);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    Optional<Category> findByName(String name);

    boolean isCategoryExists(Long id);
}
