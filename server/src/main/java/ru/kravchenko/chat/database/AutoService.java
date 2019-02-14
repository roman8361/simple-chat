package ru.kravchenko.chat.database;

/**
 * @author Roman Kravchenko
 */
public interface AutoService {

    String getNickname(String login, String password);

    void stop();

    boolean start();

}
