package ipl.cricketdb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class LoginController {

    private Main main;

    @FXML
    private TextField userText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button resetButton;

    @FXML
    private Button loginButton;

    @FXML
    private ImageView image;

    @FXML
    private Label message;

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    @FXML
    void loginAction(ActionEvent event) {
        String userName = userText.getText();
        String password = passwordText.getText();

        try{
            SocketWrapper socketWrapper = new SocketWrapper(SERVER_ADDRESS, SERVER_PORT);
            socketWrapper.write("LOGIN");
            socketWrapper.write(userName);
            socketWrapper.write(password);
            String response = (String) socketWrapper.read();
            if ("SUCCESS".equals(response)) {
                main.showHomePage(userName);
            } else {
                main.showAlert();
            }

        } catch (Exception e) {
            e.printStackTrace();
            message.setText("Error: Unable to connect to the server.");
        }
    }

    @FXML
    void resetAction(ActionEvent event) {
        userText.setText(null);
        passwordText.setText(null);
        message.setText("");
    }

    public void init() {
        Image img = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Logo.png")));
        image.setImage(img);
    }

    void setMain(Main main) {
        this.main = main;
    }
}
