package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Category;
import com.greenbasket.core.repository.CategoryInterface;
import com.greenbasket.core.util.IdGenerator;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

public class CategoryInMemory implements CategoryInterface {
    private final Map<Long, Category> storage = new HashMap<>();
    private final IdGenerator idGenerator;

    public CategoryInMemory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }


    @Override
    public Category save(Category category) throws InstanceAlreadyExistsException {
        if (category.getId() == null) {
            category.assignId(idGenerator.generateId());
        }
        storage.put(category.getId(), category);
        return category;
    }

    @Override
    public void remove(Long id){
        storage.remove(id);
    }


    // если категория есть вернётся Optional.of(category) иначе Optional.empty()
    @Override
    public Optional<Category> findById(Long id){
        return Optional.ofNullable(storage.get(id)); // Optional.ofNullable может дать ноль, Optional.of() строго проверяет что значение не null
    }

    @Override
    public List<Category> findAll(){
        List<Category> categories = new ArrayList<>();
        for (Category category : storage.values()) {
            categories.add(category);
        }
        return categories;
    }


    @Override
    public Optional<Category> findByName(String name){
//        if (name == null) {
//            return Optional.empty();
//        }

        for (Category category : storage.values()) {
            if (category.getName().equals(name)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }


    @Override
    public boolean isCategoryExists(Long id){
        return storage.containsKey(id);
    }

//    private boolean isCategoryUnique(String categoryName) { // имя категории должно быть уникально
//        String normalized = categoryName.toLowerCase().trim();
//
//        return categoryRepository.findAll().stream()
//                .map(Category::getName)
//                .filter(Objects::nonNull) // Все null выбрасываются из стрима.
//                .map(name -> name.trim().toLowerCase())
//                .noneMatch(p -> p.equals(normalized));
//    }
}
