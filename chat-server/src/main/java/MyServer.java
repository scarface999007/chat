import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private static final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public ArrayList<String> getClientsName(){
        ArrayList<String> names = new ArrayList<>();
        for(ClientHandler o : clients) {
            names.add(o.getName());
        }
        return names;
    }

    public AuthService getAuthService(){
        return authService;
    }

    MyServer(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            authService = new BaseAuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true){
                System.out.println("Server started!");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(authService != null) {
                authService.stop();
            }
        }
    }

    public synchronized boolean isNickBusy(String nick){
        for (ClientHandler o : clients){
            if(o.getName().equals(nick)){
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String msg, ClientHandler clientHandler){
        for (ClientHandler o : clients){
            if(!o.equals(clientHandler)){
                o.sendMsg(msg);
            }
        }
    }

    public synchronized void sendClientsNames(ClientHandler clientHandler){
        StringBuilder sb = new StringBuilder("/clients");
        for (ClientHandler o : clients) {
            sb.append(" " + o.getName());
        }
        for (ClientHandler o : clients){
            o.sendMsg(sb.toString());
        }
    }

    public synchronized void privateMessage(ClientHandler from, String msg, String name){
        for (ClientHandler o : clients){
            if(o.getName().equals(name)){
                o.sendMsg("от " + from.getName() + ": " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником " + name + " нет в чате");
    }

    public synchronized void unsubscribe(ClientHandler o){
        clients.remove(o);
        sendClientsNames(o);
    }

    public synchronized void subscribe(ClientHandler o){
        clients.add(o);
        sendClientsNames(o);
    }

}
