package com.greenbasket.core.service;

import com.greenbasket.core.domain.Category;
import com.greenbasket.core.domain.Comment;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.repository.CategoryInterface;
import com.greenbasket.core.repository.CommentInterface;
import com.greenbasket.core.repository.ProductInterface;
import com.greenbasket.core.util.AppException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.Comparator;
import java.util.List;

import static com.greenbasket.core.util.Validators.requireNonBlank;
import static com.greenbasket.core.util.Validators.requirePositive;

public class ProductService {
    private final ProductInterface productRepository;
    private final CategoryInterface categoryRepository;
    private final CommentInterface commentRepository;

    public ProductService(ProductInterface productRepository, CategoryInterface categoryRepository, CommentInterface commentRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
    }

    public enum Direction {
        ASC, // по возрастанию
        DESC // по убыванию
    }

    private Product requireProduct(Long id) throws InstanceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("продукт не найден"));
    }

    private Category requireCategory(Long id
    ) throws InstanceNotFoundException {

        return categoryRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("категория не найдена"));
    }


    private List<Product> requireProductsByCategory(Long categoryId
    ) throws InstanceNotFoundException {

        if (productRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new InstanceNotFoundException("продукты не найдены");
        }

        return productRepository.findByCategoryId(categoryId);
    }

    private List<Product> sortProducts(Long categoryId, Comparator<Product> comparator, CatalogService.Direction direction
    ) throws InstanceNotFoundException {

        Comparator<Product> d = direction == CatalogService.Direction.ASC
                ? comparator
                : comparator.reversed();

        return requireProductsByCategory(categoryId).stream()
                .sorted(d)
                .toList();
    }

    public Product addProductInCategory(String productName, int price, String brand, Category category
    ) throws InstanceAlreadyExistsException, InstanceNotFoundException {

        String safeProductName = requireNonBlank(productName, "название продукта")
                .toLowerCase().trim();
        // TODO: ограничение на длину
        int safePrice = requirePositive(price, "цена");
        String safeBrand = requireNonBlank(brand, "название бренда"
        ).toLowerCase().trim();

        if (!categoryRepository.isCategoryExists(category.getId())) {
            throw new InstanceNotFoundException("категория не найдена");
        }

        if (productRepository.isProductExistsByFields(productName, brand)) {
            throw new InstanceAlreadyExistsException("продукт уже добавлен");
        }

        Product newProduct = Product.builder()
                .name(safeProductName)
                .brand(safeBrand)
                .category(category)
                .price(safePrice)
                .build();
        Product p = productRepository.save(newProduct); // добавляем в список новый продукт
        return p;
    }


    public void removeProduct(Long productId
    ) throws InstanceNotFoundException {
// найти продукт, проверить существует ли он, удалить его
        requireProduct(productId);           // кинет ошибку, если нет
        productRepository.remove(productId);
    }


    public Product findProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }


    public void moveProductToCategory(Long productId, Long categoryId
    ) throws InstanceNotFoundException {
        requireProduct(productId).setCategory(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException("категория не найдена")));
    }


    public void setProductPrice(Long productId, int price
    ) throws InstanceNotFoundException {
        requireProduct(productId).setPrice(price);
    }


    public List<Product> getProductsByCategoryId(Long id) throws InstanceNotFoundException {
        requireCategory(id);
        return productRepository.findByCategoryId(id);
    }


    public List<Product> getAllProducts(
    ) throws InstanceNotFoundException {
        if (productRepository.findAll().isEmpty()) {
            throw new InstanceNotFoundException("нет ни одного продукта");
        }
        return productRepository.findAll();
    }


    public void applyDiscountToProduct(Long productId, int discount
    ) throws InstanceNotFoundException {
        requireProduct(productId).setDiscount(discount);
    }


//    public void updateProductRating(Long productId) throws InstanceNotFoundException {
//        // find all comments, get scopes, find average, set average rating to product
//        Product product = requireProduct(productId);
//
//        double rating = commentRepository.findByProductId(productId).stream()
//                .mapToInt(Comment::getScore)
//                .average().orElse(0.0);
//
//        product.setAverageRating((rating));
//    }

// Comparator<Message> comparator = (o1, o2) -> o1.getId().compareTo(o2.getId());

    public List<Product> getProductsSortedByPrice(Long categoryId, CatalogService.Direction direction
    ) throws InstanceNotFoundException {
        return sortProducts(categoryId, Comparator.comparingInt(Product::getPrice), direction);
    }


    public List<Product> getProductsSortedByRating(Long categoryId, CatalogService.Direction direction
    ) throws InstanceNotFoundException {
        return sortProducts(categoryId, Comparator.comparingDouble(Product::getAverageRating), direction);
    }


    public List<Product> getProductsSortedByDiscount(Long categoryId, CatalogService.Direction direction
    ) throws InstanceNotFoundException {
        return sortProducts(categoryId, Comparator.comparingDouble(Product::getDiscount), direction);
    }


    public List<Product> getProductsSortedByName(Long categoryId, CatalogService.Direction direction
    ) throws InstanceNotFoundException {
        return sortProducts(categoryId, Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER), direction);
    }


}
