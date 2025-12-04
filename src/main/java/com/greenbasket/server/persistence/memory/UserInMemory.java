package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.UserInterface;
import com.greenbasket.core.util.IdGenerator;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class UserInMemory implements UserInterface {
    private final HashMap<Long, User> storage = new HashMap<>();
    private final IdGenerator idGenerator;

    public UserInMemory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public User save(User user) throws InstanceAlreadyExistsException {
        if (user.getId() == null) {
            user.assignId(idGenerator.generateId());
        }
        storage.put(user.getId(), user);
        return user;
    }

    public void remove(Long id){
        storage.remove(id);
    }

    public List<User> findAll(){
        return new ArrayList<>(storage.values());
    }

    public Optional<User> findById(Long id){
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<User> findByUsername(String username){
        return storage.values().stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }
}
