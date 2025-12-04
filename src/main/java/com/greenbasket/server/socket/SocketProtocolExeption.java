package com.greenbasket.server.socket;

/**
 * Исключение, означающее ошибку на уровне протокола, а не внутренней логики.
 *
 * <p>Примеры:
 * <ul>
 *     <li>неизвестная команда;</li>
 *     <li>нехватка аргументов;</li>
 *     <li>невозможность распарсить число/JSON;</li>
 *     <li>ошибка версии протокола.</li>
 * </ul>
 *
 * <p>Такие ошибки обычно конвертируются в понятный текстовый ответ клиенту
 * (например, "ERROR Invalid command").
 */
public class SocketProtocolExeption extends RuntimeException {
    public SocketProtocolExeption(String message) {
        super(message);
    }
}
