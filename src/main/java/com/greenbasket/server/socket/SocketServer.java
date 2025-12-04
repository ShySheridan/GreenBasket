package com.greenbasket.server.socket;


/**
 * TCP-сервер приложения GreenBasket.
 *
 * <p>Отвечает только за:
 * <ul>
 *     <li>создание и конфигурацию {@link java.net.ServerSocket};</li>
 *     <li>цикл accept() входящих соединений;</li>
 *     <li>запуск {@link ClientHandler} для каждого клиента (обычно в отдельном потоке).</li>
 * </ul>
 *
 * <p>Важные ограничения:
 * <ul>
 *     <li>не содержит бизнес-логики;</li>
 *     <li>не парсит команды протокола (делегирует это {@link SocketController});</li>
 *     <li>знает только о сервисах/порт-интерфейсах из слоя core.</li>
 * </ul>
 */
public class SocketServer {
}
