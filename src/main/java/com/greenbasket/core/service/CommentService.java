package com.greenbasket.core.service;

import com.greenbasket.core.domain.Comment;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.CommentInterface;
import com.greenbasket.core.repository.ProductInterface;
import com.greenbasket.core.repository.UserInterface;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;

import static com.greenbasket.core.util.Validators.requireInRange;
import static com.greenbasket.core.util.Validators.requireSize;

// СЕРВИСЫ НЕ ЗНАЮТ ДРУГ О ДРУГЕ

public class CommentService {
    private final CommentInterface commentRepository;
    private final ProductInterface productRepository;
    private final UserInterface userRepository;

    public CommentService(CommentInterface commentRepository, ProductInterface productRepository,
                          UserInterface userRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }


    private Product requireProduct(Long id) throws InstanceNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("продукт не найден"));
    }


    private User requireUser(Long id
    ) throws InstanceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new InstanceNotFoundException("пользователь не найден"));
    }


    public Comment addComment(Long productId, String text, Long userId, int score) throws InstanceNotFoundException, InstanceAlreadyExistsException {
        String safeText = requireSize(text, 1, 100, "комментарий");
        int safeScore = requireInRange(score, 1, 5, "оценка товара");

        Product product = requireProduct(productId);
        User user = requireUser(userId);

        Comment newComment = Comment.builder()
                .text(safeText)
                .user(user)
                .product(product)
                .score(safeScore)
                .build();
        commentRepository.save(newComment);
        return newComment;
    }


    private Comment requireComment(Long commentId) throws InstanceNotFoundException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new InstanceNotFoundException("комментарий не найден"));
    }


    public List<Comment> getAllComments() throws InstanceNotFoundException {
//        if (commentRepository.findAll().isEmpty()) {
//            throw new InstanceNotFoundException("нет ни одного комментария");
//        }
        return commentRepository.findAll();
    }


    public void deleteComment(Long commentId) throws InstanceNotFoundException {
        requireComment(commentId);
        commentRepository.remove(commentId);
    }


    public List<Comment> getCommentsByProductId(Long productId) throws InstanceNotFoundException {
        requireProduct(productId);

        return commentRepository.findByProductId(productId);

//        if (comments.isEmpty()) {
//            throw new InstanceNotFoundException("у продукта нет ни одного комментария");
//        }
    }


    public List<Comment> getCommentsByUserId(Long userId) throws InstanceNotFoundException {
        requireUser(userId);

        //        if (comments.isEmpty()) {
//            throw new InstanceNotFoundException("у пользователя нет ни одного комментария");
//        }
        return commentRepository.findByUserId(userId);
    }


    public Comment addCommentAndUpdateRating(Long productId, Long userId, String text, int score
    ) throws InstanceNotFoundException {
        User user = requireUser(userId);
        Product product = requireProduct(productId);

        return Comment.builder()
                .user(user)
                .text(text)
                .score(score)
                .product(product)
                .build();
    }


    public void updateProductRating(Long productId) throws InstanceNotFoundException {
        // find all comments, get scopes, find average, set average rating to product
        Product product = requireProduct(productId);

        double rating = commentRepository.findByProductId(productId).stream()
                .mapToInt(Comment::getScore)
                .average().orElse(0.0);

        product.setAverageRating((rating));
    }

//    public Comment editComment(Long id, String text, int score) throws InstanceNotFoundException {
//
//    }

    // TODO public Comment editComment(Long id, String text)
}
