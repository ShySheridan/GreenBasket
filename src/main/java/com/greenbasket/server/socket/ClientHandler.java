package com.greenbasket.server.socket;

/**
 * Обработчик одного TCP-клиента.
 *
 * <p>Экземпляр этого класса создаётся для каждого принятого соединения
 * и, как правило, выполняется в отдельном потоке (через {@link Runnable}).
 *
 * <p>Обязанности:
 * <ul>
 *     <li>читать запросы из {@link java.net.Socket#getInputStream()};</li>
 *     <li>передавать сырой текст/JSON в {@link SocketController};</li>
 *     <li>отправлять ответы клиента обратно в {@link java.net.Socket#getOutputStream()}.</li>
 * </ul>
 *
 * <p>Класс не должен:
 * <ul>
 *     <li>знать бизнес-правила (скидки, корзины и т.д.);</li>
 *     <li>напрямую работать с репозиториями или БД;</li>
 *     <li>создавать/конфигурировать ServerSocket (это задача {@link SocketServer}).</li>
 * </ul>
 */
public class ClientHandler implements Runnable {

    @Override
    public void run() {

    }
}
