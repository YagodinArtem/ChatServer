package ru.server.chat;

public interface AuthService {

    /**
     * Stop the authorisation service (connecting to DB in progress)
     */
    void stop();


    /**
     * Start the authorisation service (close connection to DB in progress)
     */
    void start();

    /**
     *
     * @return check that database contains such user login - pass and returns nick
     */
    String getNickByLoginAndPass(String log, String pass);
}
