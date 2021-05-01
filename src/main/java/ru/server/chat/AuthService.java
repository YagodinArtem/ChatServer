package ru.server.chat;

public interface AuthService {

    /**
     * Stop the authorisation service
     */
    void stop();


    /**
     * Start the authorisation service
     */
    void start();

    /**
     *
     * @return check that database contains such user login - pass and returns nick
     */
    String getNickByLoginAndPass(String log, String pass);

    /**
     *
     * @return possible nick to change
     */
    String changeNickByLoginAndPass(String log, String pass, String possibleNick);
}
