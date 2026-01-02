/*
Общие проверки, чтобы не дублировать в сервисах.

Что внутри:

Методы типа requireNonBlank(String, "NAME_REQUIRED"),
requireNonNegative(int, "PRICE_INVALID").

Что изучить: Проектирование утилит, читаемые сообщения об ошибках.

Что можно дописать:

Шаблоны ошибок с кодами/локализацией.

Композитные валидаторы (например, для пагинации).
 */
package com.greenbasket.core.util;

import com.greenbasket.core.exception.AppException;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class Validators {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private Validators() {
    }

    @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.Contract("null, _ -> fail")
    public static String requireNonBlank(String string, String fieldName) {
        if (string == null || string.trim().isEmpty()) throw new AppException("Поле " + fieldName + " не может быть пустым");

        return string.trim();
    }

    public static int requirePositiveOrZero(Integer value, String  fieldName) {
        if (value == null || value < 0) throw new AppException("Поле " + fieldName + " не может быть пустым или < 0");
        return value;
    }

    public static int requirePositive(Integer value, String  fieldName) {
        if (value == null || value <= 0) throw new AppException("Поле " + fieldName + " не может быть пустым или <= 0");
        return value;
    }

    public static int requireNonNull(int value, String fieldName) {
        if (value == 0) throw new AppException("Поле " + fieldName + " не может быть равно 0");
        return value;
    }

    public static int requireNegative(Integer value, String fieldName) {
        if (value == null || value > 0) throw new AppException("Поле " + fieldName + " не может быть пустым или > 0");
        return value;
    }

    public static String requireSize(String s, int min, int max, String fieldName) {
        if (s == null) throw new AppException("Поле " + fieldName + " не может быть пустым");
        var t = s.trim();
        if (t.length() < min || t.length() > max) throw new AppException("Поле " + fieldName + " выходит за границы");
        return t;
    }

    public static int requireInRange(Integer i, int min, int max, String fieldName) {
        if (i == null) throw new AppException("Поле " + fieldName + " не может быть пустым");
        if (i < min || i > max) throw new AppException("Поле " + fieldName + " выходит за границы");
        return i;
    }

    public static double requireInRange(Double d, int min, int max, String fieldName) {
        if (d == null) throw new AppException("Поле " + fieldName + " не может быть пустым");
        if (d < min || d > max) throw new AppException("Поле " + fieldName + " выходит за границы");
        return d;
    }

    public static LocalDate requireDateInPast(LocalDate date, String fieldName) {
        // проверяем что дата в прошлом
        if (date == null || date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.now().minusYears(2)))
            throw new AppException("Поле " + fieldName + " должно быть в прошлом");
        return date;
    }

    public static LocalDate requireDateInFuture(LocalDate date, String fieldName) {
        if (date == null || date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now().plusYears(2)))
            throw new AppException("Поле " + fieldName + " должно быть в будущем");
        return date;
    }

    public static <T> List<T> requireNonEmptyList(List<T> list, String fieldName) {
        if (list == null || list.isEmpty()) throw new AppException("Поле " + fieldName + " не может быть пустым");
        return list;
    }

    public static <T> T requireNotNull(T object, String fieldName) {
        if (object == null) throw new AppException("Поле " + fieldName + " не может быть пустым");
        return object;
    }

    /**
     * Проверяет, что email не пустой и имеет нормальный формат.
     * Возвращает нормализованный email (обрезан и в нижнем регистре).
     */
    public static String requireValidEmail(String value, String fieldName) {
        String email = requireNonBlank(value, fieldName).trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Некорректный " + fieldName + ": " + value);
        }

        return email;
    }


    public static String requireValidPhone(String value, String fieldName) {
        String raw = requireNonBlank(value, fieldName).trim();

        boolean hasPlus = raw.startsWith("+");

        // Убираем всё, кроме цифр
        String digits = raw.replaceAll("\\D", ""); // \D = всё, что НЕ цифра

        if (digits.length() < 5 || digits.length() > 15) {
            throw new IllegalArgumentException("Некорректный " + fieldName + ": " + value);
        }

        return hasPlus ? "+" + digits : digits;
    }
}