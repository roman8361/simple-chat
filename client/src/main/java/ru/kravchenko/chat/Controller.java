package ru.kravchenko.chat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Roman Kravchenko
 */

public class Controller implements Initializable {

    private Socket socket;

    private DataInputStream inputStream;

    private DataOutputStream outputStream;

    private boolean authorized;

    public void setAutorized(boolean autorized) {
        this.authorized = autorized;
        if (autorized) {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            messagePanel.setVisible(true);
            messagePanel.setManaged(true);
        } else {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            messagePanel.setVisible(false);
            messagePanel.setManaged(false);
        }
    }

    @FXML
    TextField messageField, loginField;

    @FXML
    TextArea chatArea;

    @FXML
    HBox authPanel, messagePanel;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> clientsList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAutorized(false);
        ObservableList<String> list = FXCollections.observableArrayList();
        clientsList.setItems(list);
        clientsList.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                messageField.clear();
                messageField.appendText("/w ");
                messageField.appendText(clientsList.getSelectionModel().getSelectedItem());
                messageField.appendText(" ");
                messageField.requestFocus();
                messageField.end();

            }
        });
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            Thread tRead = new Thread(() -> {
                try {
                    while (true) {
                        String str = inputStream.readUTF();
                        if (str.startsWith("/authok ")) {
                            chatArea.clear();
                            chatArea.appendText("Welcome to Simple chat. " + str.split("\\s")[1] + "\n");
                            setAutorized(true);
                            break;
                        }
                        chatArea.appendText(str + "\n");
                    }
                    while (true) {
                        String str = inputStream.readUTF();
                        if(str.startsWith("/")) {
                            if (str.startsWith("/clientslist ")) {
                                String[] tokens = str.split("\\s");
                                Platform.runLater(() -> {
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);
                                    }
                                });
                            }

                        } else {
                            chatArea.appendText(str + "\n");
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            });
            tRead.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {
            outputStream.writeUTF(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            inputStream.close();
        } catch (Exception e) {
        }
        try {
            outputStream.close();
        } catch (Exception e) {
        }
        try {
            socket.close();
        } catch (Exception e) {
        }
    }

    public void sendAuthorizationMessage() {
        if (socket == null || socket.isClosed()) connect();
        try {
            outputStream.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
