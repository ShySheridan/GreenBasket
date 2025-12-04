package com.greenbasket.server.main;

import com.greenbasket.core.repository.*;
import com.greenbasket.core.service.*;
import com.greenbasket.core.util.IdGenerator;
import com.greenbasket.core.util.PasswordHasher;
import com.greenbasket.server.persistence.memory.*;
import com.greenbasket.server.util.idGenerator.SimpleIdGenerator;
import com.greenbasket.server.util.security.BCryptPasswordHasher;

public class AppContext {
    private final IdGenerator idGenerator = new SimpleIdGenerator();
    private final PasswordHasher passwordHasher = new BCryptPasswordHasher();

    private final ProductInterface productRepository = new ProductInMemory(idGenerator);
    private final CategoryInterface categoryRepository = new CategoryInMemory(idGenerator);
    private final CommentInterface commentRepository = new CommentInMemory(idGenerator);
    private final UserInterface userRepository = new UserInMemory(idGenerator);

    private final ProductService productService =
            new ProductService(productRepository, categoryRepository, commentRepository);
    private final CategoryService categoryService =
            new CategoryService(categoryRepository, productRepository);
    private final CommentService commentService =
            new CommentService(commentRepository, productRepository, userRepository);
    private final UserService userService =
            new UserService(userRepository, passwordHasher);

    public ProductService productService() {
        return productService;
    }

    public CategoryService categoryService() {
        return categoryService;
    }

    public CommentService commentService() {
        return commentService;
    }

    public UserInterface userService() {
        return userRepository;
    }
}

