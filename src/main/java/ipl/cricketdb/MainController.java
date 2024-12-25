package ipl.cricketdb;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> positionComboBox;
    @FXML
    private ComboBox<String> clubComboBox;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private Slider minSalarySlider;
    @FXML
    private Slider maxSalarySlider;
    @FXML
    private TextField removePlayerTextField;
    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn<Player, String> nameColumn;
    @FXML
    private TableColumn<Player, String> countryColumn;
    @FXML
    private TableColumn<Player, Integer> ageColumn;
    @FXML
    private TableColumn<Player, Double> heightColumn;
    @FXML
    private TableColumn<Player, String> clubColumn;
    @FXML
    private TableColumn<Player, String> positionColumn;
    @FXML
    private TableColumn<Player, Integer> jerseyNumberColumn;
    @FXML
    private TableColumn<Player, Integer> weeklySalaryColumn;
    @FXML
    private Label message;
    private PlayerManager playerManager;
    @FXML
    private ImageView image;
    private Main mainApp;
    private ScheduledExecutorService scheduler;

    public void setMain(Main mainApp) {
        this.mainApp = mainApp;
    }
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
            playerManager = new PlayerManager("127.0.0.1",12345);
            updateTable(playerManager.getPlayers());
            startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addListeners();
    }

    private void addListeners() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> search());
        positionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        clubComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        minSalarySlider.valueProperty().addListener((observable, oldValue, newValue) -> search());
        maxSalarySlider.valueProperty().addListener((observable, oldValue, newValue) -> search());
    }
    private void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                playerManager.fetchPlayersFromServer("127.0.0.1", 12345);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    @FXML
    private void search() {
        List<Player> results = playerManager.getPlayers();

        if (nameField.getText() != null && !nameField.getText().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getName(), nameField.getText())).collect(Collectors.toList());
        }

        if (positionComboBox.getValue() != null && !positionComboBox.getValue().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getPosition(), positionComboBox.getValue())).collect(Collectors.toList());
        }

        if (clubComboBox.getValue() != null && !clubComboBox.getValue().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getClub(), clubComboBox.getValue() )).collect(Collectors.toList());
        }

        if (countryComboBox.getValue() != null && !countryComboBox.getValue().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getCountry(), countryComboBox.getValue())).collect(Collectors.toList());
        }

        if (minSalarySlider.getValue() != 0 && maxSalarySlider.getValue() != 0) {
            double low = minSalarySlider.getValue();
            double high = maxSalarySlider.getValue();
            results = results.stream().filter(player -> player.getWeeklySalary() >= low && player.getWeeklySalary() <= high).collect(Collectors.toList());
        }

        updateTable(results);
    }

    private boolean matches(String text, String pattern) {
        Pattern regex = Pattern.compile(Pattern.quote(pattern) + ".*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }
    public void init(String msg) {
        message.setText(msg);
    }

    private void updateTable(List<Player> players) {
        ObservableList<Player> playerList = FXCollections.observableArrayList(players);
        playerTable.setItems(playerList);
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
        playerManager.saveState("127.0.0.1",12345);
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
            playerManager.saveState("127.0.0.1",12345);
        }
    }
    @FXML
    private void logout() throws Exception {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        mainApp.showLoginPage();
    }
}

