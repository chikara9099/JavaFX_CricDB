package ipl.cricketdb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import java.util.Objects;

public class LoginController extends BaseController {
    @FXML
    private TextField userText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private Button resetButton;
    @FXML
    private Button loginButton;
    @FXML
    void loginAction(ActionEvent event) {
        String userName = userText.getText();
        String password = passwordText.getText();
        try {
            SocketWrapper socketWrapper = null;
            try {
                socketWrapper = new SocketWrapper(SERVER_ADDRESS, SERVER_PORT);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cannot connect to Server");
                alert.setHeaderText("Cannot connect to Server");
                alert.setContentText("Please wait until the server is back online.");
                alert.showAndWait();
            }
            socketWrapper.write("LOGIN");
            socketWrapper.write(userName);
            socketWrapper.write(password);
            String response = (String) socketWrapper.read();
            if ("SUCCESS".equals(response)) {
                mainApp.showHomePage(userName);
            } else {
                mainApp.showAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void resetAction(ActionEvent event) {
        userText.setText(null);
        passwordText.setText(null);
    }

    public void init() {
        Image img = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Logo.png")));
        image.setImage(img);
    }
}
