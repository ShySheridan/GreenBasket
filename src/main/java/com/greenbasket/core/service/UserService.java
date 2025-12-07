package com.greenbasket.core.service;
import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.UserInterface;
import com.greenbasket.core.util.PasswordHasher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;

import static com.greenbasket.core.util.Validators.*;

/**
 * Регистрация, авторизация, профиль
 * проверка уникальности email/логина;
 * валидация пароля;
 * хеширование пароля;
 * подтверждение почты;
 * блокировки и т.п.
 */
public class UserService {
    private final UserInterface userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserInterface userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

//    public User addUser(String username) throws InstanceAlreadyExistsException {
//        String safeUsername = requireNonBlank(username, "никнейм");
//        User user = new User();
//        user.setUsername(safeUsername);
//        userRepository.save(user);
//        return user;
//    }

    /**
     * Регистрация нового пользователя.
     * - проверяем, что email уникален
     * - хэшируем пароль
     * - сохраняем пользователя с passwordHash
     */
    public User register(String email, String name, String rawPassword, String phone)
            throws InstanceAlreadyExistsException {

        String safeEmail = requireValidEmail(email, "почта")
                .trim()
                .toLowerCase();
        String safeName = requireNonBlank(name, "никнейм").trim();
        String safePassword = requireNonBlank(rawPassword, "пароль");
        String safePhone = requireValidPhone(phone, "номер телефона");


        if (userRepository.isUserExistsByEmail(safeEmail)) {
            throw new InstanceAlreadyExistsException("пользователь с такой почтой уже существует");
        }

        String hash = passwordHasher.hash(safePassword);

        User newUser = User.builder()
                .email(safeEmail)
                .username(safeName)
                .passwordHash(hash)
                .phone(safePhone)
                .build();

        return userRepository.save(newUser);
    }

    /**
     * Аутентификация (логин) пользователя по email и паролю.
     * Возвращает User при удачном логине, либо кидает исключение.
     */
    public User login(String email, String rawPassword, String phone)
            throws InstanceNotFoundException {

        String safeEmail = requireValidEmail(email, "почта")
                .trim()
                .toLowerCase();
        String safePassword = requireNonBlank(rawPassword, "пароль");
        String safePhone = requireValidPhone(phone, "номер телефона");

        User user = userRepository.findByEmail(safeEmail)
                .orElseThrow(() -> new InstanceNotFoundException("пользователь не найден"));

        if (!passwordHasher.matches(safePassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("неверный пароль");
        }

        return user;
    }


    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("пользователь не найден"));

        return User.builder()
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .email(user.getEmail())
                .phone(user.getPhone())
//                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }


    // isPasswordCorrect

    // TODO регистрация, авторизация, аутентификация
}