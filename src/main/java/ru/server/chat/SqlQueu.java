package ru.server.chat;

public class SqlQueu {

    private static final String chatUsers = "chatusers.users";

    public static String addUser(String login, String password, String nick) {
        return "INSERT INTO "+ chatUsers + " (login, password, nick) VALUES ('"
                + login + "', '"
                + password + "', '"
                + nick + "')";
    }

    public static String getAllUsers() {
        return String.format("SELECT * FROM %s", chatUsers);
    }

    public static String nickChangeByLoginAndPass(String login, String pass, String newNick) {
        return String.format("UPDATE chatusers.users SET nick = " +
                "'%s' WHERE id > 0 && login = " +
                "'%s' && password = '%s'", newNick, login, pass);
    }
}
