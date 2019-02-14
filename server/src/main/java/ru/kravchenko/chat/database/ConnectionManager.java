package ru.kravchenko.chat.database;

import lombok.SneakyThrows;

import java.sql.*;

/**
 * @author Roman Kravchenko
 */
public class ConnectionManager implements AutoService{

    private Connection connection;

    private Statement statement;

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public String getNickname(String login, String password) {
        ResultSet rs = statement.executeQuery(String.format("SELECT * FROM user WHERE login = '%s' AND password = '%s'", login, password));
        if (!rs.next()) return null;
        System.out.println(rs.getString(4));
        return rs.getString(4);
    }

    @Override
    public void stop() {
        disconnect();
    }

    @Override
    public boolean start() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userchat", "root", "root");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

}
