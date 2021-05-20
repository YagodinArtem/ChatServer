package ru.server.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Engine {

    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;
<<<<<<< HEAD
    private final ExecutorService executorService;
=======
    private static final Logger LOG = LogManager.getLogger(Engine.class.getName());
>>>>>>> origin

    public Engine() {
        LOG.info("Server started");
        clients = new ArrayList<>();
        authService = new AuthServiceImpl();
        executorService = Executors.newCachedThreadPool();
        authService.start();
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                LOG.info("Awaiting client connection");
                Socket socket = server.accept();
                LOG.info("Client connected");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            LOG.error("Fault server initialize");
        } finally {
            LOG.info("Auth service stopped");
            authService.stop();
        }
    }


    public void broadcastMsg(String msg) {
        for (ClientHandler ch : clients) {
            LOG.trace("отправили для " + ch.getNick());
            ch.sendMsg(msg);
        }
    }

    public void personalMsg(String msg, String msgRecipient, ClientHandler msgSender) {
        for (ClientHandler c : clients) {
            if (c.getNick().equals(msgRecipient)) {
                c.sendMsg(String.format("Лично от %s: %s", msgSender.getNick(), msg));
                msgSender.sendMsg(String.format("Лично для %s: %s", msgRecipient, msg));
            }
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler ch : clients) if (ch.getNick().equals(nick)) return true;
        return false;
    }

    public void subscribe(ClientHandler clientHandler) {
        LOG.info(clientHandler.getNick() + " зашел в чат");
        clients.add(clientHandler);
    }

    public void unsubscribe (ClientHandler clientHandler) {
        LOG.info(clientHandler.getNick() + " покинул чат");
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }

<<<<<<< HEAD
    public ExecutorService getExecutorService() {
        return executorService;
    }
=======
>>>>>>> origin
}
