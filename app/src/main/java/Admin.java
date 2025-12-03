import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Admin extends User{
    public static int roomBooking(Connection connection, Scanner input, Date day, Time start, Time end){
        int room_id = -1;
        try{

            //SQL SELECT for RoomBooking
            //room booking
            String query = """
                    SELECT room_id, room_name, capacity
                    FROM Room
                    """;
            
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    boolean any = false;

                    while(rs.next()) {
                        System.out.println(String.format("Room_id: %d, %s, Max capacity: %d", rs.getInt("room_id"), rs.getString("room_name"),  rs.getInt("capacity")));
                        any = true;
                    }
                    if(!any) {
                        System.out.println("No rooms exists");
                        return -1;
                    }

                    boolean valid = false;
                    System.out.println("Select which room you want to book.");
                    while(room_id < 0 || !valid) {
                        try {
                            room_id = input.nextInt();
                            valid = true;
                        } catch (Exception e) {
                            System.out.println("Please enter the room ID.");
                        }
                    }
                    
                }
            }

            //boolean valid = false;
            // System.out.println("Please enter the Room ID you would like to book");
            // while (room_id < 0 || !valid) {
            //     try {
            //         room_id = input.nextInt();
            //         valid = true;
            //     } catch (Exception e) {
            //         System.out.println("Please enter a number");
            //     }
            // }
            // Date day = Date.valueOf("0001:01:01");
            // Time start = Time.valueOf("0:01"), end = Time.valueOf("0:01");

            // valid = false;
            // while(!valid) {
            //     System.out.println("What day would you like to book?");
            //     try {
            //         day = Date.valueOf(input.nextLine());
            //         valid = true;
            //     }
            //     catch (Exception e) {
            //         System.out.println("Please enter date in format yyyy-mm-dd");
            //     }
            // }
            // DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");
            // valid = false;
            // while(!valid) {
            //     System.out.println("What time would you like to start?");
            //     try {
            //         start = LocalTime.parse(input.nextLine(), timeFmt);
            //         valid = true;
            //     }
            //     catch (Exception e) {
            //         System.out.println("Please enter time in format hh:mm");
            //     }
            // }
            // valid = false;
            // while(!valid) {
            //     System.out.println("What time would you like to end?");
            //     try {
            //         end = LocalTime.parse(input.nextLine(), timeFmt);
            //         if(end.isBefore(start)) {
            //             System.out.println("End time cannot be before start time");
            //             continue;
            //         }
            //         valid = true;
            //     }
            //     catch (Exception e) {
            //         System.out.println("Please enter time in format hh:mm");
            //     }
            // }

            // if(!Room.available(room_id, day, start, end, connection, input)) {
            //     System.out.println("Room unavailable");
            //     return -1;
            // }
            
            // query = """
            //         SELECT booking_id, start_time, end_time, day
            //         FROM bookings
            //         """;

            // int booking_id = -1;
            
            // try (PreparedStatement ps = connection.prepareStatement(query)){
            //     ResultSet rs = ps.executeQuery();
                
            //     boolean any = false;
            //     while(rs.next()) {
            //         System.out.println(String.format("Booking ID: %d, %s at %s to %s", rs.getInt("booking_id"), rs.getString("day"), rs.getString("start_time"),rs.getString("end_time")));
            //         any = true;
            //     }
            //     if(!any) {
            //         System.out.println("There are no bookings to chose from.");
            //         return -1;
            //     }

            //     System.out.println("Which booking would you like to add the room?");
            //     valid = false;
            //     while(booking_id < 0 || !valid) {
            //         try {
            //             booking_id = input.nextInt();
            //             valid = true;
            //         } catch (Exception e) {
            //             System.out.println("Please enter booking ID");
            //         }
            //     }
            //     query = """
            //             SELECT start_time, end_time, day
            //             FROM BOOKING 
            //             WHERE boooking_id = ?
            //             """;
            //     try (PreparedStatement ps2 = connection.prepareStatement(query)){
            //         ps2.setInt(1, booking_id);
            //         try (ResultSet rs2 = ps.executeQuery()) {
            //             if(!rs2.next()) throw new Exception("Booking not found");

            //             start = rs.getTime("start_time");
            //             end = rs.getTime("end_time");
            //             day = rs.getDate("day");
            //         } catch (Exception e) {
            //             System.err.println("Error getting booking:");
            //             System.err.println(e);
            //             return -1;
            //         }
            //     } 
            // }

            if(!Room.available(room_id, day, start, end, connection, input)) {
                System.out.println("Room is unavailable");
                return -1;
            }

            // query = """
            //         UPDATE bookings
            //         SET room_id = ?
            //         WHERE booking_id = ?
            //         """;
            
            // try (PreparedStatement ps = connection.prepareStatement(query)) {
            //     ps.setInt(1, room_id);
            //     ps.setInt(2, booking_id);
            //     ps.executeUpdate();
            // }

        }
        catch(Exception e){
            System.err.println("Error Booking Room");
            System.err.println(e);
            return -1;
        }
        return room_id;
    }

    public static boolean equipmentMaintenece(Connection connection, Scanner input){
        String confirm = "";
        while(!confirm.toLowerCase().equals("yes")) {
            try {
                int equipment_id = -1, room_id = -1;
                String status = "", description = "";

                //SQL SELECT for EquipmentMaintenance
                Statement equipmentQuery = connection.createStatement();
                equipmentQuery.execute("SELECT equipment_id, name FROM Equipment");
                ResultSet equipmentRS = equipmentQuery.getResultSet();

                while (equipmentRS.next()) {
                    System.out.println("(ID #" + equipmentRS.getInt("equipment_id") + ") Equpiment Name: " + equipmentRS.getString("name"));
                }

                boolean valid = false;
                while (!valid) {
                    try{
                        System.out.println("Enter an equipment id: ");
                        equipment_id = Integer.parseInt(input.nextLine());

                        //SQL SELECT for EquipmentMaintenance
                        equipmentQuery.execute("SELECT equipment_id, name FROM Equipment");
                        equipmentRS = equipmentQuery.getResultSet();
                        while (equipmentRS.next()) {
                            if (equipment_id == equipmentRS.getInt("equipment_id")) {
                                valid = true;
                                break;
                            }
                        }
                        if (!valid) {
                            System.out.println("No equipment with an ID that matches");
                        }
                    } catch (Exception e) {
                        System.out.println("Error when inputting equipment ID:" + e);
                    }
                }

                //SQL SELECT for EquipmentMaintenance
                Statement roomQuery = connection.createStatement();
                roomQuery.execute("SELECT room_id,room_name FROM Room");
                ResultSet roomRS = roomQuery.getResultSet();

                while (roomRS.next()) {
                    System.out.println("(ID #" + roomRS.getInt("room_id") + ") Room Name: " + roomRS.getString("room_name"));
                }

                valid = false;
                while (!valid) {
                    try{
                        System.out.println("Enter a room id that the equipment is in: ");
                        room_id = Integer.parseInt(input.nextLine());

                        //SQL SELECT for EquipmentMaintenance
                        roomQuery.execute("SELECT room_id,room_name FROM Room");
                        roomRS = roomQuery.getResultSet();
                        while (roomRS.next()) {
                            if (room_id == roomRS.getInt("room_id")) {
                                valid = true;
                                break;
                            }
                        }
                        if (!valid) {
                            System.out.println("No room with specified ID");
                        }
                    }
                    catch (Exception e){
                        System.out.println("Error when inputting room ID: "+ e);
                    }

                }

                System.out.println("Enter the status of the equipment");
                while(status.isEmpty()){
                    status = input.nextLine();
                }

                System.out.println("Enter description of the status");
                while (description.isEmpty()) {
                    description = input.nextLine();
                }

                while(!confirm.toLowerCase().equals("yes") && !confirm.toLowerCase().equals("no")){
                    System.out.println("Equipment ID: "+equipment_id+", Room ID: "+room_id+"\n\nStatus: "+status+"\nDetails: "+description+"\n\nIs this correct (yes or no)");
                    confirm = input.nextLine();
                }

                //SQL INSERT for EquipmentMaintenance
                String maintenanceQuery = "INSERT INTO Maintenance (equipment_id, room_id, status, issue_description) VALUES (?, ?, ?, ?)";
                try(PreparedStatement maintenanceIns = connection.prepareStatement(maintenanceQuery)){
                    maintenanceIns.setInt(1, equipment_id);
                    maintenanceIns.setInt(2,room_id);
                    maintenanceIns.setString(3, status);
                    maintenanceIns.setString(4, description);
                    maintenanceIns.executeUpdate();
                    System.out.println("Inserted new maintenance record for equipment");
                }catch(Exception maintenanceInsert){
                    System.out.println("Cannot insert maintenance record");
                }
            } catch (Exception mainenanceLog) {
                System.out.println("Issue recording maintenance log");
            }
        }
        return false;
    }

    public static void displayEquipmentStatus(Connection connection, Scanner input) {
        String query = """
                SELECT equipment_id, name
                FROM Equipment
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)){
            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;
                while(rs.next()) {
                    System.out.println(String.format("Equipment ID: %d, %s", rs.getInt("equipment_id"), rs.getString("name")));
                    any = true;
                }
                if(!any) {
                    System.out.println("No equipment found.");
                    return;
                }

                int option = -1;
                boolean valid = false;
                while (option < 0 || !valid) {
                    try {
                        System.out.println("Please select an equipment ID");
                        option = Integer.parseInt(input.nextLine());
                        valid = true;
                    } catch (Exception e) {
                        System.out.println("Please make sure you are entering a number");
                    }
                }

                query = """
                        SELECT status
                        FROM Maintenance
                        WHERE equipment_id = ?
                        ORDER BY report_date DESC
                        LIMIT 1
                        """;
                try (PreparedStatement ps2 = connection.prepareStatement(query)) {
                    ps2.setInt(1, option);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        if(!rs2.next()){
                            System.out.println("No maintenance logs for selected item");
                            return;
                        }
                        System.out.printf("Equipment status: %s\n", rs2.getString("status"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting equipment status:");
            System.err.println(e);
        }
    }

    public static boolean classManagement(Connection connection, Scanner input){
        String option;
        loop: while (true) {
            System.out.println("What would you like to complete? (q or quit to exit)");
            System.out.println("1. Create new class");
            System.out.println("2. Create session for class");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "q", "quit":
                    break loop;
                case "1", "create new class":
                    classMaker(connection,input);
                    break;
                case "2", "create session for class":
                    sessionMaker(connection,input);
                    break;
                default:
                    System.out.println("Invalid input");
                    System.out.println("\n");
            }
        }
        return false;
    }

    private static void classMaker(Connection connection, Scanner input){
        try {
            //Initialization
            String cName ="", cDescription = "", confirm = "";
            while (!confirm.toLowerCase().equals("yes")) {
                cName = "";
                cDescription = "";
                confirm = "";

                while (cName.isEmpty()) {
                    System.out.println("Enter a class name:");
                    cName = input.nextLine();
                }

                while (cDescription.isEmpty()) {
                    System.out.println("Enter a class description:");
                    cDescription = input.nextLine();
                }

                while (!(confirm.toLowerCase().equals("yes")) && !(confirm.toLowerCase().equals("no"))) {
                    System.out.println("Created Class: " + cName + "\nClass Overview: " + cDescription);
                    System.out.println("Is this correct? (Yes or No)");
                    confirm = input.nextLine();
                }
            }

            //SQL SELECT for ClassManagement Operation (Class Creation)
            String classInsertQuery = "INSERT INTO Class (name, description) VALUES (?,?)";
            try (PreparedStatement classIns = connection.prepareStatement(classInsertQuery)) {
                classIns.setString(1, cName);
                classIns.setString(2, cDescription);

                try{
                   classIns.executeUpdate();
                   System.out.println("Added new class successfully!");
                   return;
                }catch(Exception insertCompletion){
                    System.out.println("Issue inserting new class into database");
                    return;
                }

            } catch (Exception insertValues) {
                System.out.println("Can't insert values new class");
                return;
            }
        } catch(Exception e){
            System.out.println("Cannot make a new class");
            return;
        }
    }

    private static void sessionMaker(Connection connection, Scanner input) {
        try {
            String day, start_time, end_time, confirm = ""; //Initialize values
            LocalTime st = LocalTime.of(0, 0), et = LocalTime.of(0, 0);
            LocalDate date = LocalDate.of(2000, 1, 1);
            int class_id = -1, trainer_id = -1, session_size = -1;

            while (!confirm.toLowerCase().equals("yes")) {
                //Reset values
                day = "";
                start_time = "";
                end_time = "";
                confirm = "";

                //SQL SELECT for ClassManagement (SessionMaker)
                Statement classList = connection.createStatement();
                classList.execute("SELECT class_id,name from Class");
                ResultSet classRS = classList.getResultSet();
                boolean classIDCheck = false;
                while (!classIDCheck) {
                    classList.execute("SELECT class_id,name from Class");
                    classRS = classList.getResultSet();
                    while(classRS.next()){
                        System.out.println("(ID #"+classRS.getInt("class_id")+") Class: "+classRS.getString("name"));
                    }
                    System.out.println("Enter Class ID from one above:");
                    class_id = input.nextInt();

                    //Checks to ensure input is an actual class id in the database
                    classList.execute("SELECT class_id,name from Class");
                    classRS = classList.getResultSet();
                    while(classRS.next()){
                        if(class_id == classRS.getInt("class_id")){
                            classIDCheck = true;
                            break;
                        }
                    }
                    if(!classIDCheck){
                        System.out.println("No class with specified ID");
                    }

                }

                //SQL SELECT for ClassManagement (SessionMaker)
                Statement trainerList = connection.createStatement();
                trainerList.execute("SELECT user_id,first_name,last_name,user_type FROM \"User\" WHERE user_type=1 ");
                ResultSet trainerRS = trainerList.getResultSet();
                boolean trainerIDCheck = false;
                while (!trainerIDCheck) {
                    trainerList.execute("SELECT user_id,first_name,last_name,user_type FROM \"User\" WHERE user_type=1 ");
                    trainerRS = trainerList.getResultSet();
                    while(trainerRS.next()){
                        System.out.println("(ID #"+trainerRS.getInt("user_id")+") Trainer: "+trainerRS.getString("first_name")+" "+trainerRS.getString("last_name"));
                    }
                    System.out.println("Enter Trainer ID:");
                    trainer_id = input.nextInt();

                    //Compares user input to trainers in the database
                    trainerList.execute("SELECT user_id,first_name,last_name,user_type FROM \"User\" WHERE user_type=1 ");
                    trainerRS = trainerList.getResultSet();
                    while(trainerRS.next()){
                        if(trainer_id == trainerRS.getInt("user_id")){
                            trainerIDCheck = true;
                            break;
                        }
                    }
                    if(!trainerIDCheck){
                        System.out.println("No trainer with specified ID");
                    }
                }

                while (session_size == -1) {
                    System.out.println("Max Session Size:");
                    session_size = input.nextInt();
                }

                //Checks for time conflicts based off the trainer
                date = LocalDate.of(1990, 1, 1);
                boolean noConflicts = false;
                while (!noConflicts) {
                    System.out.println("Day of the class (YYYY-MM-DD)");
                    boolean valid = false;
                    while (day.isEmpty()||!valid) {
                        day = input.nextLine();
                        try {
                            date = LocalDate.parse(day);
                            valid = true;
                        } catch (Exception e) {
                            System.out.println("Day format invalid");
                            valid = false;
                        }
                    }


                    st = LocalTime.of(0, 0);
                    valid = false;
                    while (start_time.isEmpty() || !valid) {
                        System.out.println("Start time of class (24hr clock, HH:MM)");
                        start_time = input.nextLine();
                        try {
                            st = LocalTime.parse(start_time);
                            valid = true;
                        } catch (Exception e) {
                            System.out.println("Start time format invalid");
                            valid = false;
                        }
                    }

                    et = LocalTime.of(0, 0);
                    valid = false;
                    while (end_time.isEmpty() || !valid) {
                        System.out.println("End time of class (24hr clock, HH:MM)");
                        end_time = input.nextLine();
                        try {
                            et = LocalTime.parse(end_time);
                            if (st.isAfter(et)) {
                                System.out.println("End time is before the start time (must be after the start time)");
                            } else {
                                valid = true;
                            }
                        } catch (Exception e) {
                            System.out.println("End time format invalid");
                            valid = false;
                        }
                    }

                    System.out.println("A");

                    //Checks trainer shift time
                    //SQL SELECT for ClassManagement (SessionMaker)
                    Statement trainerCheck = connection.createStatement();
                    trainerCheck.executeQuery("SELECT shift_start,shift_end FROM TrainerAvailability WHERE trainer_id=" + trainer_id + " AND day='" + day + "' "); //SELECT statement
                    ResultSet trainerAvailability = trainerCheck.getResultSet();

                    System.out.println("B");

                    boolean trainerAtWork = false;
                    try {
                        trainerAvailability.next();
                        LocalTime trainerStart = LocalTime.parse(trainerAvailability.getString("shift_start"));
                        LocalTime trainerEnd = LocalTime.parse(trainerAvailability.getString("shift_end"));

                        trainerAtWork = trainerStart.isBefore(st) && trainerStart.isBefore(et) && trainerEnd.isAfter(st) && trainerEnd.isBefore(et);
                    } catch (Exception e){
                        System.out.println("Trainer schedule unavailable/unassigned, will assume at work");
                        trainerAtWork = true;
                    }

                    System.out.println("C");

                    //Checks other bookings that the trainer has current
                    Statement bookingCheck = connection.createStatement();
                    bookingCheck.executeQuery("SELECT start_time,end_time FROM Bookings WHERE trainer_id='" + trainer_id + "' AND day='" + day + "' "); //SELECT statement
                    ResultSet bookingAvailability = bookingCheck.getResultSet();

                    System.out.println("D");

                    noConflicts = true;
                    while (bookingAvailability.next() && !bookingAvailability.getString("start_time").isEmpty() && !bookingAvailability.getString("end_time").isEmpty()) {
                        LocalTime bookingST = LocalTime.parse(bookingAvailability.getString("start_time"));
                        LocalTime bookingET = LocalTime.parse(bookingAvailability.getString("end_time"));
                        //User start and end times must both be placed before the start time of another booking or after the end time of another booking
                        boolean noOverlap = et.isBefore(bookingST) || st.isAfter(bookingET);
                        if (!noOverlap && trainerAtWork) {
                            noConflicts = false;
                            System.out.println("F.2");
                            break;
                        }
                    }
                }

                //Confirmation Check
                while (!(confirm.toLowerCase().equals("yes")) && !(confirm.toLowerCase().equals("no"))) {
                    System.out.println("You entered class id: " + class_id + ", trainer id: " + trainer_id + ", day: " + day + ", start time: " + st + ",and end time: " + et + ". Is this correct? (yes or no):");
                    confirm = input.nextLine();
                }

            }

            //SQL Class Session inertion

            //SQL INSERT for ClassManagement (SessionMaker)
            //Booking Insert
            String bookingInsertQuery = "INSERT INTO Bookings (trainer_id, room_id, start_time, end_time, day) VALUES (?,?,?,?,?) RETURNING booking_id";
            int booking_id = -1;
                try (PreparedStatement bookingIns = connection.prepareStatement(bookingInsertQuery)) {

                    //Booking Session
                    bookingIns.setInt(1, trainer_id);
                    bookingIns.setInt(2, roomBooking(connection, input, java.sql.Date.valueOf(date), java.sql.Time.valueOf(st), java.sql.Time.valueOf(et)));
                    bookingIns.setTime(3, java.sql.Time.valueOf(st));
                    bookingIns.setTime(4, java.sql.Time.valueOf(et));
                    bookingIns.setDate(5, java.sql.Date.valueOf(date));

                    try{
                        ResultSet bookingRS = bookingIns.executeQuery();
                        bookingRS.next();
                        booking_id = bookingRS.getInt("booking_id");
                        System.out.println("Booking (ID #"+booking_id+") for new class session added");
                    }catch(Exception bookingAdd){
                        System.out.println("Cannot add new booking\n"+bookingAdd);
                    }
                } catch (Exception bookingInsert) {
                    System.out.println("Can't insert values new booking\n"+bookingInsert);
                }

                //SQL INSERT for ClassManagement (SessionMaker)
                //Class Session
                if(booking_id != -1) {
                    String classSessionInsertQuery = "INSERT INTO ClassSession(class_id, group_id, booking_id) VALUES (?, ?, ?)";
                    try(PreparedStatement classSessionIns = connection.prepareStatement(classSessionInsertQuery)){
                        //SQL INSERT for ClassManagement (SessionMaker)
                        //Class Group Insert
                        Statement classGroup = connection.createStatement();
                        classGroup.execute("INSERT INTO ClassGroup (class_id, max_capacity) VALUES ('"+class_id+"','"+session_size+"') RETURNING group_id");
                        ResultSet classGroupRS = classGroup.getResultSet();
                        classGroupRS.next();
                        int classGroup_id = classGroupRS.getInt("group_id");

                        classSessionIns.setInt(1, class_id);
                        classSessionIns.setInt(2, classGroup_id);
                        classSessionIns.setInt(3, booking_id);

                        try{
                            classSessionIns.executeUpdate();
                            System.out.println("Added class session!");
                        }catch(Exception classSessionAdd){
                            System.out.println("Issue adding new class session");
                        }
                    }catch(Exception classSessionInsert){
                        System.out.println("Issue inserting values class session");
                    }
                }

        } catch (Exception e) {
            System.out.println("Error Creating Session");
            System.out.println(e);
        }
    }

}