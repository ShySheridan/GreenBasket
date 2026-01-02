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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {
    private ProductService productService;
    private CategoryService categoryService;
    private CommentService commentService;
    private UserService userService;

    private ProductInterface productRepository;
    private CategoryInterface categoryRepository;
    private CommentInterface commentRepository;
    private UserInterface userRepository;

    private IdGenerator idGenerator = new SimpleIdGenerator();
    private Product milk;
    private Product juice;
    private User alice;
    private User bob;

    @BeforeEach
    void setUp() throws InstanceAlreadyExistsException {
        categoryRepository = new CategoryInMemory(idGenerator);
        productRepository = new ProductInMemory(idGenerator);
        commentRepository = new CommentInMemory(idGenerator);
        userRepository = new UserInMemory(idGenerator);

        categoryService = new CategoryService(categoryRepository, productRepository);
        productService = new ProductService(productRepository, categoryRepository, commentRepository);
        commentService = new CommentService(commentRepository, productRepository, userRepository);

        milk = Product.builder()
                .name("Milk")
                .build();
        productRepository.save(milk); // id = 1

        juice = Product.builder()
                .name("Juice")
                .build();
        productRepository.save(juice); // id = 2

        alice = User.builder().username("alice").build();
        userRepository.save(alice);    // id = 1

        bob = User.builder().username("bob").build();
        userRepository.save(bob);
    }


    @Test
    void addComment_success() throws Exception {
        Long productId = milk.getId();
        Long userId = alice.getId();

        Comment c = commentService.addComment(productId, "Нормальный товар", userId, 5);

        assertNotNull(c);
        assertEquals("Нормальный товар", c.getText());
        assertEquals(5, c.getScore());
        assertEquals(milk.getId(), c.getProduct().getId());
        assertEquals(alice.getId(), c.getUser().getId());
        assertEquals(1, commentRepository.findAll().size());
    }

    @Test
    void addComment_throws_when_text_invalid() {
        Long productId = milk.getId();
        Long userId = alice.getId();

        // пустая строка
        assertThrows(AppException.class,
                () -> commentService.addComment(productId, "", userId, 4));

        // длиннее 100 символов
        String longText = "a".repeat(101);
        assertThrows(AppException.class,
                () -> commentService.addComment(productId, longText, userId, 4));
    }

    @Test
    void addComment_throws_when_score_invalid() {
        Long productId = milk.getId();
        Long userId = alice.getId();

        assertThrows(AppException.class,
                () -> commentService.addComment(productId, "ok", userId, 0));
        assertThrows(AppException.class,
                () -> commentService.addComment(productId, "ok", userId, 6));
    }

    @Test
    void addComment_throws_when_product_not_found() {
        Long userId = alice.getId();

        assertThrows(InstanceNotFoundException.class,
                () -> commentService.addComment(999L, "well", userId, 4));
    }

    @Test
    void addComment_throws_when_user_not_found() {
        Long productId = milk.getId();

        assertThrows(InstanceNotFoundException.class,
                () -> commentService.addComment(productId, "meh", 999L, 4));
    }


    @Test
    void getAllComments_returns_all_comments() throws Exception {
        List<Comment> comments = new ArrayList<>();
        commentService.addComment(milk.getId(), "meh", alice.getId(), 4);
        commentService.addComment(juice.getId(), "good", bob.getId(), 5);

        comments = commentService.getAllComments();
        for (Comment comment : comments) {
            System.out.println(comment.getProduct().getName());
        }
        assertEquals(2, comments.size());
    }

    @Test
    void getAllComments_returns_empty_when_no_comments() throws Exception {
        List<Comment> comments = commentService.getAllComments();
        assertTrue(comments.isEmpty());
    }


    @Test
    void deleteComment_success() throws Exception {
        Comment c = commentService.addComment(milk.getId(), "to delete", alice.getId(), 3);
        List<Comment> comments = commentService.getAllComments();
        for (Comment comment : comments) {
            System.out.println(comment.getProduct().getName() + comment.getId());
        }
        assertEquals(1, commentRepository.findAll().size());

        commentService.deleteComment(c.getId());
        for (Comment comment : comments) {
            System.out.println(comment.getProduct().getName());
        }
        assertEquals(0, commentRepository.findAll().size());
        assertTrue(commentRepository.findById(c.getId()).isEmpty());
    }

    @Test
    void deleteComment_throws_when_comment_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.deleteComment(999L));
    }


    @Test
    void getCommentsByProductId_returns_comments_for_product() throws Exception {
        commentService.addComment(milk.getId(), "m1", alice.getId(), 4);
        commentService.addComment(milk.getId(), "m2", bob.getId(), 5);
        commentService.addComment(juice.getId(), "j1", alice.getId(), 3);

        List<Comment> comments = commentService.getCommentsByProductId(milk.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.stream()
                .allMatch(c -> c.getProduct().getId().equals(milk.getId())));
    }

    @Test
    void getCommentsByProductId_throws_when_product_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.getCommentsByProductId(999L));
    }


    @Test
    void getCommentsByUserId_returns_comments_for_user() throws Exception {

        commentService.addComment(milk.getId(), "c1", alice.getId(), 4);
        commentService.addComment(juice.getId(), "c2", alice.getId(), 5);
        commentService.addComment(milk.getId(), "c3", bob.getId(), 3);


        List<Comment> comments = commentService.getCommentsByUserId(alice.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.stream()
                .allMatch(c -> c.getUser().getId().equals(alice.getId())));
    }

    @Test
    void getCommentsByUserId_throws_when_user_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.getCommentsByUserId(999L));
    }


    @Test
    void addCommentAndUpdateRating_builds_comment_without_saving() throws Exception {
        Long productId = milk.getId();
        Long userId = alice.getId();

        Comment c = commentService.addCommentAndUpdateRating(productId, userId, "hey", 5);

        assertNotNull(c);
        assertNull(c.getId()); // не сохраняем в репозиторий
        assertEquals("hey", c.getText());
        assertEquals(5, c.getScore());
        assertEquals(milk.getId(), c.getProduct().getId());
        assertEquals(alice.getId(), c.getUser().getId());
        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    void addCommentAndUpdateRating_throws_when_product_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.addCommentAndUpdateRating(999L, alice.getId(), "x", 3));
    }

    @Test
    void addCommentAndUpdateRating_throws_when_user_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.addCommentAndUpdateRating(milk.getId(), 999L, "x", 3));
    }


    @Test
    void updateProductRating_sets_average_from_comments() throws Exception {
        commentService.addComment(milk.getId(), "c1", alice.getId(), 3);
        commentService.addComment(milk.getId(), "c2", bob.getId(), 5);
        commentService.addComment(milk.getId(), "c3", alice.getId(), 4);

        commentService.updateProductRating(milk.getId());

        Product updated = productRepository.findById(milk.getId()).orElseThrow();
        assertEquals(4.0, updated.getAverageRating(), 0.0001);
    }

    @Test
    void updateProductRating_sets_zero_when_no_comments() throws Exception {
        milk.setAverageRating(10.0);
        productRepository.save(milk);

        commentService.updateProductRating(milk.getId());

        Product updated = productRepository.findById(milk.getId()).orElseThrow();
        assertEquals(0.0, updated.getAverageRating(), 0.0001);
    }

    @Test
    void updateProductRating_throws_when_product_not_found() {
        assertThrows(InstanceNotFoundException.class,
                () -> commentService.updateProductRating(999L));
    }
}
