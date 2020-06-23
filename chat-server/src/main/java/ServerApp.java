public class ServerApp {
    private DataBase dataBase;
    public static void main(String[] args) {
        DataBase dataBase = DataBase.getInstance();
        new MyServer(dataBase);
    }
}