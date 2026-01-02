package com.greenbasket.server.socket;

import com.greenbasket.core.exception.AppException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
 *     <li>создавать/конфигурировать ServerSocket (это задача {@link ServerSocketChannel}).</li>
 * </ul>
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final SocketController socketController;

    public ClientHandler(Socket clientSocket, SocketController socketController) throws IOException {
//        this.serverSocket = serverSocket;
        this.clientSocket = Objects.requireNonNull(clientSocket, "socket must not be null");
        this.socketController = Objects.requireNonNull(socketController, "socketController must not be null");
    }

    @Override
    public void run() {
        try(
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            clientSocket.getOutputStream(), StandardCharsets.UTF_8)
                    ), true);
        ){
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                String trimmedLine = inputLine.trim();
                if (trimmedLine.isEmpty()) {
                    // пустые строки игнорируем
                    continue;
                }
                if ("exit".equals(trimmedLine) || "quit".equals(trimmedLine)) {
                    out.println("bye");
                    break;
                }
                try {
                    // передаём сырую строку в диспетчер протокола
                    String response = socketController.handle(trimmedLine);
                    // и отправляем ответ клиенту
                    out.println(response);
                } catch (AppException e) {
                    // ошибка формата/команды — говорим об этом клиенту
                    out.println("ERROR " + e.getMessage());
                } catch (Exception e) {
                    // любая непредвиденная ошибка — логируем и шлём общую ошибку
                    System.err.println("Unhandled error while processing request from "
                            + socketInfo() + ": " + e.getMessage());
                    e.printStackTrace();
                    out.println("ERROR INTERNAL_SERVER_ERROR");
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error in client handler for "
                    + socketInfo() + ": " + e.getMessage());
        } finally {
            closeSocketQuietly();
            System.out.println("Client handler finished for " + socketInfo());
        }
        // читает строки из BufferedReader (запросы),
        //отдаёт эти строки SocketController,
        //получает строку-ответ,
    }


    private void closeSocketQuietly() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException ignore) {}
    }

    private String socketInfo() {
        return clientSocket.getInetAddress() + ":" + clientSocket.getPort();
    }
}
