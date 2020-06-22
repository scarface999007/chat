import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private class Entry{
        private String login;
        private String pass;
        private String nick;

        Entry(String login, String pass, String nick){
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private List<Entry> entryList;

    BaseAuthService(){
        entryList = new ArrayList<>();
        entryList.add(new Entry("login1", "pass1", "nick1"));
        entryList.add(new Entry("login2", "pass2", "nick2"));
        entryList.add(new Entry("login3", "pass3", "nick3"));
    }

    @Override
    public void start() {
        System.out.println("Сервис аутенцификации запущен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry o : entryList){
            if(o.login.equals(login) && o.pass.equals(pass)) return o.nick;
        }
        return null;
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутенцификации остановлен");
    }
}
