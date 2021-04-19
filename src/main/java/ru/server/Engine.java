package ru.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Engine {

    public void serverInitAndProcess() throws IOException {
        int serverPort = 8189;
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socket = serverSocket.accept();

            Thread dataInput = new Thread(new ClientDataInput(socket));
            Thread dataOutput = new Thread(new ServerDataOutput(socket));
            dataInput.start();
            dataOutput.start();

        } catch (SocketException e) {
            System.err.println("Socket Exception");
        }
    }
}