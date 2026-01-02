package service;
import com.greenbasket.core.domain.*;
import com.greenbasket.core.repository.*;
import com.greenbasket.core.service.*;
import com.greenbasket.core.exception.AppException;
import com.greenbasket.core.util.IdGenerator;
import com.greenbasket.server.util.idGenerator.SimpleIdGenerator;
import com.greenbasket.server.persistence.memory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {
    private ProductService productService;
    private CategoryService categoryService;
    private CommentService commentService;
    private UserService userService;

    private ProductInterface productRepository;
    private CategoryInterface categoryRepository;
    private CommentInterface commentRepository;
    private UserInterface userRepository;

    private IdGenerator idGenerator = new SimpleIdGenerator();


    @BeforeEach
    void setUp() {
        categoryRepository = new CategoryInMemory(idGenerator);
        productRepository = new ProductInMemory(idGenerator);
        commentRepository = new CommentInMemory(idGenerator);
        userRepository = new UserInMemory(idGenerator);

        categoryService = new CategoryService(categoryRepository, productRepository);
        productService = new ProductService(productRepository, categoryRepository, commentRepository);
        commentService = new CommentService(commentRepository, productRepository, userRepository);
//        userService = new UserService();
    }

    @Test
    void add_product_success() throws InstanceAlreadyExistsException, InstanceNotFoundException {
        var category = categoryService.addCategory("Фрукты");
        var product = productService.addProductInCategory(
                "яблоко",
                100,
                "           Красная цена        ",
                category
        );
        assertNotNull(product, "productService.addProductInCategory вернул null");
        assertNotNull(product.getId(), "id продукта не был установлен репозиторием");
        assertEquals("яблоко", product.getName());
        assertEquals("красная цена", product.getBrand());
        assertEquals(100, product.getPrice());

        List<Product> products = productService.getProductsByCategoryId(category.getId());
        assertEquals(1, products.size());
        assertEquals("яблоко", products.get(0).getName());
    }


    @Test
    void add_product_when_category_not_found(){
        Category unknown = new Category();

        assertThrows(InstanceNotFoundException.class, () ->
                productService.addProductInCategory("Milk", 100, "Valio", unknown)
        );
    }


    @Test
    void add_product_when_categories_incorrect() throws InstanceAlreadyExistsException {
        Category dairy = categoryService.addCategory("dairy");
        // 1) Невалидная цена (отрицательная)
        AppException priceException = assertThrows(
                AppException.class,
                () -> productService.addProductInCategory(
                        "Milk",      // корректное имя
                        -10,         // НЕкорректная цена
                        "Valio",     // корректный бренд
                        dairy       // существующая категория
                )
        );
        assertTrue(priceException.getMessage().contains("цена"));

        // 2) Невалидный бренд (пустая строка)
        AppException brandException = assertThrows(
                AppException.class,
                () -> productService.addProductInCategory(
                        "Milk",      // корректное имя
                        100,         // корректная цена
                        "   ",       // НЕкорректный бренд (пустой после trim)
                        dairy       // существующая категория
                )
        );
        assertTrue(brandException.getMessage().contains("название бренда"));
    }


    @Test
    void add_product_when_product_is_already_exists() throws InstanceAlreadyExistsException, InstanceNotFoundException {
        var category = categoryService.addCategory("dairy");
        Product existing = Product.builder()
                .id(1L)
                .name("milk")
                .brand("valio")
                .category(category)
                .build();
        productRepository.save(existing);

        assertThrows(InstanceAlreadyExistsException.class, () ->
                productService.addProductInCategory("milk", 100, "valio", category)
        );
    }


    @Test
    void removeProduct_success() throws Exception {
        var category = categoryService.addCategory("dairy");
        Product p = Product.builder()
                .id(1L)
                .name("milk")
                .brand("valio")
                .category(category)
                .build();
        productRepository.save(p);

        assertEquals(1, productRepository.findAll().size());

        // when
        productService.removeProduct(1L);

        // then
        assertEquals(0, productRepository.findAll().size());
        assertNull(productService.findProductById(1L));
    }


    @Test
    void remove_product_when_not_found() throws InstanceNotFoundException {
        assertThrows(InstanceNotFoundException.class, () ->
                productService.removeProduct(999L)
        );
    }


    void find_product_by_id_when_found() throws InstanceAlreadyExistsException {
        Product p = Product.builder()
                .id(10L)
                .name("juice")
                .brand("rich")
                .category(categoryService.addCategory("ertyu"))
                .build();
        productRepository.save(p);

        Product result = productService.findProductById(10L);

        assertNotNull(result);
        assertEquals("juice", result.getName());
    }


    @Test
    void find_product_by_id_when_not_found() {
        Product result = productService.findProductById(123L);
        assertNull(result);
    }


    @Test
    void moveProductToCategory_success() throws Exception {
        var cat1 = categoryService.addCategory("cat1");
        var cat2 = categoryService.addCategory("cat2");
        // given
        Product p = Product.builder()
                .id(1L)
                .name("chocolate")
                .brand("milka")
                .category(cat1)   // пока лежит в "drinks"
                .build();
        productRepository.save(p);

        // when
        productService.moveProductToCategory(1L, cat2.getId());

        // then
        Product updated = productService.findProductById(1L);
        assertNotNull(updated);
        assertEquals(cat2, updated.getCategory());
    }


    @Test
    void move_product_to_category_when_category_not_found() throws InstanceNotFoundException, InstanceAlreadyExistsException {
        var cat1 = categoryService.addCategory("cat1");
        assertThrows(InstanceNotFoundException.class, () ->
                productService.moveProductToCategory(999L, cat1.getId())
        );
    }


    @Test
    void move_product_to_category_when_product_not_found() throws InstanceAlreadyExistsException {
        var drinks = categoryService.addCategory("drinks");
        Long missingProductId = 999L;
        Long existingCategoryId = drinks.getId(); // категория есть в setUp()

        // when + then
        assertThrows(InstanceNotFoundException.class,
                () -> productService.moveProductToCategory(missingProductId, existingCategoryId)
        );
    }

    @Test
    void set_product_price_when_product_not_found() {
        // given: продукта с таким id нет
        Long missingProductId = 999L;

        // when + then
        assertThrows(InstanceNotFoundException.class,
                () -> productService.setProductPrice(missingProductId, 123)
        );
    }



    @Test
    void set_product_price() throws Exception {
        var cat1 = categoryService.addCategory("cat1");
        Product p = Product.builder()
                .id(1L)
                .name("milk")
                .brand("valio")
                .category(cat1)
                .build();
        p.setPrice(100);
        productRepository.save(p);

        productService.setProductPrice(1L, 250);

        Product updated = productService.findProductById(1L);
        assertEquals(250, updated.getPrice());
    }


    @Test
    void get_products_by_category() throws Exception {
        var cat1 = categoryService.addCategory("cat1");
        var cat2 = categoryService.addCategory("cat2");
        Product p1 = Product.builder()
                .id(1L)
                .name("milk")
                .brand("valio")
                .category(cat1)
                .build();
        Product p2 = Product.builder()
                .id(2L)
                .name("juice")
                .brand("rich")
                .category(cat1)
                .build();
        Product p3 = Product.builder()
                .id(3L)
                .name("chocolate")
                .brand("milka")
                .category(cat2)
                .build();

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        List<Product> drinksProducts = productService.getProductsByCategoryId(cat1.getId());

        assertEquals(2, drinksProducts.size());
        assertTrue(drinksProducts.stream().allMatch(p -> p.getCategory().equals(cat1)));
    }


    @Test
    void get_products_by_category_when_category_not_found() {
        assertThrows(InstanceNotFoundException.class, () ->
                productService.getProductsByCategoryId(999L)
        );
    }


    @Test
    void getAllProducts_returns_all_when_not_empty() throws Exception {
        var cat1 = categoryService.addCategory("cat1");
        Product p1 = Product.builder()
                .id(1L)
                .name("milk")
                .brand("valio")
                .category(cat1)
                .build();
        Product p2 = Product.builder()
                .id(2L)
                .name("juice")
                .brand("rich")
                .category(cat1)
                .build();
        productRepository.save(p1);
        productRepository.save(p2);

        // when
        List<Product> all = productService.getAllProducts();

        // then
        assertEquals(2, all.size());
    }

    @Test
    void getAllProducts_throws_when_empty() {
        // given: репозиторий пустой

        // when + then
        assertThrows(InstanceNotFoundException.class, () ->
                productService.getAllProducts()
        );
    }


    @Test
    void getProductsSortedByPrice_asc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product cheap = Product.builder()
                .name("Cheap")
                .brand("B1")
                .category(drinks)
                .build();
        cheap.setPrice(50);

        Product middle = Product.builder()
                .name("Middle")
                .brand("B2")
                .category(drinks)
                .build();
        middle.setPrice(100);

        Product expensive = Product.builder()
                .name("Expensive")
                .brand("B3")
                .category(drinks)
                .build();
        expensive.setPrice(200);

        productRepository.save(cheap);
        productRepository.save(middle);
        productRepository.save(expensive);

        // when
        List<Product> result = productService.getProductsSortedByPrice(
                drinks.getId(),
                CatalogService.Direction.ASC
        );

        // then: проверяем порядок по именам
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        assertEquals(List.of("Cheap", "Middle", "Expensive"), names);
    }

    @Test
    void getProductsSortedByPrice_desc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product cheap = Product.builder()
                .name("Cheap")
                .brand("B1")
                .category(drinks)
                .build();
        cheap.setPrice(50);

        Product middle = Product.builder()
                .name("Middle")
                .brand("B2")
                .category(drinks)
                .build();
        middle.setPrice(100);

        Product expensive = Product.builder()
                .name("Expensive")
                .brand("B3")
                .category(drinks)
                .build();
        expensive.setPrice(200);

        productRepository.save(cheap);
        productRepository.save(middle);
        productRepository.save(expensive);

        // when
        List<Product> result = productService.getProductsSortedByPrice(
                drinks.getId(),
                CatalogService.Direction.DESC
        );

        // then
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        assertEquals(List.of("Expensive", "Middle", "Cheap"), names);
    }


    @Test
    void getProductsSortedByRating_desc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product low = Product.builder()
                .name("Low")
                .brand("B1")
                .category(drinks)
                .build();
        low.setAverageRating(2.0);

        Product mid = Product.builder()
                .name("Mid")
                .brand("B2")
                .category(drinks)
                .build();
        mid.setAverageRating(3.5);

        Product high = Product.builder()
                .name("High")
                .brand("B3")
                .category(drinks)
                .build();
        high.setAverageRating(5.0);

        productRepository.save(low);
        productRepository.save(mid);
        productRepository.save(high);

        // when
        List<Product> result = productService.getProductsSortedByRating(
                drinks.getId(),
                CatalogService.Direction.DESC
        );

        // then
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        assertEquals(List.of("High", "Mid", "Low"), names);
    }

    @Test
    void getProductsSortedByRating_asc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product low = Product.builder()
                .name("Low")
                .brand("B1")
                .category(drinks)
                .build();
        low.setAverageRating(2.0);

        Product mid = Product.builder()
                .name("Mid")
                .brand("B2")
                .category(drinks)
                .build();
        mid.setAverageRating(3.5);

        Product high = Product.builder()
                .name("High")
                .brand("B3")
                .category(drinks)
                .build();
        high.setAverageRating(5.0);

        productRepository.save(low);
        productRepository.save(mid);
        productRepository.save(high);

        // when
        List<Product> result = productService.getProductsSortedByRating(
                drinks.getId(),
                CatalogService.Direction.ASC
        );

        // then
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        assertEquals(List.of("Low", "Mid", "High"), names);
    }


    @Test
    void getProductsSortedByDiscount_asc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product p1 = Product.builder()
                .name("P1")
                .brand("B1")
                .category(drinks)
                .build();
        p1.setDiscount(5);

        Product p2 = Product.builder()
                .name("P2")
                .brand("B2")
                .category(drinks)
                .build();
        p2.setDiscount(10);

        Product p3 = Product.builder()
                .name("P3")
                .brand("B3")
                .category(drinks)
                .build();
        p3.setDiscount(20);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        // when
        List<Product> result = productService.getProductsSortedByDiscount(
                drinks.getId(),
                CatalogService.Direction.ASC
        );

        // then
        List<Integer> discounts = result.stream()
                .map(Product::getDiscount)
                .toList();

        assertEquals(List.of(5, 10, 20), discounts);
    }

    @Test
    void getProductsSortedByDiscount_desc() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product p1 = Product.builder()
                .name("P1")
                .brand("B1")
                .category(drinks)
                .build();
        p1.setDiscount(5);

        Product p2 = Product.builder()
                .name("P2")
                .brand("B2")
                .category(drinks)
                .build();
        p2.setDiscount(10);

        Product p3 = Product.builder()
                .name("P3")
                .brand("B3")
                .category(drinks)
                .build();
        p3.setDiscount(20);

        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        // when
        List<Product> result = productService.getProductsSortedByDiscount(
                drinks.getId(),
                CatalogService.Direction.DESC
        );

        // then
        List<Integer> discounts = result.stream()
                .map(Product::getDiscount)
                .toList();

        assertEquals(List.of(20, 10, 5), discounts);
    }


    @Test
    void getProductsSortedByName_asc_caseInsensitive() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product apple = Product.builder()
                .name("apple")
                .brand("B1")
                .category(drinks)
                .build();

        Product Banana = Product.builder()
                .name("Banana")
                .brand("B2")
                .category(drinks)
                .build();

        Product cherry = Product.builder()
                .name("cherry")
                .brand("B3")
                .category(drinks)
                .build();

        productRepository.save(apple);
        productRepository.save(Banana);
        productRepository.save(cherry);

        // when
        List<Product> result = productService.getProductsSortedByName(
                drinks.getId(),
                CatalogService.Direction.ASC
        );

        // then
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        // 'apple', 'Banana', 'cherry' по алфавиту без учёта регистра
        assertEquals(List.of("apple", "Banana", "cherry"), names);
    }

    @Test
    void getProductsSortedByName_desc_caseInsensitive() throws Exception {
        var drinks = categoryService.addCategory("drinks");
        Product apple = Product.builder()
                .name("apple")
                .brand("B1")
                .category(drinks)
                .build();

        Product Banana = Product.builder()
                .name("Banana")
                .brand("B2")
                .category(drinks)
                .build();

        Product cherry = Product.builder()
                .name("cherry")
                .brand("B3")
                .category(drinks)
                .build();

        productRepository.save(apple);
        productRepository.save(Banana);
        productRepository.save(cherry);

        // when
        List<Product> result = productService.getProductsSortedByName(
                drinks.getId(),
                CatalogService.Direction.DESC
        );

        // then
        List<String> names = result.stream()
                .map(Product::getName)
                .toList();

        assertEquals(List.of("cherry", "Banana", "apple"), names);
    }


    @Test
    void getProductsSortedByPrice_throws_when_no_products_in_category() throws InstanceAlreadyExistsException {
        var sweets = categoryService.addCategory("drinks");
        assertThrows(InstanceNotFoundException.class, () ->
                productService.getProductsSortedByPrice(
                        sweets.getId(),
                        CatalogService.Direction.ASC
                )
        );
    }

}
