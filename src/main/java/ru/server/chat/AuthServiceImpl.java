package ru.server.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private List<User> users;

    @Override
    public void stop() { System.err.println("Auth service stopped"); }

    @Override
    public void start() {
        users = new ArrayList<>(Arrays.asList(
                new User("root", "123", "root")
                , new User("login1", "pass1", "nick1")
                , new User("login2", "pass2", "nick2")));
    }

    @Override
    public String getNickByLoginAndPass(String log, String pass) {
        for (User u : users) {
            if (u.getLogin().equals(log) && u.getPassword().equals(pass)) return u.getNick();
        }
        return "";
    }
}
