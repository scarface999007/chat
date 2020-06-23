import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Messenger");

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        ControllerLogin controllerLogin = loader.getController();
        primaryStage.setOnCloseRequest(controllerLogin.getCloseEventHandler());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
