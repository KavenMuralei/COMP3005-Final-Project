import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static int memberLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
                System.out.println("\n");
            }    
        }
        System.out.println("member loop exited");
        return 0;
    }

    private static int trainerLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
                System.out.println("\n");
            }    
        }
        System.out.println("trainer loop exited");
        return 0;
    }

    private static int adminLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
                System.out.println("\n");
            }    
        }
        System.out.println("admin loop exited");
        return 0;
    }

    private static void logIn(Connection connection, Scanner input) {
        String email  = "", password = "";
        System.out.println("Enter Email:");
        while(email.isEmpty()) {
            email = input.nextLine().trim();
        }
        System.out.println("Enter Password:");
        while(password.isEmpty()) {
            password = input.nextLine().trim();
        }

        String query = """
                SELECT 
                    email, 
                    user_password, 
                    user_type 
                FROM \"User\" 
                WHERE email = ? AND user_password = ?
                """;
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email.toLowerCase());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("User not found.");
                    logIn(connection, input);
                    return; 
                }
                switch (rs.getInt("user_type")) {
                    case 0 -> memberLoop(connection, input);
                    case 1 -> trainerLoop(connection, input);
                    case 2 -> adminLoop(connection, input);
                    default -> {
                        System.out.println("Error, unexpected user type");
                        return;
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error connecting to database:");
            System.out.println(e);
            return;
        }
    }
  
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
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
                logIn(connection, input);
                connection.close();
            } else {
                System.out.println("Failed to connect to database.");
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }

        System.out.println("program terminating");
        input.close();
    }

}
