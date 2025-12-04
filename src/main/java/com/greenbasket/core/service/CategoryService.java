package com.greenbasket.core.service;

import com.greenbasket.core.domain.Category;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.repository.CategoryInterface;
import com.greenbasket.core.repository.ProductInterface;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

import static com.greenbasket.core.util.Validators.requireInRange;

public class CategoryService {
    private final CategoryInterface categoryRepository;
    private final ProductInterface productRepository;

    public CategoryService(CategoryInterface categoryRepository, ProductInterface productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }


    private Category requireCategory(Long id
    ) throws InstanceNotFoundException {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("категория не найдена"));
    }


    private Product requireProduct(Long id) throws InstanceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("продукт не найден"));
    }


    public Category addCategory(String name) throws InstanceAlreadyExistsException {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new InstanceAlreadyExistsException("категория уже существует");
        }

        Category newCategory = Category.builder()
                .name(name).build();

        categoryRepository.save(newCategory);
        return newCategory;
    }


    public void deleteCategory(Long id) throws InstanceNotFoundException {
        requireCategory(id);

        for (Product product : productRepository.findByCategoryId(id)) {
            product.setCategory(null);
        }
        categoryRepository.remove(id);
    }



    public Category getCategoryById(Long id) throws InstanceNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("категория не найдена"));
    }



    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    // изменить скидку к товарам категории (со всеми твоими условиями);
    public void applyDiscountToCategory(Long categoryId, int discount) throws InstanceNotFoundException {
        // require category, apply discount
        // require products in category, if category have products apply discount to them
        int safeDiscount = requireInRange(discount, 1, 100, "скидка");
        requireCategory(categoryId).setDiscount(discount);

        List<Product> products = productRepository.findByCategoryId(categoryId);
        for (Product product : products) {
            product.setDiscount(safeDiscount);
        }
    }

//    public void deleteAllCategories() {
//        for (Category category : categoryRepository.findAll()) {
//
//            categoryRepository.remove(category.getId());
//        }
//    }

}
