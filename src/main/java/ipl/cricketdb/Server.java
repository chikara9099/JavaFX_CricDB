package ipl.cricketdb;

import IPLDatabase.FileHandler.FileIO;
import IPLDatabase.Player;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private static final int PORT = 12345;
    private static List<Player> players;
    private Map<String, String> userCredentials;
    private static List<Player> transferList;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String playerFilePath = "C:\\Users\\MSI\\Desktop\\Study\\CSE108\\CricketDB\\src\\main\\resources\\ipl\\cricketdb\\players.txt";
    private static final String credentialsFilePath = "C:\\Users\\MSI\\Desktop\\Study\\CSE108\\CricketDB\\src\\main\\resources\\ipl\\cricketdb\\credentials.txt";
    public Server(Map<String, String> userCredentials) {
        this.userCredentials = userCredentials;
        this.players = loadPlayersFromFile();
        this.transferList = new ArrayList<>();
        startPeriodicPlayerRefresh();
    }
    public void transferPlayerToList(Player player) {
        lock.writeLock().lock();
        try {
            players.remove(player);
            transferList.add(player);
            savePlayers(players);
            System.out.println("Player " + player.getName() + " moved to transfer list.");
        } finally {
            lock.writeLock().unlock();
        }
    }
    public List<Player> getTransferList() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(transferList);
        } finally {
            lock.readLock().unlock();
        }
    }
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started....");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPeriodicPlayerRefresh() {
        Thread refreshThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Refresh every 5 seconds
                    List<Player> updatedPlayers = loadPlayersFromFile();
                    lock.writeLock().lock();
                    try {
                        this.players = updatedPlayers;
                        System.out.println("Player list refreshed.");
                    } finally {
                        lock.writeLock().unlock();
                    }
                } catch (InterruptedException e) {
                    System.err.println("Periodic refresh interrupted.");
                }
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    public List<Player> getPlayers() {
        lock.readLock().lock();
        try {
            return players;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void savePlayers(List<Player> updatedPlayers) {
        lock.writeLock().lock();
        try {
            FileIO file = new FileIO();
            file.savePlayers(updatedPlayers, playerFilePath);
            players = updatedPlayers;
            System.out.println("Players updated and saved to file.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<Player> loadPlayersFromFile() {
        try {
            FileIO file = new FileIO();
            return file.loadPlayers(playerFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Server server;

        public ClientHandler(Socket clientSocket, Server server) {
            this.clientSocket = clientSocket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                SocketWrapper socketWrapper = new SocketWrapper(clientSocket);
                String command = (String) socketWrapper.read();

                switch (command) {
                    case "GET_PLAYERS":
                        players = server.getPlayers();
                        socketWrapper.write(players);
                        break;

                    case "LOGIN":
                        String username = (String) socketWrapper.read();
                        String password = (String) socketWrapper.read();
                        String response = verifyCredentials(username, password) ? "SUCCESS" : "FAILURE";
                        socketWrapper.write(response);
                        break;

                    case "SAVE_PLAYERS":
                        List<Player> newPlayers = (List<Player>) socketWrapper.read();
                        server.savePlayers(newPlayers);
                        socketWrapper.write("Players updated successfully.");
                        break;

                    case "SELL_PLAYER":
                        Player playerToSell = (Player) socketWrapper.read();
                        players.remove(playerToSell);
                        playerToSell.setClub(null);
                        players.add(playerToSell);
                        server.transferPlayerToList(playerToSell);
                        socketWrapper.write("Player transferred to transfer list.");
                        break;

                    case "GET_TRANSFER_LIST":
                        socketWrapper.write(server.getTransferList());
                        break;

                    case "BUY_PLAYER":
                        System.out.println("Received command: BUY_PLAYER");
                        String buyerUsername = (String) socketWrapper.read();
                        System.out.println("Buyer username: " + buyerUsername);
                        Player playerToBuy = (Player) socketWrapper.read();
                        System.out.println("Player received: " + playerToBuy);
                        lock.writeLock().lock();
                        try {
                            if (transferList.contains(playerToBuy)) {
                                transferList.remove(playerToBuy);
                                players.remove(playerToBuy);
                                playerToBuy.setClub(buyerUsername);
                                players.add(playerToBuy);
                                savePlayers(players);
                                socketWrapper.write("Player bought successfully.");
                                System.out.println("Player " + playerToBuy.getName() + " sold to " + buyerUsername);
                            } else {
                                socketWrapper.write("Player not found in transfer list.");
                            }
                        } finally {
                            lock.writeLock().unlock();
                        }
                        break;

                    default:
                        socketWrapper.write("INVALID_COMMAND");
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean verifyCredentials(String username, String password) {
            return server.userCredentials.containsKey(username) && server.userCredentials.get(username).equals(password);
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> userCredentials = loadUserCredentials();
        Server server = new Server(userCredentials);
        server.start();
    }

    private static Map<String, String> loadUserCredentials() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        FileIO file = new FileIO();
        credentials = file.loadCredentials(credentialsFilePath);
        return credentials;
    }
}


