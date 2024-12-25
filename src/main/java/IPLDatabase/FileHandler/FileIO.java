package IPLDatabase.FileHandler;
import IPLDatabase.Player;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileIO {
    public List<Player> loadPlayers(String fileName) throws Exception {
        List<Player> players = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                String[] data = line.split(",");
                String name = data[0];
                String country = data[1];
                int age = Integer.parseInt(data[2]);
                double height = Double.parseDouble(data[3]);
                String club = data[4];
                String position = data[5];
                int jerseyNumber = (data[6].isEmpty())?-1:Integer.parseInt(data[6]);
                int weeklySalary = Integer.parseInt(data[7]);
                Player player = new Player(name, country, age, height, club, position, jerseyNumber, weeklySalary);
                players.add(player);
            }
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        finally {
            assert br != null;
            br.close();
        }
        return players;
    }
    public void savePlayers(List<Player> players,String fileName) throws Exception {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            for (Player player : players) {
                bw.write(player.getName() + "," + player.getCountry() + "," + player.getAge() + "," + player.getHeight() + "," + player.getClub() + "," + player.getPosition() + "," + ((player.getJerseyNumber() == -1)?"":player.getJerseyNumber()) + "," + player.getWeeklySalary());
                bw.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            assert bw != null;
            bw.close();
        }
    }
    public Map<String, String> loadCredentials(String fileName) throws Exception {
        Map<String, String> credentials = new HashMap<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileName));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                String[] data = line.split(",");
                if (data.length < 2) {
                    System.err.println("Invalid line: " + line);
                    continue;
                }
                String username = data[0].trim();
                String password = data[1].trim();
                credentials.put(username, password);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return credentials;
    }
}
