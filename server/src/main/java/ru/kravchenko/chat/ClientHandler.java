package ru.kravchenko.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Roman Kravchenko
 */
public class ClientHandler {

    private Server server;

    private Socket socket;

    private DataInputStream in;

    private DataOutputStream out;

    public String getNickName() {
        return nickName;
    }

    private String nickName;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String string = in.readUTF();
                        if(string.startsWith("/auth")) {
                            String[] token = string.split("\\s");
                            if (token.length == 3) {
                                String nickFromDB = server.getAutoService().getNickname(token[1], token[2]);
                                if (nickFromDB != null) {
                                    nickName = nickFromDB;
                                    if(!server.isNickBusy(nickName)) {
                                        sendMsg("/authok " + token[1]);
                                        server.substribe(this);
                                        break;
                                    } else { sendMsg("Учётная запись занята");
                                    }
                                } else {
                                    sendMsg("Не верный логин/пароль");
                                    System.out.println("Такой учётной записи не существует");
                                }
                            }
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/")) {
                            if (str.equals("/end")) {
                                break;
                            }
                            if(str.startsWith("/w")) {
                                String[] tokens = str.split("\\s", 3);
                                server.sendPrivateMessage(this, tokens[1], tokens[2]);
                            }

                        } else {
                            server.broadcaset(nickName + ": " + str);
                        }
                        System.out.println("Сообщение от клиента: " + str);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.undsubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
