import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class User {
    public static void changeName(Connection connection, Scanner input) {
        String f_name = "", l_name = "";
        
        while (f_name.isEmpty()) {
            System.out.println("Please enter new first name:");
            f_name = input.nextLine();
        }
        while (l_name.isEmpty()) {
            System.out.println("Please enter new last name:");
            l_name = input.nextLine();
        }

        String query = """
                UPDATE \"User\"
                SET first_name = ?, last_name = ?
                WHERE user_id = ?
                """;
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, f_name);
            ps.setString(2, l_name);
            ps.setInt(3, SessionManager.getUserId());

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error updating name:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
        System.out.println("Name changed!");
    }
    
    public static void changeEmail(Connection connection, Scanner input) {
        String email = "";

        while (email.isEmpty()) {
            System.out.println("Please enter new email:");
            email = input.nextLine();
        }

        String query = """
                UPDATE \"User\"
                SET email = ?
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setInt(2, SessionManager.getUserId());

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error updating email:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
        System.out.println("Email changed!");
    }

    public static void changePassword(Connection connection, Scanner input) {
        String password = "";

        while (password.isEmpty()) {
            System.out.println("Please enter new password:");
            password = input.nextLine();
        }

        String query = """
                UPDATE \"User\"
                SET user_password = ?
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, password);
            ps.setInt(2, SessionManager.getUserId());

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error updating password:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
        System.out.println("Password changed!");
    }
}
