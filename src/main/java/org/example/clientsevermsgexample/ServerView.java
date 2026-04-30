package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private Button button_send;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    @FXML
    private ScrollPane sp_main;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);

                Platform.runLater(() ->
                        addMessage("Server started on port " + port + "... Waiting for client...")
                );

                socket = serverSocket.accept();

                Platform.runLater(() ->
                        addMessage("Client connected!")
                );

                bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );

                listenForMessages();

            } catch (IOException e) {
                Platform.runLater(() ->
                        addMessage("Server error: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void initialize() {
        button_send.setOnAction(e -> {
            sendMessage(tf_message.getText());
            tf_message.clear();
        });

        tf_message.setOnAction(e -> {
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
            String messageFromClient;

            try {
                while ((messageFromClient = bufferedReader.readLine()) != null) {
                    String finalMessage = messageFromClient;

                    Platform.runLater(() -> {
                        addMessage("Client: " + finalMessage);
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    addMessage("Client disconnected.");
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
