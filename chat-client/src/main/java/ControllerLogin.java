import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {

    public TextField loginText;
    public TextField passwordText;

    ChatConnection chatConnection;


    public void enter(ActionEvent actionEvent) throws IOException {
        if(!loginText.getText().isEmpty() && !passwordText.getText().isEmpty()){
            chatConnection.auth(loginText.getText(), passwordText.getText());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Логин и пароль не могут быть пустыми.");
        }
    }

    public void keyPressed(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode().getName().equals("Enter")){
            if(!loginText.getText().isEmpty() && !passwordText.getText().isEmpty()){
                chatConnection.auth(loginText.getText(), passwordText.getText());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Логин и пароль не могут быть пустыми.");
            }
        }
    }

    public static void showAlert(Alert.AlertType alertType, String title, String header) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    private final javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            chatConnection.closeConnection();
            System.exit(1);
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatConnection = ChatConnection.getInstance();
    }
}
