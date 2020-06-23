import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    /*private class Entry{
        private String login;
        private String pass;
        private String nick;

        Entry(String login, String pass, String nick){
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }*/

    private List<DataBaseEntity> entityList;
    private DataBase dataBase;

    BaseAuthService(DataBase dataBase){
        this.dataBase = dataBase;
        entityList = new ArrayList<>();
    }

    private void addEntryFromDB(String login, String password) throws SQLException {
        entityList.add(dataBase.getEntry(login, password));
    }

    @Override
    public void start() {
        System.out.println("Сервис аутенцификации запущен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        addEntryFromDB(login, pass);
        for (DataBaseEntity o : entityList){
            if(o.getLogin().equals(login) && o.getPass().equals(pass)) return o.getNick();
        }
        return null;
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутенцификации остановлен");
    }
}
