package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Comment;
import com.greenbasket.core.repository.CommentInterface;
import com.greenbasket.core.util.IdGenerator;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CommentInMemory implements CommentInterface {
    private final HashMap<Long, Comment> storage = new HashMap<>();
    private final IdGenerator idGenerator;

    public CommentInMemory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }


    @Override
    public void remove(Long id){
        storage.remove(id);
    }

    @Override
    public Comment edit(Long id, String text,  int score){
        Comment comment = storage.get(id);
        comment.setText(text);
        comment.setScore(score);
        return comment;
    }

    @Override
    public Comment save(Comment comment) throws InstanceAlreadyExistsException {
        if (comment.getId() == null) {
            comment.assignId(idGenerator.generateId());
        }
        storage.put(comment.getId(), comment);
        return comment;
    }


    @Override
    public Optional<Comment> findById(Long id){
        return Optional.ofNullable(storage.get(id));
        // если будем удалять объект findById( Optional.of(null) ) кинет NullPointerException,
        // а Optional.ofNullable(null)  Optional.empty()
    }

    @Override
    public List<Comment> findAll(){
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Comment> findByUserId(Long userId){
        List<Comment> comments = new ArrayList<>();
        for (Comment comment : storage.values()) {
            if (comment.getUser().getId().equals(userId)) {
                comments.add(comment);
            }
        }
        return comments;
    }

    @Override
    public List<Comment> findByScore(int score){
        List<Comment> comments = new ArrayList<>();
        for (Comment comment : storage.values()) {
            if (comment.getScore() == score) {
                comments.add(comment);
            }
        }
        return comments;
    }

    @Override
    public List<Comment> findByProductId(Long productId){
        ArrayList<Comment> comments = new ArrayList<>();
        for (Comment comment : storage.values()) {
            if (comment.getProduct().getId().equals(productId)) {
                comments.add(comment);
            }
        }
        return comments;
    }

    @Override
    public int countByProductId(Long productId){
        return findByProductId(productId).size();
    }

    @Override
    public boolean isCommentExists(Long id){
        return storage.containsKey(id);
    }

}
