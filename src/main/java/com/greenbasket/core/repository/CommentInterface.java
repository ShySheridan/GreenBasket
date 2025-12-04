package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Comment;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;

public interface CommentInterface {
    public void remove(Long id);

    public Comment save(Comment comment) throws InstanceAlreadyExistsException;

    public Comment edit(Long id, String text, int score);

    public Optional<Comment> findById(Long id);

    public List<Comment> findAll();

    public List<Comment> findByUserId(Long userId);

    public List<Comment> findByScore(int score);

    public List<Comment> findByProductId(Long productId);

    public int countByProductId(Long productId);

    public boolean isCommentExists(Long id);
}
