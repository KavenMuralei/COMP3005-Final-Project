import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        try{
            String url = "jdbc:postgresql://localhost:5432/finalproject";
            String user = "postgres";
            String password = "admin";
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url,user,password);
//            Statement statement = connection.createStatement();
//            statement.close();
            if (connection != null) {
                System.out.println("Connected to database");
            } else {
                System.out.println("Failed to connect to database.");
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }
    }

}
