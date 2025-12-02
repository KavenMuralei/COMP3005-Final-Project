import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Scanner;

public class Room {
    public static boolean available(int room_id, Date day, Time start_time, Time end_time, Connection connection, Scanner input) {
        String query = """
                SELECT start_time, end_time
                FROM Bookings
                WHERE room_id = ? AND day = ?
                """;
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, room_id);
            ps.setDate(2, day);
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    Time rm_starTime = rs.getTime("start_time");
                    Time rm_endtime = rs.getTime("end_time");

                    if(start_time.before(rm_endtime) && end_time.after(rm_starTime))
                        return false;
                }
            } catch (Exception e) {
                System.err.println("Error finding room:");
                System.err.println(e);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error connecting to database:");
            System.err.println(e);
            return false;
        }

        return true;
    }
}
