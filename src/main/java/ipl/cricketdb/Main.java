package ipl.cricketdb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage stage;
    static String userName;
    private MainController controller;
    public static String getUserName() {
        return userName;
    }
    public Stage getStage() {
        return stage;
    }
    public MainController getController() {
        return controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        showLoginPage();
    }

    public void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setMain(this);
        controller.init();
        Scene scene = new Scene(root, 879, 664);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public void showHomePage(String username) throws Exception {
        userName = username;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setMain(this);
        controller.init(username);
        Scene scene = new Scene(root, 879, 664);
        stage.setTitle("Cricket Player Database");
        stage.setScene(scene);
        stage.show();
    }

    public void showClubPage() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("club-view.fxml"));
        Parent root = fxmlLoader.load();
        ClubController controller = fxmlLoader.getController();
        controller.setMain(this);
        controller.init(userName);
        Scene scene = new Scene(root, 879, 664);
        stage.setTitle("My Club Database");
        stage.setScene(scene);
        stage.show();
    }

    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Incorrect Credentials");
        alert.setHeaderText("Incorrect Credentials");
        alert.setContentText("The username and password you provided is not correct.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}

