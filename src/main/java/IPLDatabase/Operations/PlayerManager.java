package IPLDatabase.Operations;
import IPLDatabase.Player;
import IPLDatabase.FileHandler.FileIO;
import ipl.cricketdb.SocketWrapper;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.*;
public class PlayerManager {
    FileIO file = new FileIO();
    List<Player> players;
    public PlayerManager(String database) throws Exception {
        players = file.loadPlayers(database);
    }

    public PlayerManager(String serverAddress, int serverPort) throws Exception {
        fetchPlayersFromServer(serverAddress, serverPort);
    }

    public void fetchPlayersFromServer(String serverAddress, int serverPort) throws Exception {
        try  {
            Socket socket = new Socket(serverAddress, serverPort);
            SocketWrapper socketWrapper = new SocketWrapper(socket);
            socketWrapper.write("GET_PLAYERS");
            players = (List<Player>) socketWrapper.read();
        } catch (Exception e) {
            throw new Exception("Failed to fetch players from server", e);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player searchPlayerByName(String playerName) {
        for(Player player : players) {
            if(player.getName().equalsIgnoreCase(playerName)) {
                return player;
            }
        }
        return null;
    }

    public List<Player> searchPlayerByPosition(String position) {
        List<Player> data = new ArrayList<>();
        for (Player player : players) {
            if (player.getPosition().equalsIgnoreCase(position)) {
                data.add(player);
            }
        }
        return data;
    }

    public List<Player> searchPlayerBySalary(int low, int high) {
        List<Player> data = new ArrayList<>();
        for (Player player : players) {
            if (player.getWeeklySalary() >= low && player.getWeeklySalary() <= high) {
                data.add(player);
            }
        }
        return data;
    }
    public List<Player> searchByClub(String club) {
        List<Player> data = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club)) {
                data.add(player);
            }
        }
        return data;
    }
    public List<Player> searchPlayerByClubAndCountry(String club, String country) {
        List<Player> data = new ArrayList<>();
        for (Player player : players) {
            if (player.getClub().equalsIgnoreCase(club) && player.getCountry().equalsIgnoreCase(country)) {
                data.add(player);
            }
        }
        return data;
    }

    public void countryCount()
    {
        HashMap<String, Integer> count = new HashMap<>();
        for(Player player : players) {
            count.put(player.getCountry(), count.getOrDefault(player.getCountry(), 0) + 1);
        }
        for(HashMap.Entry<String,Integer> entry:count.entrySet())
        {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
    public String maxClubSalary(String club){
        double maxSalary = 0;
        for(Player player : players) {
            if(player.getClub().equalsIgnoreCase(club) && player.getWeeklySalary() > maxSalary) {
                maxSalary = player.getWeeklySalary();
            }
        }
        return new BigDecimal(maxSalary).toPlainString();
    }
    public String maxClubAge(String club){
        int maxAge = 0;
        for(Player player : players) {
            if(player.getClub().equalsIgnoreCase(club) && player.getAge() > maxAge) {
                maxAge = player.getAge();
            }
        }
        return String.valueOf(maxAge);
    }
    public String maxClubHeight(String club){
        double maxHeight = 0;
        for(Player player : players) {
            if(player.getClub().equalsIgnoreCase(club) && player.getHeight() > maxHeight) {
                maxHeight = player.getHeight();
            }
        }
        return String.valueOf(maxHeight);
    }
    public String ClubYearlySalary(String club){
        long weeklySalary = 0;
        for(Player player : players) {
            if(player.getClub().equalsIgnoreCase(club)) {
                weeklySalary += player.getWeeklySalary();
            }
        }
        return String.valueOf(weeklySalary);
    }
    public void AddPlayer(Player player)
    {
        for(Player p : players) {
            if(p.getName().equalsIgnoreCase(player.getName())) {
                System.err.println("Player with this name already exists");
                return;
            }
        }
        players.add(player);
    }
    public void RemovePlayer(String name) {
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getName().equalsIgnoreCase(name)) {
                iterator.remove();
            }
        }
    }

    public void saveState(String filename) throws Exception {
        file.savePlayers(players,filename);
    }
    public void saveState(String serverAddress, int serverPort) throws Exception {
        try  {
            Socket socket = new Socket(serverAddress, serverPort);
            SocketWrapper socketWrapper = new SocketWrapper(socket);
            socketWrapper.write("SAVE_PLAYERS");
            socketWrapper.write(players);
        } catch (Exception e) {
            throw new Exception("Failed to save new state", e);
        }
    }
    public void sortOnSalary()
    {
        Sorter sorter = new Sorter();
        players.sort(sorter);
    }
}
