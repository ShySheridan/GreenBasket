package com.greenbasket.server.main;

import com.greenbasket.core.repository.*;
import com.greenbasket.core.service.*;
import com.greenbasket.core.util.IdGenerator;
import com.greenbasket.core.util.PasswordHasher;
import com.greenbasket.server.persistence.memory.*;
import com.greenbasket.server.socket.ClientHandler;
import com.greenbasket.server.socket.SocketController;
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

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;

//    public AppContext(PasswordHasher passwordHasher, SocketController socketController, IdGenerator idGenerator,
//                      ProductInterface productRepository, CategoryInterface categoryRepository,
//                      CommentInterface commentRepository) {
    public AppContext() throws IOException {
        this.passwordHasher = new BCryptPasswordHasher();
//        this.socketController = new SocketController(new ClientHandler());
        this.idGenerator = new SimpleIdGenerator();
        this.productRepository = new ProductInMemory(idGenerator);
        this.categoryRepository = new CategoryInMemory(idGenerator);
        this.commentRepository = new CommentInMemory(idGenerator);
        this.userRepository = new UserInMemory(idGenerator);
        this.productService = new ProductService(productRepository, categoryRepository, commentRepository);
        this.categoryService  =
                new CategoryService(categoryRepository, productRepository);
        this.commentService  =
                new CommentService(commentRepository, productRepository, userRepository);
        this.userService =
                new UserService(userRepository, passwordHasher);
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

    public UserInterface userService() {
        return userRepository;
    }


    private Map<String, CommandHandler> createCommandHandlers() {
        Map<String, CommandHandler> handlers = new HashMap<>();

        handlers.put("HELP",
                new HelpCommandHandler());
        handlers.put("SIGN_IN",
                new SignInCommandHandler(userService));
        handlers.put("LOG_OUT",
                new LogOutCommandHandler(userService));
        handlers.put("SIGH_UP",
                new SignUpCommandHandler(userService));
        handlers.put("EXIT",
                new ExitCommandHandler());

        handlers.put("SHOW_BUCKET",
                new ShowBucketCommandHandler(bucketService));
        handlers.put("ADD_PRODUCT_TO_BUCKET",
                new AddProductToBucketCommandHandler());
        handlers.put("ADD_COMMENT",
                new AddCommentCommandHandler());
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

