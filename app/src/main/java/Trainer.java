import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Trainer extends User{
    private static int trainer_id;

    public static void logout() {
        trainer_id = -1;
        User.logout();
    }

    public static void fetchTrainerId(Connection connection) {
        String query = """
                SELECT trainer_id 
                FROM Trainer
                WHERE user_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, user_id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Trainer not found.");
                    return; 
                }
                trainer_id = rs.getInt(1);
            } catch (Exception e) {
                System.out.println("Error getting trainer_id:");
                System.out.println(e);
                return;
            }
        }
        catch (Exception e) {
            System.out.println("Error connection to database:");
            System.out.println(e);
        }
    }

    public static void displaySchedule(Connection connection) {
        String sql = """
            SELECT b.booking_id,
                b.day,
                b.start_time,
                b.end_time,
                b.room_id,
                'PT' AS session_type
            FROM Bookings b
            JOIN PTSession p ON p.booking_id = b.booking_id
            WHERE b.trainer_id = ?
            UNION ALL
            SELECT b.booking_id,
                b.day,
                b.start_time,
                b.end_time,
                b.room_id,
                'CLASS' AS session_type
            FROM Bookings b
            JOIN ClassSession c ON c.booking_id = b.booking_id
            WHERE b.trainer_id = ?
            ORDER BY day, start_time;
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trainer_id);
            ps.setInt(2, trainer_id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("Booking ID: %s, %s %s to %s room:%s type:%s",
                        rs.getInt("booking_id"),
                        rs.getDate("day"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getInt("room_id"),
                        rs.getString("session_type")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching schedule:");
            System.out.println(e);
        }
    }

    public static void displayAvailability(Connection connection) {
        String query = """
                SELECT *
                FROM TrainerAvailability
                WHERE trainer_id = ?
                """;
        
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setInt(1, trainer_id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("%s, from %s to %s",
                        rs.getString("day"),
                        rs.getString("shift_start"),
                        rs.getString("shift_end")
                    );
                }
            } catch (Exception e) {
                System.err.println("Error fetching availability:");
                System.err.println(e);
            }
        } catch (Exception e) {
            System.err.println("Error connecting to database:");
            System.err.println(e);
        }
    }

    private static void addAvailability(Connection connection, Scanner input) {
        String query = """
                INSERT INTO TrainerAvailability (trainer_id, day, shift_start, shift_end) 
                VALUES (?, ?, ?, ?) 
                """;
        boolean valid = false;
        String day = "";
        while(day.isEmpty() || !valid) {
            System.out.println("What day would you like to add availability (Can only have 1 availability per day)");
            day = input.nextLine();
            try {
                LocalDate.parse(day);
                valid = true;
            } catch (Exception e) {
                valid = false;
            }
        }

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm"); // accepts "9:00" or "09:00"
        LocalTime startTime = null;
        while (startTime == null) {
            System.out.println("What is the start time of your availability? (e.g. 9:00)");
            String start = input.nextLine().trim();
            try {
                startTime = LocalTime.parse(start, timeFmt);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Use H:mm (e.g. 9:00 or 09:00).");
            }
        }

        LocalTime endTime = null;
        while (endTime == null) {
            System.out.println("What is the end time of your availability? (e.g. 17:30)");
            String end = input.nextLine().trim();
            try {
                endTime = LocalTime.parse(end, timeFmt);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Use H:mm (e.g. 17:30).");
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, trainer_id);
            ps.setDate(2, Date.valueOf(day));
            ps.setTime(3, java.sql.Time.valueOf(startTime));
            ps.setTime(4, java.sql.Time.valueOf(endTime));
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error adding availability:");
            System.err.println(e);
        }
    }

    private static void removeAvailability(Connection connection, Scanner input) {
        String query = """
                DELETE FROM TrainerAvailability
                WHERE trainer_id = ? AND day = ?
                """;
        
        
        boolean valid = false;
        String day = "";
        while(day.isEmpty() || !valid) {
            System.out.println("What day would you like to remove availability");
            day = input.nextLine();
            try {
                LocalDate.parse(day);
                valid = true;
            } catch (Exception e) {
                valid = false;
            }
        }
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, trainer_id);
            ps.setDate(2, Date.valueOf(day));

            try {
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Error removing availability:");
                System.err.println(e);
            }
        } catch (Exception e) {
            System.err.println("Error connecting to database:");
            System.err.println(e);
        }
    }

    public static void setAvailability(Connection connection, Scanner input) {

        String change_type = "";

        loop: while(true) {
            switch(change_type) {
                case "add" -> {
                    addAvailability(connection, input);
                    change_type = "";
                }
                case "remove" -> {
                    removeAvailability(connection, input);
                    change_type = "";
                }
                case "q" -> { break loop; }
                default -> {
                    System.out.println("Current availability:");
                    displayAvailability(connection);
                    System.out.println("\n");
                    System.out.println("Would you like to add or remove an availability?");
                    change_type = input.nextLine();
                }
            }
        }
    }
}
