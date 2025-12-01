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
                System.out.println("Error adding updating name:");
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
}
