package service;
import com.greenbasket.core.domain.*;
import com.greenbasket.core.repository.*;
import com.greenbasket.core.service.*;
import com.greenbasket.core.util.IdGenerator;
import com.greenbasket.server.util.idGenerator.SimpleIdGenerator;
import com.greenbasket.server.persistence.memory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceTest {
    private ProductService productService;
    private CategoryService categoryService;
    private CommentService commentService;
    private UserService userService;

    private ProductInterface productRepository;
    private CategoryInterface categoryRepository;
    private CommentInterface commentRepository;
    private UserInterface userRepository;

    private Product product;
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

        product = new Product();
    }


    @Test
    void category_is_created_and_listed() throws InstanceAlreadyExistsException {
        var before = categoryService.getAllCategories().size();

        var category = categoryService.addCategory("Фрукты");
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals("Фрукты", category.getName());

        var categories = categoryService.getAllCategories();
        assertEquals(before + 1, categories.size());
        assertTrue(
                categories.stream().anyMatch(c -> "Фрукты".equals(c.getName()))
        );

        categories.forEach(c ->
                System.out.println("id=" + c.getId() + ", name=" + c.getName())
        );

    }

    @Test
    void print_all_categories_in_memory() throws InstanceAlreadyExistsException {

        categoryService.addCategory("Овощи");
        categoryService.addCategory("Молочные продукты");
        var categories = categoryService.getAllCategories();

        System.out.println("==== КАТЕГОРИИ В ПАМЯТИ ====");
        if (categories.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            categories.forEach(c ->
                    System.out.println("id=" + c.getId() + ", name=" + c.getName())
            );
        }
        assertEquals(2, categories.size());
        assertTrue(categories.stream().anyMatch(c -> "Овощи".equals(c.getName())));
        assertTrue(categories.stream().anyMatch(c -> "Молочные продукты".equals(c.getName())));

    }

    @Test
    void deleteCategory_removes_existing_category() throws Exception {
        Long id = 5L;
        Category category = Category.builder()
                .id(id)
                .name("Напитки")
                .build();

        categoryRepository.save(category);

        categoryService.deleteCategory(id);

        assertTrue(categoryRepository.findById(id).isEmpty());
    }


    @Test
    void addCategory_throws_when_name_already_exists() throws Exception {
        categoryService.addCategory("Фрукты");

        assertThrows(InstanceAlreadyExistsException.class,
                () -> categoryService.addCategory("Фрукты"));
    }


    @Test
    void deleteCategory_throws_when_not_found() throws Exception {
        Long id = 5L;
        assertThrows(InstanceNotFoundException.class, () -> {categoryService.deleteCategory(id);});
    }


    @Test
    void get_category_by_id_returns_category_when_found() throws Exception {
        Long id = 234L;
        Category category = Category.builder()
                .id(id)
                .name("Овощи")
                .build();
        categoryRepository.save(category);

        assertEquals(id, category.getId());
        assertEquals(category.getName(), categoryService.getCategoryById(id).getName());
        assertEquals(category, categoryService.getCategoryById(id));

        Category result = categoryService.getCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Овощи", result.getName());
    }


    @Test
    void getCategoryById_throws_when_not_found() {
        // given
        Long id = 2L;

        assertThrows(InstanceNotFoundException.class,
                () -> categoryService.getCategoryById(id));
    }


    @Test
    void applyDiscountToCategory_sets_discount_for_category_and_products() throws Exception {
        // given

        int discount = 15;
        Category category = Category.builder()
                .name("Молочные продукты")
                .discount(10)
                .build();

        category = categoryRepository.save(category);
        Long categoryId = category.getId();
        System.out.println("categoryDiscount=" + category.getDiscount());


        Product p1 = Product.builder()
                .name("Молоко")
                .category(category)
                .discount(0)
                .build();

        Product p2 = Product.builder()
                .name("Творог")
                .category(category)
                .discount(5)
                .build();

        productRepository.save(p1);
        productRepository.save(p2);

        System.out.println("productDiscount=" + p1.getDiscount());
        System.out.println("productDiscount=" + p2.getDiscount());

        // when
        categoryService.applyDiscountToCategory(categoryId, discount);

        // then
        assertEquals(discount, category.getDiscount());
        assertEquals(discount, p1.getDiscount());
        assertEquals(discount, p2.getDiscount());

        System.out.println("category discount=" + category.getDiscount());
        System.out.println("product1 discount=" + p1.getDiscount());
        System.out.println("product2 discount=" + p2.getDiscount());
    }

    @Test
    void applyDiscountToCategory_throws_when_category_not_found() {
        Long categoryId = 123L;

        assertThrows(InstanceNotFoundException.class,
                () -> categoryService.applyDiscountToCategory(categoryId, 10));
    }
}
