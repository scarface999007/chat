import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final int PORT = 8189;
    private static final String HOST = "localhost";
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    @FXML
    public ListView<String> listViewMessage;
    @FXML
    public ListView<String> listViewContact;

    @FXML
    private javafx.scene.control.Button closeButton;

    public TextField textField;

    public void sendMessage(){
        if(!socket.isClosed()){
            if(!textField.getText().isEmpty()){
                try {
                    out.writeUTF(textField.getText());
                    listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + textField.getText());
                    textField.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void getClientsNamesReq(){
        if(!socket.isClosed()){
            try {
                out.writeUTF("/getClientNames");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(ActionEvent actionEvent) {
        sendMessage();
    }

    public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")){
            sendMessage();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            openConnection();
            listViewMessage.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            System.out.println("INITIALIZE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws IOException {
        try {
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String msgFromServer = in.readUTF();
                        if(msgFromServer.equalsIgnoreCase("/end")){
                            closeConnection();
                            Platform.exit();
                            break;
                        }
                        updateContactList(msgFromServer);
                        if(!msgFromServer.startsWith("/clients")) {
                            listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + msgFromServer);
                        }
                    }
                    while (true) {
                        if(socket.isClosed()){
                            break;
                        }
                        String msgFromServer = in.readUTF();
                        updateContactList(msgFromServer);
                        if(msgFromServer.equalsIgnoreCase("/end")){
                            closeConnection();
                            Platform.exit();
                            break;
                        }
                        if(!msgFromServer.startsWith("/clients")) {
                            listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + msgFromServer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e){
            System.out.println("Не удалось подключиться к серверу");
            e.printStackTrace();
        }
    }

    public void updateContactList(String msgFromServer){
        if(msgFromServer.startsWith("/clients")){
            listViewContact.getItems().clear();
            String[] nick = msgFromServer.split(" ");
            for (int i = 1; i < nick.length; i++) {
                listViewContact.getItems().add(nick[i].trim());
            }
        }
    }

    public void closeButtonAction(ActionEvent actionEvent) throws IOException {
        out.writeUTF("/end");
        out.flush();
    }

    public void closeConnection(){
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
