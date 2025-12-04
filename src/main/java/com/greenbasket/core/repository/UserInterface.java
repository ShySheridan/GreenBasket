package com.greenbasket.core.repository;

import com.greenbasket.core.domain.Order;
import com.greenbasket.core.domain.User;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.Optional;


public interface UserInterface {

   public User save(User user) throws InstanceAlreadyExistsException; // создать или обновить (шире чем add)

   public void remove(Long id);

   public List<User> findAll();

   public Optional<User> findById(Long id);

   public Optional<User> findByUsername(String username);

//   boolean isUserExists(Long id);

   /**
    * Нужен для логина/регистрации по email.
    */
   public Optional<User> findByEmail(String email);

   /**
    * Проверка существования пользователя с таким email (для регистрации).
    */
   public boolean isUserExistsByEmail(String email);


}
