import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.Scanner;

public class Room {
    public static boolean available(int room_id, Date day, Time start_time, Time end_time, Connection connection, Scanner input) {
        String query = """
                SELECT day, start_time, end_time
                FROM Room
                WHERE room_id = ?
                """;
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, room_id);
            try (ResultSet rs = ps.executeQuery()){
                if(!rs.next()) {
                    System.out.println("Room not found");
                    return false;
                }

                while(rs.next()) {
                    Date rm_day = rs.getDate("day");
                    Time rm_starTime = rs.getTime("start_time");
                    Time rm_endtime = rs.getTime("end_time");

                    if((start_time.after(rm_starTime) && end_time.before(rm_endtime)) || (start_time.before(rm_endtime) && end_time.after(rm_starTime)) && day.equals(rm_day))
                        return false;
                }
            } catch (Exception e) {
                System.err.println("Error finding room:");
                System.err.println(e);
            }
        } catch (Exception e) {
            System.err.println("Error connecting to database:");
            System.err.println(e);
        }

        return true;
    }
}
