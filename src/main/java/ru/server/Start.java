package ru.server;

import java.io.IOException;

public class Start {

    public static void main(String[] args) {
        Engine engine = new Engine();
        try {
            engine.serverInitAndProcess();
        } catch (IOException e) {
            System.err.println("serverInitAndProcess() failure");
            e.printStackTrace();
        }
    }
}
