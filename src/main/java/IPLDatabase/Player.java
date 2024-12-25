package IPLDatabase;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    private String name;
    private String country;
    private int age;
    private double height;
    private String club;
    private String position;
    private int jerseyNumber;
    private int weeklySalary;
    public Player(String name, String country, int age, double height, String club, String position, int jerseyNumber, int weeklySalary) {
        this.name = name;
        this.country = country;
        this.age = age;
        this.height = height;
        this.club = club;
        this.position = position;
        this.jerseyNumber = jerseyNumber;
        this.weeklySalary = weeklySalary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public int getJerseyNumber() {
        return jerseyNumber;
    }

    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }

    public int getWeeklySalary() {
        return weeklySalary;
    }

    public void setWeeklySalary(int weeklySalary) {
        this.weeklySalary = weeklySalary;
    }
    @Override
    public String toString() {
        return "Name: " + name + "\n" + "Country: " + country + "\n" + "Age: " + age + "\n" + "Height: " + height + "\n" + "Club: " + club + "\n" + "Position: " + position + "\n" + "Jersey No. " + ((jerseyNumber == -1)?"":jerseyNumber) + "\n" + "Weekly Salary: " + weeklySalary + "\n";
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, club, jerseyNumber);
    }

}
