package ru.server.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public Engine() {
        clients = new ArrayList<>();
        authService = new AuthServiceImpl();
        authService.start();
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Awaiting client connection");
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            System.err.println("Fault server initialize");
        } finally {
            authService.stop();
        }
    }


    public void broadcastMsg(String msg) {
        for (ClientHandler ch : clients) {
            System.out.println("отправили для " + ch.getNick());
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
        clients.add(clientHandler);
    }

    public void unsubscribe (ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
