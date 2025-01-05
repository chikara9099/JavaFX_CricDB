package ipl.cricketdb;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import IPLDatabase.Player;

public class AddPlayerController extends BaseController {
    @FXML
    private TextField countryField;
    @FXML
    private TextField ageField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField clubField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField jerseyNumberField;
    @FXML
    private TextField weeklySalaryField;
    @FXML
    private void handleAddPlayer() throws Exception {
        String name = nameField.getText();
        String country = countryField.getText();
        int age = Integer.parseInt(ageField.getText());
        double height = Double.parseDouble(heightField.getText());
        String club = clubField.getText();
        String position = positionField.getText();
        int jerseyNumber;
        if(jerseyNumberField.getText().isEmpty())
        {
            jerseyNumber = -1;
        }
        else jerseyNumber = Integer.parseInt(jerseyNumberField.getText());
        int weeklySalary = Integer.parseInt(weeklySalaryField.getText());

        Player newPlayer = new Player(name, country, age, height, club, position, jerseyNumber, weeklySalary);
        mainApp.getController().addPlayer(newPlayer);

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}

