import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    private static int memberLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "change name":
                    User.changeName(connection, input);
                    break;
                case "change email":
                    User.changeEmail(connection, input);
                    break;
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
            }    
            System.out.println("\n");
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
            }    
            System.out.println("\n");
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
            }    
            System.out.println("\n");
        }
        System.out.println("admin loop exited");
        return 0;
    }

    private static void registerUser(Connection connection, Scanner input) {
        String f_name = "", l_name = "", email = "", password = "", phone_number = "", dob = "", gender = "", confirm = "";
        while(confirm.equals("no") || confirm.isEmpty()){
            f_name = ""; l_name = ""; email = ""; password = ""; phone_number = ""; dob = ""; gender = ""; confirm = "";
            System.out.println("Please Enter First Name");
            while (f_name.isEmpty()) {
                f_name = input.nextLine();
            }
            System.out.println("Please Enter Last Name");
            while (l_name.isEmpty()) {
                l_name = input.nextLine();
            }
            System.out.println("Please Enter Email");
            while (email.isEmpty()) {
                email = input.nextLine().toLowerCase();
            }
            System.out.println("Please Enter Password");
            while (password.isEmpty()) {
                password = input.nextLine();
            }
            System.out.println("Please Enter Phone Number (xxx-xxx-xxxx)");
            while (phone_number.isEmpty()) {
                phone_number = input.nextLine();
            }
            System.out.println("Please Enter Date of Birth (YYYY-MM-DD)");
            boolean valid = false;
            while (dob.isEmpty() || !valid) {
                dob = input.nextLine();
                try {
                    LocalDate.parse(dob);
                    valid = true;
                }
                catch (Exception e) {
                    System.out.println("Date of birth format invalid");
                    valid = false;
                }
            }
            System.out.println("Please Enter gender (male, female, other)");
            HashSet<String> possibleGenders = new HashSet<String>(Arrays.asList("male", "female", "other"));
            while (gender.isEmpty() || !possibleGenders.contains(gender)) {
                gender = input.nextLine().toLowerCase();
            }
            System.out.println(
                "You entered:\nName: %s %s\nEmail: %s\nPassword: %s\nPhone Number: %s\nDate of Birth: %s\nGender: %s\nIs this correct?"
                .formatted(f_name, l_name, email, password, phone_number, dob, gender)
            );
            while(!confirm.equals("yes") && !confirm.equals("no")){
                System.out.println("Enter 'yes' or 'no'");
                confirm = input.nextLine().toLowerCase();
            }
        }
        
        String query = """
                WITH new_user AS (
                    INSERT INTO \"User\"(first_name, last_name, email, user_password, user_type) 
                    VALUES (?, ?, ?, ?, 0) 
                    RETURNING user_id
                )
                INSERT INTO Member(member_id, phone_number, date_of_birth, gender)
                SELECT user_id, ?, ?, ? FROM new_user;
                """;
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, f_name);
            ps.setString(2, l_name);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, phone_number);
            ps.setDate(6, java.sql.Date.valueOf(dob));
            ps.setString(7, gender);

            try {
                ps.executeUpdate();
                System.out.println("New member added successfully");
                return;
            }
            catch (Exception e) {
                System.out.println("Error adding new member:");
                System.out.println(e);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database:");
            System.out.println(e);
            return;
        }
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
                    user_id, 
                    first_name,  
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
                    return; 
                }

                SessionManager.setEmail(email);
                SessionManager.setUserId(rs.getInt(rs.findColumn("user_id")));

                System.out.println("Hello " +rs.getString(rs.findColumn("first_name")) + "!");

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
        } catch (Exception e) {
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

                String option = "";
                System.out.println("Login or Register?");
                while (!option.equals("login") && !option.equals("register")){
                    System.out.println("Please enter 'login' or 'register'");
                    option = input.nextLine().toLowerCase();
                }

                if(option.equals("login"))
                    logIn(connection, input);
                else
                    registerUser(connection, input);
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
