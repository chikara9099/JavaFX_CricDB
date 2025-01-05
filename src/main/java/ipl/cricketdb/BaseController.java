package ipl.cricketdb;

import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class BaseController {
    @FXML
    protected ImageView image;
    @FXML
    protected TextField nameField;
    @FXML
    protected ComboBox<String> positionComboBox;
    @FXML
    protected ComboBox<String> clubComboBox;
    @FXML
    protected ComboBox<String> countryComboBox;
    @FXML
    protected Slider minSalarySlider;
    @FXML
    protected Slider maxSalarySlider;
    @FXML
    protected TextField removePlayerTextField;
    @FXML
    protected TableView<Player> playerTable;
    @FXML
    protected TableColumn<Player, String> nameColumn;
    @FXML
    protected TableColumn<Player, String> countryColumn;
    @FXML
    protected TableColumn<Player, Integer> ageColumn;
    @FXML
    protected TableColumn<Player, Double> heightColumn;
    @FXML
    protected TableColumn<Player, String> clubColumn;
    @FXML
    protected TableColumn<Player, String> positionColumn;
    @FXML
    protected TableColumn<Player, Integer> jerseyNumberColumn;
    @FXML
    protected TableColumn<Player, Integer> weeklySalaryColumn;
    @FXML
    protected Label message;
    protected PlayerManager playerManager;
    protected ScheduledExecutorService scheduler;
    protected Main mainApp;
    protected String SERVER_ADDRESS = "127.0.0.1";
    protected int SERVER_PORT = 12345;
    protected SocketWrapper socketWrapper;
    public void setMain(Main mainApp){
        this.mainApp = mainApp;
    }
    protected boolean matches(String text, String pattern) {
        Pattern regex = Pattern.compile(Pattern.quote(pattern) + ".*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }
    protected void updateTable(List<Player> players) {
        ObservableList<Player> playerList = FXCollections.observableArrayList(players);
        Platform.runLater(()->{
            playerTable.getItems().clear();
            playerTable.setItems(playerList);
            playerTable.refresh();
        });
    }
    @FXML
    protected void logout() throws Exception {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        mainApp.showLoginPage();
    }
    protected void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                playerManager.fetchPlayersFromServer(SERVER_ADDRESS, SERVER_PORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
    protected void addListeners() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> search());
        positionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        if(clubComboBox != null) clubComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());
        minSalarySlider.valueProperty().addListener((observable, oldValue, newValue) -> search());
        maxSalarySlider.valueProperty().addListener((observable, oldValue, newValue) -> search());
    }
    @FXML
    protected void search() {
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
}
