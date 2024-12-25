package ipl.cricketdb;

import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.Task;

import static ipl.cricketdb.Main.userName;

public class MarketController {
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
    private TableColumn<Player, Void> buyButtonColumn;

    private Main main;
    private ScheduledExecutorService scheduler;
    private PlayerManager playerManager;
    private SocketWrapper socketWrapper;

    public void setMain(Main mainApp) throws IOException, ClassNotFoundException {
        this.main = mainApp;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @FXML
    public void initialize() {
        try {
            socketWrapper = new SocketWrapper(new Socket("127.0.0.1", 12345));
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
                updateTable(getValue());
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
                    SocketWrapper socka = new SocketWrapper(new Socket("127.0.0.1", 12345));
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
                fetchAndUpdateTransferList();
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

    private void updateTable(List<Player> players) {
        ObservableList<Player> playerList = FXCollections.observableArrayList(players);
        playerTable.setItems(playerList);
    }

    private void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                fetchAndUpdateTransferList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}

