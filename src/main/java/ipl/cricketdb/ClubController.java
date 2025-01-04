package ipl.cricketdb;

import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClubController extends BaseController{
    @FXML
    private TableColumn<Player, Void> sellButtonColumn;
    @FXML
    private Label maxAge;
    @FXML
    private Label maxHeight;
    @FXML
    private Label maxSalary;
    @FXML
    private Label totalSalary;
    public void init(String userName) {
        message.setText(userName);
        String imgLocation = userName.replace(" ","");
        imgLocation = imgLocation.concat(".png");
        Image img = new Image(Objects.requireNonNull(Main.class.getResourceAsStream(imgLocation)));
        image.setImage(img);
        javafx.application.Platform.runLater(() -> {
            maxAge.setText(playerManager.maxClubAge(userName));
            maxHeight.setText(playerManager.maxClubHeight(userName));
            maxSalary.setText(playerManager.maxClubSalary(userName));
            totalSalary.setText(playerManager.ClubYearlySalary(userName));
        });
    }
    private String userName = Main.getUserName();
    @FXML
    public void initialize() {
        positionComboBox.setItems(FXCollections.observableArrayList(null, "Batsman", "Bowler", "Allrounder", "Wicketkeeper"));
        countryComboBox.setItems(FXCollections.observableArrayList(null, "India", "Australia", "England", "South Africa", "Bangladesh", "New Zealand","Afghanistan", "West Indies", "South Africa"));
        minSalarySlider.setBlockIncrement(1000000);
        maxSalarySlider.setBlockIncrement(1000000);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));
        sellButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button sellButton = new Button("Sell");
            {
                sellButton.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    if (player != null) {
                        try {
                            sellPlayer(player);
                            playerManager.fetchPlayersFromServer(SERVER_ADDRESS,SERVER_PORT);
                            List<Player> updatedPlayers = playerManager.searchByClub(userName);
                            updateTable(updatedPlayers);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(sellButton);
                }
            }
        });
        try {
            playerManager = new PlayerManager(SERVER_ADDRESS,SERVER_PORT);
            List<Player> clubPlayers = playerManager.searchByClub(userName);
            updateTable(clubPlayers);
            startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        addListeners();
    }
    @Override
    protected void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                playerManager.fetchPlayersFromServer(SERVER_ADDRESS,SERVER_PORT);
                init(userName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    @Override
    @FXML
    protected void search() {
        List<Player> results = playerManager.searchByClub(userName);
        if (nameField.getText() != null && !nameField.getText().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getName(), nameField.getText())).collect(Collectors.toList());
        }
        if (positionComboBox.getValue() != null && !positionComboBox.getValue().isEmpty()) {
            results = results.stream().filter(player -> matches(player.getPosition(), positionComboBox.getValue())).collect(Collectors.toList());
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
    @FXML
    private void backToHome() throws Exception {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        mainApp.showHomePage(userName);
    }
    @FXML
    private void sellPlayer(Player player) {
        try {
            SocketWrapper socketWrapper = new SocketWrapper(new Socket(SERVER_ADDRESS,SERVER_PORT));
            socketWrapper.write("SELL_PLAYER");
            socketWrapper.write(player);
            socketWrapper.write("Player sold and transferred to transfer list.");
            search();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void showMarket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("market.fxml"));
            Parent root = loader.load();
            MarketController controller = loader.getController();
            controller.setMain(mainApp);
            controller.setPlayerManager(playerManager);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main-styles.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setTitle("Buy Player");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            updateTable(playerManager.searchByClub(userName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
