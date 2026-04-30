package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class ClientView {

    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private VBox vbox_messages;
    @FXML
    private ScrollPane sp_main;

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", port);

                bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );

                Platform.runLater(() -> {
                    addMessage("Connected to server on port " + port);
                });

                listenForMessages();

            } catch (IOException e) {
                Platform.runLater(() -> {
                    addMessage("Could not connect to server: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void initialize() {
        button_send.setOnAction(event -> {
            sendMessage(tf_message.getText());
            tf_message.clear();
        });

        tf_message.setOnAction(event -> {
            sendMessage(tf_message.getText());
            tf_message.clear();
        });
    }



    private void sendMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            addMessage("Me: " + message);

        } catch (IOException e) {
            addMessage("Message failed to send.");
        }
    }

    private void listenForMessages() {
        new Thread(() -> {
            String messageFromServer;

            try {
                while ((messageFromServer = bufferedReader.readLine()) != null) {
                    String finalMessage = messageFromServer;

                    Platform.runLater(() -> {
                        addMessage("Server: " + finalMessage);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    addMessage("Disconnected from server.");
                });
            }
        }).start();
    }

    private void addMessage(String message) {
        Label label = new Label(message);
        vbox_messages.getChildren().add(label);

        sp_main.setVvalue(1.0);
    }
}
