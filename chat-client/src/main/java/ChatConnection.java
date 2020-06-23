import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatConnection {

    private static final int PORT = 8189;
    private static final String HOST = "localhost";
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket = null;
    private ControllerChat controllerChat;
    SimpleDateFormat dateFormat = new SimpleDateFormat();
    private LogChat logChat;
    private String name;

    private static ChatConnection instance;

    public static synchronized ChatConnection getInstance(){
        if(instance == null){
            instance = new ChatConnection();
        }
        return instance;
    }

    ChatConnection(){
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void auth(String login, String pass) throws IOException {
        showWindowsChat();
        StringBuilder sb = new StringBuilder();
        sb.append("/auth").append(" ").append(login).append(" ").append(pass);
        sendMessage(sb.toString());
    }

    /*public void showWindowsChat() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ControllerLogin.class.getResource("main.fxml"));
        Parent winChat = fxmlLoader.load();
        controllerChat = fxmlLoader.getController();
        Main.stageForm.setScene(new Scene(winChat));
        Main.stageForm.centerOnScreen();
        Main.stageForm.setTitle("Messenger");
        Main.stageForm.setOnCloseRequest(e -> {
            System.out.println("Close form chat");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chat_client/src/main/java/" + login + ".log"))) {
                oos.writeObject(userLog);
                oos.flush();
                oos.close();
                sendMessage("/exit", login, null, null);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }*/

    public void showWindowsChat() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("sample.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        controllerChat = fxmlLoader.getController();
        stage.setOnCloseRequest(controllerChat.getCloseEventHandler());
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
                            closeSocket();
                            Platform.exit();
                            break;
                        }
                        if(msgFromServer.equalsIgnoreCase("Учетная запись уже используется") || msgFromServer.equalsIgnoreCase("Неверные логин/пароль")){
                            controllerChat.listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + msgFromServer);
                            Thread.sleep(1000);
                            closeConnection();
                        }
                        if(msgFromServer.startsWith("/authok")){
                            String[] parts = msgFromServer.split("\\s");
                            name = parts[1];
                            if(logChat == null){
                                logChat = new LogChat(name);
                            }
                            if(logChat.read() != null){
                                controllerChat.listViewMessage.getItems().addAll(logChat.read());
                            }

                        }
                        updateContactList(msgFromServer);
                        if(!msgFromServer.startsWith("/clients")) {
                            logChat.write(dateFormat.format(new Date()) + ": " + msgFromServer);
                            controllerChat.listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + msgFromServer);
                        }
                    }
                    while (true) {
                        if(socket.isClosed()){
                            break;
                        }
                        String msgFromServer = in.readUTF();
                        updateContactList(msgFromServer);
                        if(msgFromServer.equalsIgnoreCase("/end")){
                            logChat.write(dateFormat.format(new Date()) + ": " + msgFromServer);
                            closeSocket();
                            Platform.exit();
                            break;
                        }
                        if(!msgFromServer.startsWith("/clients")) {
                            logChat.write(dateFormat.format(new Date()) + ": " + msgFromServer);
                            controllerChat.listViewMessage.getItems().add(dateFormat.format(new Date()) + ": " + msgFromServer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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

    public void sendMessage(String text){
        if(!socket.isClosed()){
            try {
                if(logChat != null){
                    logChat.write(dateFormat.format(new Date()) + ": " + text);
                }
                out.writeUTF(text);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateContactList(String msgFromServer){
        if(msgFromServer.startsWith("/clients")){
            controllerChat.listViewContact.getItems().clear();
            String[] nick = msgFromServer.split(" ");
            for (int i = 1; i < nick.length; i++) {
                controllerChat.listViewContact.getItems().add(nick[i].trim());
            }
        }
    }
    public void closeConnection(){
        sendMessage("/end");
    }

    public void closeSocket() throws IOException {
        out.writeUTF("/closeSocket");
        out.flush();
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
