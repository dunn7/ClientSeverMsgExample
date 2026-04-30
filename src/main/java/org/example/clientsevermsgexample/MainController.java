package org.example.clientsevermsgexample;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class MainController implements Initializable {
    @FXML
    private ComboBox dropdownPort;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dropdownPort.getItems().addAll("7",     // ping
                "13",     // daytime
                "21",     // ftp
                "23",     // telnet
                "71",     // finger
                "80",     // http
                "119",     // nntp (news)
                "161"      // snmp);
        );
    }

    @FXML
    private Button clearBtn;



    @FXML
    private TextArea resultArea;

    @FXML
    private Label server_lbl;

    @FXML
    private Button testBtn;

    @FXML
    private Label test_lbl;

    @FXML
    private TextField urlName;

    @FXML
    void checkConnection(ActionEvent event) {

        String host = urlName.getText();
        int port = Integer.parseInt(dropdownPort.getValue().toString());

        try {
            Socket sock = new Socket(host, port);
            resultArea.appendText(host + " listening on port " + port + "\n");
            sock.close();
        } catch (UnknownHostException e) {
            resultArea.setText(String.valueOf(e) + "\n");
            return;
        } catch (Exception e) {
            resultArea.appendText(host + " not listening on port "
                    + port + "\n");
        }

    }

    @FXML
    void clearBtn(ActionEvent event) {
        resultArea.setText("");
        urlName.setText("");

    }

    @FXML
    void startServer(ActionEvent event) {
        try {
            if (dropdownPort.getValue() == null) {
                resultArea.appendText("Please select a port first.\n");
                return;
            }

            int port = Integer.parseInt(dropdownPort.getValue().toString());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Server-view.fxml"));
            Parent root = loader.load();

            ServerView serverView = loader.getController();
            serverView.setPort(port);
            serverView.startServer();

            Stage stage = new Stage();
            stage.setTitle("Server");
            stage.setScene(new Scene(root));
            stage.show();
            Label lb11 = new Label("Server");
            lb11.setLayoutX(100);
            lb11.setLayoutY(100);

        } catch (Exception e) {
            resultArea.appendText("Error starting server: " + e.getMessage() + "\n");
        }
    }

    String message;

    @FXML
    void startClient(ActionEvent event) {
        try {
            if (dropdownPort.getValue() == null) {
                resultArea.appendText("Please select a port first.\n");
                return;
            }

            int port = Integer.parseInt(dropdownPort.getValue().toString());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("client-view.fxml"));
            Parent root = loader.load();

            ClientView clientView = loader.getController();
            clientView.setPort(port);
            clientView.connectToServer();

            Stage stage = new Stage();
            stage.setTitle("Client");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            resultArea.appendText("Error starting client: " + e.getMessage() + "\n");
        }
    }

}
