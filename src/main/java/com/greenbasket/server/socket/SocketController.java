package com.greenbasket.server.socket;

import com.greenbasket.core.exception.AppException;
import com.greenbasket.server.socket.commands.CommandHandler;

import java.util.HashMap;

/**
 * Разбор и обработка команд протокола поверх сокета.
 *
 * <p>Получает от {@link ClientHandler} строку/JSON запроса, выбирает нужный use-case
 * и вызывает соответствующий метод сервисов из слоя core.
 *
 * <p>Функции:
 * <ul>
 *     <li>парсинг текста запроса (например, "ADD_PRODUCT ...", JSON и т.п.);</li>
 *     <li>валидация формата команды;</li>
 *     <li>маппинг данных запроса в DTO или аргументы методов сервисов;</li>
 *     <li>формирование текстового/JSON-ответа для клиента.</li>
 * </ul>
 *
 * <p> выступает "входным адаптером" (inbound adapter):
 * он переводит понятия протокола (команды, сообщения) в понятия домена
 * (продукты, категории, заказы).
 */
public class SocketController {
    private final ClientHandler clientHandler;
    private final HashMap<String, CommandHandler> commandList;


    public SocketController(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;

    }

    public String handle(String rawRequest) throws AppException{

    }

}
