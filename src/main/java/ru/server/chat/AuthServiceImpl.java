package ru.server.chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private List<User> users;
    private Statement statement;
    private final String url = "jdbc:mysql://localhost:3306/chatusers";
    private final String username = "root";
    private final String password = "985632";
    private Connection conn;

    @Override
    public void stop() {
        try {
            assert statement != null;
            statement.close();
            System.err.println("Auth service stopped");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void start() {
        statement = getStatement();
        users = new ArrayList<>();
        try {
            assert statement != null;
            ResultSet rs = statement.executeQuery(SqlQueu.getAllUsers());
            while (rs.next()) {
                users.add(new User(
                        rs.getString(2)
                        , rs.getString(3)
                        , rs.getString(4)));
            }

            System.out.println(users);
        } catch (SQLException e) {
            System.err.println("Unable to get users list");
        }
    }

    @Override
    public String getNickByLoginAndPass(String log, String pass) {
        for (User u : users) {
            if (u.getLogin().equals(log) && u.getPassword().equals(pass)) return u.getNick();
        }
        return "";
    }

    @Override
    public String changeNickByLoginAndPass(String log, String pass, String possibleNick) {
        for (User u : users) {
            if (u.getNick().equals(possibleNick)) return "decline";
        }
        try {
            statement.executeUpdate(SqlQueu.nickChangeByLoginAndPass(log, pass, possibleNick));
            return possibleNick;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
      return "";
    }

    private Statement getStatement() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to DB successful");
            return conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Unable to establish connection to DB");
        }
        return null;
    }
}
