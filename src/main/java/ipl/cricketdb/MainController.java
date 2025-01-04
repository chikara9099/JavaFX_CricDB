package ipl.cricketdb;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainController extends BaseController{
    @FXML
    public void initialize() {
        positionComboBox.setItems(FXCollections.observableArrayList(null, "Batsman", "Bowler", "Allrounder", "Wicketkeeper"));
        clubComboBox.setItems(FXCollections.observableArrayList(null, "Chennai Super Kings","Delhi Capitals","Gujarat Titans","Kolkata Knight Riders","Lucknow Super Giants","Mumbai Indians","Punjab Kings","Rajasthan Royals","Royal Challengers Bangalore","Sunrisers Hyderabad"));
        countryComboBox.setItems(FXCollections.observableArrayList(null, "India", "Australia", "England", "South Africa", "Bangladesh", "New Zealand","Afghanistan", "West Indies", "South Africa"));
        minSalarySlider.setBlockIncrement(1000000);
        maxSalarySlider.setBlockIncrement(1000000);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));
        try {
            playerManager = new PlayerManager(SERVER_ADDRESS,SERVER_PORT);
            updateTable(playerManager.getPlayers());
            startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addListeners();
    }
    public void init(String msg) {
        message.setText(msg);
        String imgLocation = msg.replace(" ","");
        imgLocation = imgLocation.concat(".png");
        Image img = new Image(Objects.requireNonNull(Main.class.getResourceAsStream(imgLocation)));
        image.setImage(img);
    }
    @FXML
    private void showAddPlayerDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-player.fxml"));
            Parent root = loader.load();
            AddPlayerController controller = loader.getController();
            controller.setMain(mainApp);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("add-player-styles.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Add New Player");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            updateTable(playerManager.getPlayers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void showMyClubPage() throws Exception {
        mainApp.showClubPage();
    }

    public void addPlayer(Player newPlayer) throws Exception {
        playerManager.AddPlayer(newPlayer);
        playerManager.saveState(SERVER_ADDRESS,SERVER_PORT);
    }

    @FXML
    private void toggleRemovePlayer() {
        removePlayerTextField.setVisible(!removePlayerTextField.isVisible());
    }

    @FXML
    private void removePlayer() throws Exception {
        String playerName = removePlayerTextField.getText();
        if (playerName != null && !playerName.isEmpty()) {
            playerManager.RemovePlayer(playerName);
            updateTable(playerManager.getPlayers());
            removePlayerTextField.clear();
            removePlayerTextField.setVisible(false);
            playerManager.saveState(SERVER_ADDRESS,SERVER_PORT);
        }
    }
}

