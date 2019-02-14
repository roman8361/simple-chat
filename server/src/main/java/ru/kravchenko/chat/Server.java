package ru.kravchenko.chat;

import ru.kravchenko.chat.database.AutoService;
import ru.kravchenko.chat.database.ConnectionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * @author Roman Kravchenko
 */
public class Server {

    private Vector<ClientHandler> clientHandlers;

    AutoService autoService;

    public AutoService getAutoService() {
        return autoService;
    }

    public Server() {
        autoService = new ConnectionManager();
        if (!autoService.start()) {
            System.out.println("Сервис вторизации не запустили.");
            System.exit(0);
        }
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            clientHandlers = new Vector<>();
            System.out.println("Сервер запущен...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент присоединился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void substribe(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
        broadcastClientList();
    }

    public void undsubscribe(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        broadcastClientList();
    }

    public void broadcaset (String message) {
        for (ClientHandler c: clientHandlers) {
            c.sendMsg(message);
        }
    }

    public void sendPrivateMessage (ClientHandler src, String dstNickName, String message) {
        for (ClientHandler c: clientHandlers) {
            if (c.getNickName().equals(dstNickName)) {
                c.sendMsg("От " + src.getNickName() + ": " + message);
                src.sendMsg("кому " + dstNickName + ": " + message);
                return;
            }
        }
        src.sendMsg("пользователя " + dstNickName + "нет ...");
    }

    public boolean isNickBusy(String nickName) {
        for (ClientHandler c: clientHandlers) {
            if (c.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/clientslist ");
        for (ClientHandler c: clientHandlers) {
            stringBuilder.append(c.getNickName()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String out = stringBuilder.toString();
        for (ClientHandler c: clientHandlers) {
            c.sendMsg(out);
        }
    }

}
