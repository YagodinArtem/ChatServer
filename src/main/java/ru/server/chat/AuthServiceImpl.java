package ru.server.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthServiceImpl implements AuthService {

    private List<User> users;
    private final String url = "jdbc:mysql://localhost:3306/chatusers";
    private final String username = "root";
    private final String password = "985632";
    private Connection connection;
    private SqlQue sql;
    private static final Logger LOG = LogManager.getLogger(AuthServiceImpl.class.getName());


    @Override
    public void start() {
        initConnectionAndSqlQue();
        users = new ArrayList<>();
        try {
            ResultSet rs = sql.getGetAllUsers().executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getString(2)
                        , rs.getString(3)
                        , rs.getString(4)));
            }
        } catch (SQLException e) {
            LOG.error("Unable to get users list");
        } catch (NullPointerException n) {
            LOG.fatal("Unable to run authorization service");
        }
    }

    /**
     * закрывает соединения с БД и все PreparedStatement из объекта клсса SqlQueu
     */
    @Override
    public void stop() {
        try {
            connection.close();
            sql.closeAllStatements();
            LOG.info("Auth service stopped");
        } catch (SQLException e) {
            LOG.error("Unable to close connection");
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
            sql.getNickChangeAndSetParameters(log, pass, possibleNick).executeUpdate();
            return possibleNick;
        } catch (SQLException e) {
            LOG.error("Unable to changeNick");
        }
      return "";
    }

    private void initConnectionAndSqlQue() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            sql = new SqlQue(connection);
            LOG.info("Connection to DB successful");
        } catch (SQLException e) {
            LOG.error("Unable to establish connection to DB");
        }
    }
}
