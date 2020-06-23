import java.sql.*;

public class DataBase {


    private static DataBase instance;
    private Connection connection;
    private Statement statement;

    public static synchronized DataBase getInstance(){
        if(instance == null){
            instance = new DataBase();
        }
        return instance;
    }

    DataBase(){
        try {
            createDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createDB() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try
        {
            connection = DriverManager.getConnection("jdbc:sqlite:chatDB.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(60);  // set timeout to 30 sec.
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS chatDB (id INTEGER PRIMARY KEY AUTOINCREMENT, login STRING, password STRING, nick STRING)");
            insertData("login1", "pass1", "nick1");
            insertData("login2", "pass2", "nick2");
            insertData("login3", "pass3", "nick3");
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public void closeDB(){
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            System.err.println(e);
        }
    }

    public void updateData(){

    }

    public void insertData(String login, String password, String nick) throws SQLException {
        statement.executeUpdate("insert into chatDB (login, password, nick) values(" + "'" + login + "'" + "," + "'" + password + "'" + "," + "'" + nick + "'" + ")");
    }

    public DataBaseEntity getEntry(String login, String password) throws SQLException {
        ResultSet rs = statement.executeQuery("select * from chatDB where login = " + "'" + login + "'" + " AND " + "password = " + "'" + password + "'");
        return new DataBaseEntity(rs.getString("login"), rs.getString("password"), rs.getString("nick"));
    }

}
