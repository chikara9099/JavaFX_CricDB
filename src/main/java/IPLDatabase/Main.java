package IPLDatabase;

import IPLDatabase.Operations.PlayerManager;
import IPLDatabase.Player;

import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws Exception {
        PlayerManager playerManager = new PlayerManager("players.txt");
        Scanner scn = new Scanner(System.in);
        while(true)
        {
            System.out.println("Main Menu");
            System.out.println("(1) Search Players");
            System.out.println("(2) Search Clubs");
            System.out.println("(3) Add ipl.cricketdb.Player");
            System.out.println("(4) Exit System");
            /*System.out.println("(5) Remove ipl.cricketdb.Player");
            System.out.println("(6) Sort Players");
            System.out.println("(7) Show All Players");*/
            int choice;
            String input = scn.nextLine();
            try{
                if(input.isEmpty())
                {
                    choice = -1;
                }
                else choice = Integer.parseInt(input);
            }
            catch(Exception e)
            {
                choice = -1;
            }
            if(choice == 4)
            {
                playerManager.saveState("players.txt");
                break;
            }
            switch(choice)
            {
                case 1:
                    while(true)
                    {
                        System.out.println("(1) By Player Name");
                        System.out.println("(2) By Club and Country");
                        System.out.println("(3) By Position");
                        System.out.println("(4) By Salary Range");
                        System.out.println("(5) Country-wise player count");
                        System.out.println("(6) Back to Main Menu");
                        List<Player> players;
                        int choice2;
                        String input2 = scn.nextLine();
                        try{
                            if(input2.isEmpty())
                            {
                                choice2 = -1;
                            }
                            else choice2 = Integer.parseInt(input2);
                        }
                        catch(Exception e)
                        {
                            choice2 = -1;
                        }
                        if(choice2 == 6) break;
                        switch(choice2)
                        {
                            case 1:
                                System.out.print("Enter Player Name: ");
                                String name = scn.nextLine();
                                Player player = playerManager.searchPlayerByName(name);
                                if(player != null)
                                {
                                    System.out.println(player);
                                }
                                else System.out.println("Player not found");
                                break;
                            case 2:
                                System.out.print("Enter Player's Country Name: ");
                                String country = scn.next();
                                scn.nextLine();
                                System.out.print("Enter Player's Club Name: ");
                                String club = scn.nextLine();
                                players = playerManager.searchPlayerByClubAndCountry(country, club);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such player with this country and club");
                                break;
                            case 3:
                                System.out.print("Enter Player Position: ");
                                String position = scn.next();
                                scn.nextLine();
                                players = playerManager.searchPlayerByPosition(position);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such player with this position");
                                break;
                            case 4:
                                System.out.print("Enter lowest salary: ");
                                int low = scn.nextInt();
                                scn.nextLine();
                                System.out.print("Enter highest salary: ");
                                int high = scn.nextInt();
                                scn.nextLine();
                                players = playerManager.searchPlayerBySalary(low, high);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such player with this weekly salary range");
                                break;
                            case 5:
                                playerManager.countryCount();
                                break;
                            default:
                                System.out.println("Invalid choice");
                        }
                    }
                    break;
                case 2:
                    while(true)
                    {
                        System.out.println("(1) Player(s) with the maximum salary of a club");
                        System.out.println("(2) Player(s) with the maximum age of a club");
                        System.out.println("(3) Player(s) with the maximum height of a club");
                        System.out.println("(4) Total yearly salary of a club");
                        System.out.println("(5) Back to Main Menu");
                        int choice2 = scn.nextInt();
                        scn.nextLine();
                        if(choice2 == 5) break;
                        List<Player> players;
                        String club;
                        switch(choice2)
                        {
                            case 1:
                                System.out.print("Enter club name: ");
                                club = scn.nextLine();
                                /*players = playerManager.maxClubSalary(club);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such club with this name");*/
                                break;
                            case 2:
                                System.out.print("Enter club name: ");
                                club = scn.nextLine();
                                /*players = playerManager.maxClubAge(club);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such club with this name");*/
                                break;
                            case 3:
                                System.out.print("Enter club name: ");
                                club = scn.nextLine();
                                /*players = playerManager.maxClubHeight(club);
                                if(!players.isEmpty())
                                {
                                    for(Player p: players)
                                    {
                                        System.out.println(p);
                                    }
                                }
                                else System.out.println("No such club with this name");*/
                                break;
                            case 4:
                                System.out.print("Enter club name: ");
                                club = scn.nextLine();
                                /*long totalSalary = playerManager.ClubYearlySalary(club);
                                System.out.println(club + " has a overall yearly salary of " + totalSalary);*/
                                break;
                        }
                    }
                    break;
                case 3:
                    System.out.print("Player's name: ");
                    String name = scn.nextLine();
                    System.out.print("Player's country: ");
                    String country = scn.nextLine();
                    System.out.print("Player's age: ");
                    int age = scn.nextInt();
                    scn.nextLine();
                    System.out.print("Player's height: ");
                    double height = scn.nextDouble();
                    scn.nextLine();
                    System.out.print("Player's club: ");
                    String club = scn.nextLine();
                    System.out.print("Player's position: ");
                    String position = scn.nextLine();
                    System.out.print("Player's Jersey number: ");
                    int jerseyNumber;
                    try {
                        String input3 = scn.nextLine();
                        if (input3.isEmpty()) {
                            jerseyNumber = -1;
                        } else {
                            jerseyNumber = Integer.parseInt(input3);
                        }
                    } catch (NumberFormatException e) {
                        jerseyNumber = -1;
                    }
                    System.out.print("Player's salary: ");
                    int salary = scn.nextInt();
                    scn.nextLine();
                    Player player = new Player(name,country,age,height,club,position,jerseyNumber,salary);
                    playerManager.AddPlayer(player);
                    break;
                /*case 5:
                    System.out.print("Enter Player name: ");
                    String toRemove = scn.nextLine();
                    playerManager.RemovePlayer(toRemove);
                    break;

                 */
                /*case 6:
                    playerManager.sortOnSalary();
                    break;
                case 7:
                    for(Player p : playerManager.getPlayers())
                    {
                        System.out.println(p);
                    }
                    break;*/
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}