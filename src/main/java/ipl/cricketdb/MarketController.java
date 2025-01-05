package ipl.cricketdb;

import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.Task;

import static ipl.cricketdb.Main.userName;

public class MarketController extends BaseController{
    @FXML
    private TableColumn<Player, Void> buyButtonColumn;
    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @FXML
    public void initialize() {
        try {
            socketWrapper = new SocketWrapper(new Socket(SERVER_ADDRESS, SERVER_PORT));
            setupTableColumns();
            fetchAndUpdateTransferList();
            startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        clubColumn.setCellValueFactory(new PropertyValueFactory<>("club"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        jerseyNumberColumn.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        weeklySalaryColumn.setCellValueFactory(new PropertyValueFactory<>("weeklySalary"));
        buyButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button buyButton = new Button("Buy");
            {
                buyButton.setOnAction(event -> {
                    Player player = getTableView().getItems().get(getIndex());
                    if (player != null) {
                        try {
                            buyPlayer(player);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buyButton);
            }
        });
    }
    private void fetchAndUpdateTransferList() {
        Task<List<Player>> task = new Task<>() {
            @Override
            protected List<Player> call() throws Exception {
                socketWrapper.write("GET_TRANSFER_LIST");
                return (List<Player>) socketWrapper.read();
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                List<Player> updatedTransferList = getValue();
                if(updatedTransferList != null) {
                    updateTable(updatedTransferList);
                }
                else
                {
                    updateTable(Collections.emptyList());
                }
            }
            @Override
            protected void failed() {
                super.failed();
                //e.printStackTrace();
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void buyPlayer(Player player) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    SocketWrapper socka = new SocketWrapper(new Socket(SERVER_ADDRESS, SERVER_PORT));
                    System.out.println("Sending command BUY_PLAYER...");
                    socka.write("BUY_PLAYER");
                    System.out.println("Sending buyer username: " + userName);
                    socka.write(userName);
                    System.out.println("Sending player: " + player);
                    socka.write(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                ObservableList<Player> players = playerTable.getItems();
                players.remove(player);
                fetchAndUpdateTransferList();
            }
            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to buy player. Please try again.");
                    alert.showAndWait();
                });
                //e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    protected void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                fetchAndUpdateTransferList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }
}

