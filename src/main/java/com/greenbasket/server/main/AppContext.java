package com.greenbasket.server.main;

import com.greenbasket.core.domain.Cart;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.*;
import com.greenbasket.core.service.*;
import com.greenbasket.core.util.IdGenerator;
import com.greenbasket.core.util.PasswordHasher;
import com.greenbasket.server.persistence.memory.*;
import com.greenbasket.server.socket.commands.CommandHandler;
import com.greenbasket.server.util.idGenerator.SimpleIdGenerator;
import com.greenbasket.server.util.security.BCryptPasswordHasher;
import com.greenbasket.server.socket.commands.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppContext {
    private final IdGenerator idGenerator;
    private final PasswordHasher passwordHasher;
//    private final SocketController socketController;
    private final ProductInterface productRepository;
    private final CategoryInterface categoryRepository;
    private final CommentInterface commentRepository ;
    private final UserInterface userRepository;
    private final CartInterface cartRepository;
    private final OrderInterface orderRepository;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;
    private final CartService cartService;

    private User admin = User.builder()
            .role(User.Role.ADMIN)
            .email("admin@shop.local")
            .username("admin")
            .passwordHash("admin123")
            .build();


//    public AppContext(PasswordHasher passwordHasher, SocketController socketController, IdGenerator idGenerator,
//                      ProductInterface productRepository, CategoryInterface categoryRepository,
//                      CommentInterface commentRepository) {
    public AppContext() {
        this.passwordHasher = new BCryptPasswordHasher();
//        this.socketController = new SocketController(new ClientHandler());
        this.idGenerator = new SimpleIdGenerator();
        this.productRepository = new ProductInMemory(idGenerator);
        this.categoryRepository = new CategoryInMemory(idGenerator);
        this.commentRepository = new CommentInMemory(idGenerator);
        this.userRepository = new UserInMemory(idGenerator);
        this.cartRepository = new CartInMemory(idGenerator);
        this.orderRepository = new OrderInMemory();

        this.productService = new ProductService(productRepository, categoryRepository, commentRepository);
        this.categoryService  =
                new CategoryService(categoryRepository, productRepository);
        this.commentService  =
                new CommentService(commentRepository, productRepository, userRepository);
        this.userService =
                new UserService(userRepository, passwordHasher);
        this.cartService =
                new CartService(productRepository, cartRepository, userRepository, orderRepository);
    }

    public ProductService productService() {
        return productService;
    }

    public CategoryService categoryService() {
        return categoryService;
    }

    public CommentService commentService() {
        return commentService;
    }

    public UserService userService() {
        return userService;
    }


    private Map<String, CommandHandler> createCommandHandlers() {
        Map<String, CommandHandler> handlers = new HashMap<>();

        handlers.put("HELP",
                new HelpCommandHandler());
        handlers.put("SIGN_IN",
                new SignInCommandHandler(userService));
        handlers.put("SIGN_OUT",
                new SignOutCommandHandler(userService));
        handlers.put("SIGN_UP",
                new SignUpCommandHandler(userService));
        handlers.put("EXIT",
                new ExitCommandHandler());

        handlers.put("SHOW_BUCKET",
                new ShowCartCommandHandler(cartService));
        handlers.put("ADD_PRODUCT_TO_BUCKET",
                new AddProductToCartCommandHandler(cartService));
        handlers.put("ADD_COMMENT",
                new AddCommentCommandHandler(commentService));
        handlers.put("ADD_PRODUCT_TO_CATEGORY",
                new AddProductToCategoryCommandHandler());
        handlers.put("MAKE_ORDER",
                new MakeOrderCommandHandler());
        handlers.put("REMOVE_PRODUCT",
                new RemoveProductCommandHandler());
        handlers.put("SHOW_CATEGORY",
                new ShowCategoryCommandHandler());
        handlers.put("SHOW_PRODUCT_DESCRIPTION",
                new ShowProductDescriptionCommandHandler());

        return handlers;
    }
}

