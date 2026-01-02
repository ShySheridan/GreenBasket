package com.greenbasket.server.socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class ServerSocketChannel {
    private  ServerSocket serverSocket;
    private final SocketController socketController; // объект, который умеет обрабатывать команды
    private final ExecutorService clientPool; // пул потоков для клиентов
    private final int port;
    private volatile boolean running; // volatile обеспечение видимости изменений переменной между разными потоками

    public ServerSocketChannel(int port, SocketController socketController) {
        this.port = port;
        this.socketController = Objects.requireNonNull(socketController, "socketController");
        this.clientPool = Executors.newCachedThreadPool();
        this.running = false;
    }
    public void start() throws IOException {
        if (running) {
            throw new IllegalStateException("Server is already running");
        }
        running = true;
        try(ServerSocket serverSocket = new ServerSocket(port)){
            this.serverSocket = serverSocket;
            System.out.println("GreenBasket ServerSocketChannel started on port " + port);
            while (running) {
                try {
                    // ждем подключения клиента
                    Socket clientSocket = serverSocket.accept(); // блокирует выполнение программы до тех пор, пока клиент не подключится
                    System.out.println("New client connected from "
                            + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    // для каждого клиента запускаем отдельный обработчик
                    ClientHandler handler = new ClientHandler(clientSocket, socketController);
                    clientPool.submit(handler); // передать handler в clientPool (чтобы он выполнялся в отдельном потоке)
                } catch (IOException e) {
                    if (running) {
                        // Ошибка во время работы сервера
                        System.err.println("Error accepting client connection: " + e.getMessage());
                        e.printStackTrace();
                    } else {
                        // Если running = false, значит нас кто-то корректно остановил (stop()),
                        // и accept() мог кинуть исключение из-за закрытого ServerSocket.
                        System.out.println("Server stopped accepting connections.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start ServerSocketChannel on port " + port + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
            System.out.println("GreenBasket ServerSocketChannel stopped.");
        }
    }


    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close(); // это прервёт блокирующий accept()
        }
        clientPool.shutdown();
    }
}
