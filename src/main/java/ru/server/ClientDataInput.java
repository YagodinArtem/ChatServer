package ru.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientDataInput implements Runnable {

    private final DataInputStream in;

    public ClientDataInput(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println(in.readUTF());
            }
        } catch (IOException e) {
            System.err.println("Socket exception");
            e.printStackTrace();
        }
    }
}
