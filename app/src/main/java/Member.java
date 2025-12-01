import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Member extends User{
    public static void changePhoneNumber(Connection connection, Scanner input) {
        String phone_number = "";

        while (phone_number.isEmpty()) {
            System.out.println("Please enter new phone number (xxx-xxx-xxxx):");
            phone_number = input.nextLine();
        }

        String query = """
                UPDATE \"User\"
                SET phone_number = ?
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, phone_number);
            ps.setInt(2, user_id);

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error updating phone number:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
        System.out.println("Phone Number changed!");
    }
}
