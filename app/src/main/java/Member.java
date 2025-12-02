import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Member extends User{
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

    public static void changeGender(Connection connection, Scanner input) {
        String gender = "";

        while (!gender.equals("male") || !gender.equals("female") || !gender.equals("other")) {
            System.out.println("Please enter new gender:");
            gender = input.nextLine().toLowerCase();
        }

        String query = """
                UPDATE Member
                SET gender = ?
                WHERE member_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, gender);
            ps.setInt(2, user_id);

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error updating gender:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
        System.out.println("Gender changed!");
    }
    public static void dashboard(Connection connection) {
        String query = """
        SELECT * FROM member_dashboard_view WHERE member_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.isBeforeFirst()) { // check if empty
                    System.out.println("No dashboard data found for member " + user_id);
                    return;
                }

                System.out.println("===== DASHBOARD =====");

                boolean printedHeader = false;

                while (rs.next()) {
                    if (!printedHeader) {
                        System.out.println("Name: "
                                + rs.getString("first_name") + " "
                                + rs.getString("last_name"));

                        System.out.println("Latest Weight (kg): " + rs.getObject("latest_weight"));
                        System.out.println("Latest Height (cm): " + rs.getObject("latest_height"));
                        System.out.println("Latest Bodyfat: " + rs.getObject("latest_bodyfat"));
                        System.out.println("Latest BPM: " + rs.getObject("latest_bpm"));
                        System.out.println("Last Metric Time: " + rs.getTimestamp("last_metric_time"));
                        System.out.println("Total Classes Joined: " + rs.getInt("total_classes_joined"));
                        System.out.println("Active Goals:");
                        printedHeader = true;
                    }

                    // Print each active goal
                    System.out.println(" - " + rs.getString("goal_type")
                            + " | Target: " + rs.getObject("target")
                            + " | Status: " + rs.getString("goal_status"));
                }
            }

        } catch (Exception e) {
            System.out.println("Error retrieving dashboard:");
            System.out.println(e);
        }
    }

    public static void addFitnessGoal(Connection connection, Scanner input) {
        String goalType = "";
        Double target = null;

        while (goalType.isEmpty()) {
            System.out.println("Enter goal type (weight in kg, bodyfat, strength):");
            goalType = input.nextLine().trim().toLowerCase();
        }

        while (target == null) {
            try {
                System.out.println("Enter target value:");
                target = Double.parseDouble(input.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid number, try again.");
            }
        }

        String query = """
        INSERT INTO FitnessGoal (member_id, goal_type, target, start_of_goal, end_of_goal)
        VALUES (?, ?, ?, ?, ?);
        """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);
            ps.setString(2, goalType);
            ps.setDouble(3, target);
            ps.setDate(4, Date.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            ps.setDate(5, null);
            ps.executeUpdate();
            System.out.println("Fitness goal updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating fitness goal:");
            System.out.println(e);
        }
    }

    public static void manageHealthMetrics(Connection connection, Scanner input) {
        Double weight = null;
        Double height = null;
        Double bodyfat = null;
        Integer bpm = null;

        try {
            System.out.println("Enter weight in kilograms (or press Enter to skip):");
            String w = input.nextLine().trim();
            if (!w.isEmpty()) weight = Double.parseDouble(w);

            System.out.println("Enter height (or press Enter to skip):");
            String h = input.nextLine().trim();
            if (!h.isEmpty()) height = Double.parseDouble(h);

            System.out.println("Enter bodyfat percentage (or press Enter to skip):");
            String b = input.nextLine().trim();
            if (!b.isEmpty()) bodyfat = Double.parseDouble(b);

            System.out.println("Enter BPM (or press Enter to skip):");
            String r = input.nextLine().trim();
            if (!r.isEmpty()) bpm = Integer.parseInt(r);

        } catch (Exception e) {
            System.out.println("Invalid entry. Please enter numeric values.");
            return;
        }

        String query = """
            INSERT INTO HealthMetric (member_id, time, weight, height_cm, bodyfat_percent, bpm)
            VALUES (?, ?, ?, ?, ?, ?);
            """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

            if (weight == null) ps.setNull(3, java.sql.Types.NUMERIC);
            else ps.setDouble(3, weight);

            if (height == null) ps.setNull(4, java.sql.Types.NUMERIC);
            else ps.setDouble(4, height);

            if (bodyfat == null) ps.setNull(5, java.sql.Types.NUMERIC);
            else ps.setDouble(5, bodyfat);

            if (bpm == null) ps.setNull(6, java.sql.Types.INTEGER);
            else ps.setInt(6, bpm);

            ps.executeUpdate();
            System.out.println("New health metric recorded!");
        } catch (Exception e) {
            System.out.println("Error adding health metric:");
            System.out.println(e);
        }
    }

    public static void manageFitnessGoals(Connection connection, Scanner input) {
        String listQuery = """
            SELECT goal_id, goal_type, target, status, start_of_goal, end_of_goal
            FROM FitnessGoal
            WHERE member_id = ?
            """;

        try (PreparedStatement psList = connection.prepareStatement(listQuery)) {
            psList.setInt(1, user_id);

            try (ResultSet rs = psList.executeQuery()) {
                System.out.println("===== Current Fitness Goals =====");
                boolean hasGoals = false;
                while (rs.next()) {
                    hasGoals = true;
                    System.out.println("Goal ID: " + rs.getInt("goal_id")
                            + " | Type: " + rs.getString("goal_type")
                            + " | Target: " + rs.getDouble("target")
                            + " | Status: " + rs.getString("status")
                            + " | Start: " + rs.getDate("start_of_goal")
                            + " | End: " + rs.getDate("end_of_goal"));
                }
                if (!hasGoals) {
                    System.out.println("No fitness goals found for this member.");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving fitness goals:");
            System.out.println(e);
            return;
        }

        System.out.println("Enter the goal_id of the fitness goal you want to modify (or press Enter to cancel):");
        String goalInput = input.nextLine().trim();
        if (goalInput.isEmpty()) {
            System.out.println("Operation cancelled.");
            return;
        }

        Integer goalId = null;
        try {
            goalId = Integer.parseInt(goalInput);
        } catch (Exception e) {
            System.out.println("Invalid number. Cancelling operation.");
            return;
        }

        System.out.println("Do you want to update the status or remove the goal? (update/remove, Enter to cancel):");
        String action = input.nextLine().trim().toLowerCase();
        if (action.isEmpty()) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (action.equals("update")) {
            System.out.println("Enter new status (ongoing, completed, cancelled) or press Enter to cancel:");
            String newStatus = input.nextLine().trim().toLowerCase();
            if (newStatus.isEmpty()) {
                System.out.println("Operation cancelled.");
                return;
            }

            String query = """
                UPDATE FitnessGoal
                SET status = ?, end_of_goal = ?
                WHERE goal_id = ? AND member_id = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, newStatus);

                if (newStatus.equals("completed") || newStatus.equals("cancelled")) {
                    ps.setDate(2, Date.valueOf(LocalDate.now())); // set current date
                } else if (newStatus.equals("ongoing")) {
                    ps.setNull(2, java.sql.Types.DATE); // reset to null
                } else {
                    ps.setNull(2, java.sql.Types.DATE); // default safety
                }

                ps.setInt(3, goalId);
                ps.setInt(4, user_id);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Fitness goal status updated successfully!");
                } else {
                    System.out.println("No fitness goal found with that ID for this member.");
                }
            } catch (Exception e) {
                System.out.println("Error updating fitness goal:");
                System.out.println(e);
            }

        } else if (action.equals("remove")) {
            String query = """
                DELETE FROM FitnessGoal
                WHERE goal_id = ? AND member_id = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, goalId);
                ps.setInt(2, user_id);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Fitness goal removed successfully!");
                } else {
                    System.out.println("No fitness goal found with that ID for this member.");
                }
            } catch (Exception e) {
                System.out.println("Error removing fitness goal:");
                System.out.println(e);
            }

        } else {
            System.out.println("Invalid choice. Please type 'update' or 'remove'.");
        }
    }

    public static void viewHealthHistory(Connection connection) {
        String query = """
            SELECT time, weight, height_cm, bodyfat_percent, bpm
            FROM HealthMetric
            WHERE member_id = ?
            ORDER BY time DESC
            """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("===== Health History =====");

                boolean hasHistory = false;
                while (rs.next()) {
                    hasHistory = true;
                    System.out.println("Date/Time: " + rs.getTimestamp("time")
                            + " | Weight (kg): " + rs.getObject("weight")
                            + " | Height (cm): " + rs.getObject("height_cm")
                            + " | Bodyfat (%): " + rs.getObject("bodyfat_percent")
                            + " | BPM: " + rs.getObject("bpm"));
                }

                if (!hasHistory) {
                    System.out.println("No health history found for member " + user_id);
                }
            }

        } catch (Exception e) {
            System.out.println("Error retrieving health history:");
            System.out.println(e);
        }
    }
}
