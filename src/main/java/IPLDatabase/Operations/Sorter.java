package IPLDatabase.Operations;
import IPLDatabase.Player;
import java.util.Comparator;

public class Sorter implements Comparator<Player> {
    public int compare(Player p1, Player p2) {
        if(p1.getWeeklySalary() == p2.getWeeklySalary())
        {
            return 0;
        }
        else if(p1.getWeeklySalary() > p2.getWeeklySalary())
        {
            return 1;
        }
        else return -1;
    }
}
