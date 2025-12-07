package com.greenbasket.client.transport.socket;

import java.io.*;
import java.net.Socket;

/**
 * отвечает за установление соединения (адрес, порт), чтение/запись строк/байтов, закрытие соединения;
 */
public class ServerConnection implements Closeable {
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerConnection(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        connect();
    }

    private void connect() throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(),
                true); // autoFlush: println() сразу отправляет строку
    }

    public void sendLine(String line) {
        if (!isOpen()) {
            throw new IllegalStateException("Socket is not connected");
        }
        if (line == null) {
            throw new NullPointerException("line is null");
        }
        out.println(line);
    }


    public String readLine() throws IOException {
        if (!isOpen()) {
            throw new IllegalStateException("Socket is not connected");
        }
        return in.readLine();
    }


    /**
     * @return true, если сокет подключен и не закрыт.
     */
    public boolean isOpen() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Закрывает соединение и связанные потоки.
     */
    @Override
    public void close() throws IOException {
        IOException firstError = null;

        if (out != null) {
            out.close(); // PrintWriter.close() не кидает IOException
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                firstError = e;
                // Закрывая несколько ресурсов, запомни первую возникшую ошибку, но всё равно попытайся закрыть остальные
            }
        }

        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
//Если это первая ошибка при закрытии – запомни её.
//Если до этого уже что-то упало при закрытии другого ресурса – новую ошибку игнорируем (мы её не теряем, но и не затираем первой)
                if (firstError == null) {
                    firstError = e;
                }
            }
        }
// если были ошибки, бросаем первую
        if (firstError != null) {
            throw firstError;
        }
    }

    @Override
    public String toString() {
        return "ServerConnection{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", open=" + isOpen() +
                '}';
    }
}
