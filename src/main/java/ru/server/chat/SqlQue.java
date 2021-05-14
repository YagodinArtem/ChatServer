package ru.server.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlQue {

    private final String chatUsers = "chatusers.users";
    private PreparedStatement addUser;
    private PreparedStatement getAllUsers;
    private PreparedStatement nickChangeByLoginAndPass;
    private static final Logger LOG = LogManager.getLogger(SqlQue.class.getName());

    /**
     * Конструктор при создании экземпляра объекта, инициализирует PreparedStatement
     *
     * @param connection экземпляр Connection из класса AuthServiceImpl
     * @throws SQLException
     */
    public SqlQue(Connection connection) throws SQLException {
        getAllUsers = connection.prepareStatement("SELECT * FROM " + chatUsers);
        nickChangeByLoginAndPass = connection.prepareStatement("UPDATE " + chatUsers + " SET nick = ? " +
                "WHERE id > 0 AND login = ? AND password = ?");
        addUser = connection.prepareStatement("INSERT INTO " + chatUsers + " (login, password, nick) VALUES (?, ?, ?)");
    }


    public PreparedStatement getAddUserAndSetParameters(String login, String pass, String nick) {
        try {
            addUser.setString(1, login);
            addUser.setString(2, pass);
            addUser.setString(3, nick);
        } catch (SQLException e) {
            LOG.error("Unable to set parameters getAddUser");
        }
        return addUser;
    }

    public PreparedStatement getGetAllUsers() {
        return getAllUsers;
    }

    public PreparedStatement getNickChangeAndSetParameters(String login, String pass, String nick) {
        try {
            nickChangeByLoginAndPass.setString(1, nick);
            nickChangeByLoginAndPass.setString(2, login);
            nickChangeByLoginAndPass.setString(3, pass);
        } catch (SQLException e) {
            LOG.error("Unable to set parameters getNickChange");
        }
        return nickChangeByLoginAndPass;
    }

    public void closeAllStatements() {
        try {
            addUser.close();
            getAllUsers.close();
            nickChangeByLoginAndPass.close();
        } catch (SQLException e) {
            LOG.error("Unable to close statements");
        }
    }
}
