package ru.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerDataOutput implements Runnable {

    private final DataOutputStream out;
    private final BufferedReader consoleReader;

    public ServerDataOutput(Socket socket) throws IOException {
        out = new DataOutputStream(socket.getOutputStream());
        consoleReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        while (true) {
            try {
                String output = consoleReader.readLine();
                out.writeUTF(output);
            } catch (IOException e) {
                System.err.println("ServerDataOutput exception");
            }
        }
    }
}
