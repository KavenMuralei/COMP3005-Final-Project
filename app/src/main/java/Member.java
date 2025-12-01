import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Member extends User{
    private static int member_id;

    public static void fetchMemberId(Connection connection) {
        String query = """
                SELECT member_id 
                FROM Member
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                member_id = rs.getInt(1);
            } catch (Exception e) {
                System.out.println("Error getting member_id:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
    }

    public static void changePhoneNumber(Connection connection, Scanner input) {
        String phone_number = "";

        while (phone_number.isEmpty()) {
            System.out.println("Please enter new phone number (xxx-xxx-xxxx):");
            phone_number = input.nextLine();
        }

        String query = """
                UPDATE Member
                SET phone_number = ?
                WHERE member_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, phone_number);
            ps.setInt(2, member_id);

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

    public static void changeDoB(Connection connection, Scanner input) {
        String phone_number = "";

        while (phone_number.isEmpty()) {
            System.out.println("Please enter new phone number (xxx-xxx-xxxx):");
            phone_number = input.nextLine();
        }

        String query = """
                UPDATE Member
                SET date_of_birth = ?
                WHERE member_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, phone_number);
            ps.setInt(2, member_id);

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
