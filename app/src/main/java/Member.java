import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
    public static void managePTSession(Connection connection, Scanner input) {
        System.out.println("Would you like to book, reschedule, or remove a PT session? (book/reschedule/remove, Enter to cancel):");
        String action = input.nextLine().trim().toLowerCase();
        if (action.isEmpty()) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            if (action.equals("book")) {
                // Show trainer availability
                String availQuery = """
                SELECT trainer_id, day, shift_start, shift_end
                FROM TrainerAvailability
                ORDER BY trainer_id, day, shift_start
                """;
                try (PreparedStatement ps = connection.prepareStatement(availQuery);
                     ResultSet rs = ps.executeQuery()) {
                    System.out.println("===== Trainer Availabilities =====");
                    while (rs.next()) {
                        System.out.println("Trainer ID: " + rs.getInt("trainer_id")
                                + " | Day: " + rs.getDate("day")
                                + " | Shift: " + rs.getTime("shift_start") + " - " + rs.getTime("shift_end"));
                    }
                }

                // Show available rooms
                String roomQuery = """
                SELECT room_id, room_name, capacity, location_details
                FROM Room
                ORDER BY room_id
                """;
                try (PreparedStatement ps = connection.prepareStatement(roomQuery);
                     ResultSet rs = ps.executeQuery()) {
                    System.out.println("===== Available Rooms =====");
                    while (rs.next()) {
                        System.out.println("Room ID: " + rs.getInt("room_id")
                                + " | Name: " + rs.getString("room_name")
                                + " | Capacity: " + rs.getInt("capacity")
                                + " | Location: " + rs.getString("location_details"));
                    }
                }

                // Now prompt for trainer and room
                System.out.println("Enter trainer_id:");
                int trainerId = Integer.parseInt(input.nextLine().trim());

                System.out.println("Enter room_id:");
                int roomId = Integer.parseInt(input.nextLine().trim());

                System.out.println("Enter date (yyyy-MM-dd):");
                LocalDate day = LocalDate.parse(input.nextLine().trim());

                System.out.println("Enter start time (HH:mm):");
                LocalTime startTime = LocalTime.parse(input.nextLine().trim());

                System.out.println("Enter end time (HH:mm):");
                LocalTime endTime = LocalTime.parse(input.nextLine().trim());

                //check if trainer is available
                String availabilityQuery = """
                SELECT 1 FROM TrainerAvailability
                WHERE trainer_id = ? AND day = ?
                  AND shift_start <= ? AND shift_end >= ?
                """;
                try (PreparedStatement ps = connection.prepareStatement(availabilityQuery)) {
                    ps.setInt(1, trainerId);
                    ps.setDate(2, Date.valueOf(day));
                    ps.setTime(3, Time.valueOf(startTime));
                    ps.setTime(4, Time.valueOf(endTime));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("Trainer not available at that time.");
                            return;
                        }
                    }
                }
                //check for time conflicts
                String conflictQuery = """
                SELECT 1 FROM Bookings
                WHERE room_id = ? AND day = ?
                  AND ((start_time < ? AND end_time > ?) OR (start_time < ? AND end_time > ?))
                """;
                try (PreparedStatement ps = connection.prepareStatement(conflictQuery)) {
                    ps.setInt(1, roomId);
                    ps.setDate(2, Date.valueOf(day));
                    ps.setTime(3, Time.valueOf(endTime));
                    ps.setTime(4, Time.valueOf(startTime));
                    ps.setTime(5, Time.valueOf(startTime));
                    ps.setTime(6, Time.valueOf(endTime));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Room conflict detected.");
                            return;
                        }
                    }
                }
                //insert into booking table
                String bookingQuery = """
                INSERT INTO Bookings (trainer_id, room_id, start_time, end_time, day)
                VALUES (?, ?, ?, ?, ?)
                RETURNING booking_id
                """;
                int bookingId;
                try (PreparedStatement ps = connection.prepareStatement(bookingQuery)) {
                    ps.setInt(1, trainerId);
                    ps.setInt(2, roomId);
                    ps.setTime(3, Time.valueOf(startTime));
                    ps.setTime(4, Time.valueOf(endTime));
                    ps.setDate(5, Date.valueOf(day));

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        bookingId = rs.getInt("booking_id");
                    }
                }

                //insert into ptsession table
                String ptQuery = """
                INSERT INTO PTSession (member_id, booking_id, status)
                VALUES (?, ?, 'scheduled')
                """;
                try (PreparedStatement ps = connection.prepareStatement(ptQuery)) {
                    ps.setInt(1, user_id);
                    ps.setInt(2, bookingId);
                    ps.executeUpdate();
                }

                System.out.println("PT session booked successfully!");

            } else if (action.equals("reschedule") || action.equals("remove")) {

                //find the sessions
                String sessionQuery = """
                SELECT pt.pt_session_id, b.day, b.start_time, b.end_time, b.room_id, b.trainer_id
                FROM PTSession pt
                JOIN Bookings b ON b.booking_id = pt.booking_id
                WHERE pt.member_id = ?
                ORDER BY b.day, b.start_time
                """;
                try (PreparedStatement ps = connection.prepareStatement(sessionQuery)) {
                    ps.setInt(1, user_id);
                    try (ResultSet rs = ps.executeQuery()) {
                        System.out.println("===== Your PT Sessions =====");
                        boolean hasSessions = false;
                        while (rs.next()) {
                            hasSessions = true;
                            System.out.println("Session ID: " + rs.getInt("pt_session_id")
                                    + " | Trainer: " + rs.getInt("trainer_id")
                                    + " | Room: " + rs.getInt("room_id")
                                    + " | Date: " + rs.getDate("day")
                                    + " | Time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time"));
                        }
                        if (!hasSessions) {
                            System.out.println("No PT sessions found.");
                            return;
                        }
                    }
                }

                System.out.println("Enter pt_session_id to " + action + ":");
                int ptSessionId = Integer.parseInt(input.nextLine().trim());

                if (action.equals("reschedule")) {
                    System.out.println("Enter new date (yyyy-MM-dd):");
                    LocalDate newDay = LocalDate.parse(input.nextLine().trim());

                    System.out.println("Enter new start time (HH:mm):");
                    LocalTime newStart = LocalTime.parse(input.nextLine().trim());

                    System.out.println("Enter new end time (HH:mm):");
                    LocalTime newEnd = LocalTime.parse(input.nextLine().trim());
                    //updates the current booking
                    String updateQuery = """
                    UPDATE Bookings
                    SET day = ?, start_time = ?, end_time = ?
                    WHERE booking_id = (SELECT booking_id FROM PTSession WHERE pt_session_id = ? AND member_id = ?)
                    """;
                    try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
                        ps.setDate(1, Date.valueOf(newDay));
                        ps.setTime(2, Time.valueOf(newStart));
                        ps.setTime(3, Time.valueOf(newEnd));
                        ps.setInt(4, ptSessionId);
                        ps.setInt(5, user_id);

                        int rows = ps.executeUpdate();
                        if (rows > 0) {
                            System.out.println("PT session rescheduled successfully!");
                        } else {
                            System.out.println("No PT session found to reschedule.");
                        }
                    }

                } else if (action.equals("remove")) {
                    //deletes the session from the table
                    String deleteQuery = """
                    DELETE FROM PTSession WHERE pt_session_id = ? AND member_id = ?
                    """;
                    try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
                        ps.setInt(1, ptSessionId);
                        ps.setInt(2, user_id);

                        int rows = ps.executeUpdate();
                        if (rows > 0) {
                            System.out.println("PT session removed successfully!");
                        } else {
                            System.out.println("No PT session found to remove.");
                        }
                    }
                }
            } else {
                System.out.println("Invalid choice. Please type 'book', 'reschedule', or 'remove'.");
            }

        } catch (Exception e) {
            System.out.println("Error managing PT session:");
            System.out.println(e);
        }
    }
    public static void registerForGroupClass(Connection connection, Scanner input) {
        try {
            // Show available group classes with capacity info
            String classQuery = """
            SELECT cg.group_id, c.name AS class_name, cg.max_capacity,
                   COUNT(jg.member_id) AS registered_count
            FROM ClassGroup cg
            JOIN Class c ON c.class_id = cg.class_id
            LEFT JOIN Join_Group jg ON cg.group_id = jg.group_id
            GROUP BY cg.group_id, c.name, cg.max_capacity
            ORDER BY cg.group_id
            """;

            try (PreparedStatement ps = connection.prepareStatement(classQuery);
                 ResultSet rs = ps.executeQuery()) {
                System.out.println("===== Available Group Classes =====");
                boolean hasClasses = false;
                while (rs.next()) {
                    hasClasses = true;
                    int groupId = rs.getInt("group_id");
                    String className = rs.getString("class_name");
                    int maxCapacity = rs.getInt("max_capacity");
                    int registered = rs.getInt("registered_count");

                    System.out.println("Group ID: " + groupId
                            + " | Class: " + className
                            + " | Capacity: " + registered + "/" + maxCapacity);
                }
                if (!hasClasses) {
                    System.out.println("No group classes scheduled.");
                    return;
                }
            }


            System.out.println("Enter group_id to register (or press Enter to cancel):");
            String inputGroup = input.nextLine().trim();
            if (inputGroup.isEmpty()) {
                System.out.println("Operation cancelled.");
                return;
            }
            int groupId = Integer.parseInt(inputGroup);

            //checks the capacity
            String capacityQuery = """
            SELECT cg.max_capacity, COUNT(jg.member_id) AS registered_count
            FROM ClassGroup cg
            LEFT JOIN Join_Group jg ON cg.group_id = jg.group_id
            WHERE cg.group_id = ?
            GROUP BY cg.max_capacity
            """;
            try (PreparedStatement ps = connection.prepareStatement(capacityQuery)) {
                ps.setInt(1, groupId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int maxCapacity = rs.getInt("max_capacity");
                        int registered = rs.getInt("registered_count");
                        if (registered >= maxCapacity) {
                            System.out.println("Class group is full. Cannot register.");
                            return;
                        }
                    } else {
                        System.out.println("Invalid group_id.");
                        return;
                    }
                }
            }
            //check to see if member is already registered
            String checkQuery = """
            SELECT 1 FROM Join_Group WHERE member_id = ? AND group_id = ?
            """;
            try (PreparedStatement ps = connection.prepareStatement(checkQuery)) {
                ps.setInt(1, user_id);
                ps.setInt(2, groupId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("You are already registered for this group class.");
                        return;
                    }
                }
            }

            // registration query
            String insertQuery = """
            INSERT INTO Join_Group (group_id, member_id, enrollment_date)
            VALUES (?, ?, CURRENT_DATE)
            """;
            try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
                ps.setInt(1, groupId);
                ps.setInt(2, user_id);
                ps.executeUpdate();
                System.out.println("Successfully registered for group class " + groupId + "!");
            }

        } catch (Exception e) {
            System.out.println("Error registering for group class:");
            e.printStackTrace();
        }
    }
}
