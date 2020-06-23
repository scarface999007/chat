import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private String name;
    DataInputStream in;
    DataOutputStream out;
    private boolean isAuthOk = false;
    private static final int TIMEOUT = 120 * 1000;
    //private static final int TIMEOUT = 5000;

    ClientHandler(MyServer myServer, Socket socket) throws IOException {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public String getName() {
        return name;
    }

    public void authentication() throws IOException {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try{
                    synchronized (this){
                        if(name.equals("")) {
                            System.out.println("TIME OUT");
                            sendMsg("/end");
                            Thread.sleep(100);
                            in.close();
                            out.close();
                            socket.close();
                        }
                    }
                } catch (InterruptedException | IOException e){
                    e.printStackTrace();
                }
            }
        }, TIMEOUT);
        while (true){

            String str = in.readUTF();
            if(str.equals("/end")){
                sendMsg("/end");
                break;
            }
            if(str.equals("/closeSocket")){
                break;
            }
            if(str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if(nick != null) {
                    if(!myServer.isNickBusy(nick)){
                        sendMsg("/authok " + nick);
                        name = nick;
                        myServer.broadcastMessage(name + " зашел в чат", this);
                        myServer.subscribe(this);
                        isAuthOk = true;
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (isAuthOk){
            String strFromClient = in.readUTF();
            System.out.println("от " + name + ": " + strFromClient);
            if(strFromClient.equals("/end")){
                sendMsg("/end");
                break;
            }
            if(strFromClient.startsWith("/w")){
                String[] parts = strFromClient.split("\\s");
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < parts.length; i++) {
                    sb.append(parts[i] + " ");
                }
                myServer.privateMessage(this, "pm from " + name + ": " + sb.toString().trim() ,parts[1]);
            } else {
                myServer.broadcastMessage(name + ": " + strFromClient, this);
            }
        }
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        myServer.unsubscribe(this);
        myServer.broadcastMessage(name + " вышел из чата", this);
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
