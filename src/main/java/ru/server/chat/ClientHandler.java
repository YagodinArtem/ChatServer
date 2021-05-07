package ru.server.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.*;

public class ClientHandler {

    private Engine engine;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private final long timeOut = 120;


    public ClientHandler(Engine engine, Socket socket) {
        try {
            primaryInitialize(engine, socket);
            new Thread(() -> {
                try {
                    authTimeout();
                    readMessages();
                } catch (IOException e) {
                    System.err.println("Client " + nick + " disconnected");
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Fault create client handler");
        }
    }

    /**
     * Первичная инициализация клиента подключения
     *
     * @param engine объект ссылка на текущий инстанс сервера Engine
     * @param socket объект ссылка на текущий сокет сервера Engine
     * @throws IOException выбрасывает исключение если один из потоков либо сокет недоступны
     */

    private void primaryInitialize(Engine engine, Socket socket) throws IOException {
        this.engine = engine;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.nick = "";
    }

    /**
     * В цикле проверяет входящее сообщение от клиента и если оно
     * отвечает условию проводит аутентификацию на сервере
     *
     * @throws IOException выбрасывает исключение если поток считывания недоступен
     */
    public void authentication() throws IOException {
        sendMsg("Для аутентификации напишите в чат </auth login pass>");
        while (true) {
            String fromClient = in.readUTF();
            if (fromClient.startsWith("/auth")) {
                String[] params = fromClient.split(" ");
                String temp = engine.getAuthService().getNickByLoginAndPass(params[1], params[2]);
                if (!temp.equals("") && !engine.isNickBusy(temp)) {
                    nick = temp;
                    engine.subscribe(this);
                    engine.broadcastMsg(nick + ": зашел в чат");
                    sendMsg("Для смены ника напишите в чат /nick <login password newNick>");
                    return;
                } else {
                    sendMsg("Ник занят, либо неверные <логин пароль>");
                }
            }
        }
    }

    /**
     * В цикле ждет сообщение от клиента, проверяет тип сообщения и реализовывает
     * соответствующую логику отправки сообщений
     *
     * @throws IOException в случае если поток чтения недоступен
     */
    public void readMessages() throws IOException {
        while (true) {
            String strFromClient = in.readUTF();
            System.out.println("от " + nick + ": " + strFromClient);
            if (strFromClient.equals("/end")) {
                return;
            } else if (strFromClient.startsWith("/nick")) {
                nickChange(strFromClient);
            } else if (strFromClient.startsWith("/w")) {
                personalMessage(strFromClient);
            } else {
                engine.broadcastMsg(nick + ": " + strFromClient);
            }
        }
    }

    /**
     *
     * @param strFromClient строка от клиента которая начинается с /w отправляет личное сообщение адресату
     */
    private void personalMessage(String strFromClient) {
        String[] personal = strFromClient.split(" ");
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < personal.length; i++) {
            message.append(personal[i]).append(" ");
        }
        engine.personalMsg(message.toString(), personal[1], this);
    }

    /**
     *
     * @param strFromClient строка от клиента которая начинается с /nick запускает процедуру смены ника
     */
    private void nickChange(String strFromClient) {
        String[] params = strFromClient.split(" ");
        StringBuilder temp = new StringBuilder();
        for (int i = 3; i < params.length; i++) {
            temp.append(params[i]).append(" ");
        }
        String possibleNick = engine.getAuthService().changeNickByLoginAndPass(params[1], params[2], temp.toString().trim());
        if (!engine.isNickBusy(possibleNick) && !possibleNick.equals("") && !possibleNick.equals("decline")) {
            String tempNick = nick;
            nick = possibleNick;
            engine.broadcastMsg(tempNick + " поменял ник на " + nick);
        } else {
            sendMsg("Ник занят!");
        }
    }

    /**
     * Отправка сообщения текущему клиенту
     *
     * @param msg сообщение
     */

    public void sendMsg(String msg) {
        try {
            out.writeUTF(getDate() + " " + msg);
        } catch (IOException e) {
            System.err.println("Fault send message");
        }
    }

    /**
     * Выполняется в блоке finally, закрывает все потоки и сокет
     */

    public void closeConnection() {
        engine.unsubscribe(this);
        engine.broadcastMsg(nick + " вышел из чата");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает временные рамки для прохождения процедуры аутентификации
     * в случае, когда не выполняется аутентификация за указанное время, ловится
     * исключение TimeoutException в котором клиенту отправляется сообщение об истечении
     * времени для аутентификации и вызывается closeConnection(); разрывающий соединение.
     * @variable long timeOut
     */
    public void authTimeout() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<?> future = executor.submit(() -> {
            try {
                authentication();
            } catch (IOException e) {
                System.err.println("Fault when running authTimeout auth");
            }
        });
        executor.shutdown();

        try {
            future.get(timeOut, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted when authTimeout");
        } catch (ExecutionException e) {
            System.err.println("Execution ex when authTimeout");
        } catch (TimeoutException e) {
            future.cancel(true);
            sendMsg(String.format("Тайм аут аутентификации %d сек.", timeOut));
            closeConnection();
        }
    }

    public String getNick() {
        return nick;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

}
