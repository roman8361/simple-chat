package ru.kravchenko.chat;

import static org.junit.Assert.assertTrue;

import lombok.SneakyThrows;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    Connection connection;
    Statement statement;

    @Test
    @SneakyThrows
    public void connect() {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userchat", "root", "root");
       // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/userchat");

        statement = connection.createStatement();
    }


//    public String getDatePassword () {
//
//
//    }

    @Test
    @SneakyThrows
    public void getDatePassword() {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE id = 1" );
        if (!rs.next()) return;
        System.out.println(rs.getString(3));
    }

    @Test
    @SneakyThrows
    public void getDateNick() {
        connect();
        ResultSet rs = statement.executeQuery("SELECT * FROM user WHERE login = 'roman' AND password = '111' " );
        if (!rs.next()) return;
        System.out.println(rs.getString(4));
    }

    @Test
    @SneakyThrows
    public void addToBD() {
        connect();
        int resultSet = statement.executeUpdate("INSERT INTO user (id, login, password, nick) VALUES (7, 'sony', '333', 'sony13')");
    }

    @Test
    @SneakyThrows
    public void addToBDTest() {
        connect();
        String id = UUID.randomUUID().toString();
        int rs = statement.executeUpdate( "INSERT INTO id_string (id, name, scope) VALUES ('5646464', 'sony', 5)");
    }




//    @Test
//    @SneakyThrows
//    public void insert() {
//        connect();
//        ResultSet re = statement.executeUpdate("insert into userchat.user (`id`, `login`, `password`) VALUES ('6', 'www', 'rrr'));
//
//    }



}
