import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class User {
    static int user_id;
    static String email;
    static int user_type;

    public static void setEmail(String _email) {
        email = _email;
    }

    public static void setUserId(int _user_id) {
        user_id = _user_id;
    }
    
    public static void setUserType(int _user_type) {
        user_type = _user_type;
    }

    public static String getEmail() {
        return email;
    }

    public static int getUserId() {
        return user_id;
    }

    public static int getUserType() {
        return user_type;
    }

    public static void registerUser(Connection connection, Scanner input) {
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

    public static void logIn(Connection connection, Scanner input) {
        String _email  = "", password = "";
        System.out.println("Enter Email:");
        while(_email.isEmpty()) {
            _email = input.nextLine().trim();
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
            ps.setString(1, _email.toLowerCase());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("User not found.");
                    return; 
                }

                email = _email;
                User.setUserId(rs.getInt(rs.findColumn("user_id")));
                user_type = rs.getInt(rs.findColumn("user_type"));

                System.out.println("Hello " +rs.getString(rs.findColumn("first_name")) + "!");
            }
        } catch (Exception e) {
            System.out.println("Error connecting to database:");
            System.out.println(e);
            return;
        }
    }

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
            ps.setInt(3, user_id);

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
            ps.setInt(2, user_id);

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
            ps.setInt(2, user_id);

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

    public static void changeGender(Connection connection, Scanner input) {

    }
}
