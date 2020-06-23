import java.io.*;
import java.util.ArrayList;

public class LogChat {

    private String userName;
    private String filePath;

    LogChat(String userName){
        this.userName = userName;
        filePath = "history_" + userName + ".txt";
    }

    public void write(String msg){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
            writer.write(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> read(){
        ArrayList<String> log = null;

        try(BufferedReader reader = new BufferedReader((new FileReader(filePath)))){
            log = new ArrayList<>();
            String str;
            while ((str = reader.readLine()) != null){
                log.add(str);
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log;
    }

}
