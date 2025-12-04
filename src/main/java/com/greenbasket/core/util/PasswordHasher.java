package com.greenbasket.core.util;

/**
 * Абстракция над хэшированием паролей.
 */
public interface PasswordHasher {

    /**
     * Превращает сырой пароль в строку-хэш, готовую для сохранения в БД.
     */
    public String hash(String rawPassword);

    /**
     * Проверяет, соответствует ли сырой пароль сохранённому хэшу.
     */
    public boolean matches(String rawPassword, String passwordHash);
}
