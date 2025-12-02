import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Admin extends User{
    public static boolean roomBooking(Connection connection, Scanner input){
        try{
            String room_id = "";
            System.out.println("Please enter the Room ID you would like to book");
            while (room_id.isEmpty()) {
                room_id = input.nextLine();
            }
//            Statement statement = connection.createStatement();
//            statement.execute(
//                    "INSERT INTO bookings (first_name, last_name, email, enrollment_date) " +
//                        "VALUES ('" + first_name + "', '" + last_name + "', '" + email + "', '" + enrollment_date + "');"
//            );
        }
        catch(Exception e){
            System.out.println("Error Booking Room");
        }
        return false;
    }

    public static boolean equipmentMaintenece(Connection connection, Scanner input){
        return false;
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
                case "2", "create session for class":
                    sessionMaker(connection,input);
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
            String cName, cDescription, confirm = "";
            while (confirm.isEmpty()) {
                cName = "";
                cDescription = "";

                while(cName.isEmpty()){
                    System.out.println("Enter a class name:");
                    cName = input.nextLine();
                }

                while(cDescription.isEmpty()){
                    System.out.println("Enter a class description:");
                    cDescription = input.nextLine();
                }

                while(!(confirm.toLowerCase().equals("yes")) && !(confirm.toLowerCase().equals("no"))){
                    System.out.println("Created Class: "+cName+"\nClass Overview: "+cDescription);
                    System.out.println("Is this correct? (Yes or No)");
                    confirm = input.nextLine();
                }

                //SQL Operation
                String classInsertQuery = "INSERT INTO Class (class_name, class_description) VALUES (?,?)";
                try (PreparedStatement classIns = connection.prepareStatement(classInsertQuery)) {
                    classIns.setString(1, cName);
                    classIns.setString(2, cDescription);
                } catch (Exception insert) {
                    System.out.println("Can't insert new booking");
                }
            }
        } catch(Exception e){
            System.out.println("Cannot make a new class");
        }
    }

    private static void sessionMaker(Connection connection, Scanner input) {
        try {
            String day, start_time, end_time, confirm = ""; //Initialize values
            LocalTime st = LocalTime.of(0, 0), et = LocalTime.of(0, 0);
            LocalDate date = LocalDate.of(2000, 1, 1);
            int class_id = -1, trainer_id = -1, session_size = -1;

            while (!confirm.toLowerCase().equals("no") || !confirm.isEmpty()) {
                //Reset values
                day = "";
                start_time = "";
                end_time = "";
                confirm = "";

                System.out.println("Class ID");
                while (class_id == -1) {
                    class_id = input.nextInt();
                }

                System.out.println("Trainer ID ");
                while (trainer_id == -1) {
                    trainer_id = input.nextInt();
                }

                System.out.println("Max Session Size ");
                while (session_size == -1) {
                    session_size = input.nextInt();
                }

                //Checks for time conflicts based off the trainer
                date = LocalDate.of(1990, 1, 1);
                boolean noConflicts = false;
                while (!noConflicts) {
                    System.out.println("Day of the class (YYYY-MM-DD)");
                    while (day.isEmpty()) {
                        boolean valid = false;
                        while (day.isEmpty() || !valid) {
                            day = input.nextLine();
                            try {
                                LocalDate.parse(day);
                                valid = true;
                            } catch (Exception e) {
                                System.out.println("Day format invalid");
                                valid = false;
                            }
                        }
                    }


                    st = LocalTime.of(0, 0);
                    boolean valid = false;
                    while (start_time.isEmpty() || !valid) {
                        System.out.println("Start time of class");
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
                        System.out.println("End time of class");
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

                    //Checks trainer shift time
                    Statement trainerCheck = connection.createStatement();
                    trainerCheck.executeQuery("SELECT shift_start,shift_end FROM TrainerAvailability WHERE trainer_id='" + trainer_id + "' AND day='" + day + "' "); //SELECT statement
                    ResultSet trainerAvailability = trainerCheck.getResultSet();

                    LocalTime trainerStart = LocalTime.parse(trainerAvailability.getString("shift_start"));
                    LocalTime trainerEnd = LocalTime.parse(trainerAvailability.getString("shift_end"));

                    boolean trainerAtWork = trainerStart.isBefore(st) && trainerStart.isBefore(et) && trainerEnd.isAfter(st) && trainerEnd.isBefore(et);


                    //Checks other bookings that the trainer has current
                    Statement bookingCheck = connection.createStatement();
                    bookingCheck.executeQuery("SELECT start_time,end_time FROM Bookings WHERE trainer_id='" + trainer_id + "' AND day='" + day + "' "); //SELECT statement
                    ResultSet bookingAvailability = bookingCheck.getResultSet();

                    boolean noExistingAppointments = (bookingAvailability.getString(start_time).isEmpty() && bookingAvailability.getString(end_time).isEmpty());

                    if (noExistingAppointments) {
                        noConflicts = true;
                    } else {
                        noConflicts = true;
                        while (bookingAvailability.next()) {
                            LocalTime bookingST = LocalTime.parse(bookingAvailability.getString("start_time"));
                            LocalTime bookingET = LocalTime.parse(bookingAvailability.getString("end_time"));
                            //User start and end times must both be placed before the start time of another booking or after the end time of another booking
                            boolean noOverlap = et.isBefore(bookingST) || st.isAfter(bookingET);
                            if (!noOverlap && trainerAtWork) {
                                noConflicts = false;
                                break;
                            }
                        }
                    }
                }

                //Confirmation Check
                while (!(confirm.toLowerCase().equals("yes")) && !(confirm.toLowerCase().equals("no"))) {
                    System.out.println("You entered class id: " + class_id + ", trainer id: " + trainer_id + ", day: " + day + ", start time: " + st + ",and end time: " + et + ". Is this correct? (yes or no):");
                    confirm = input.nextLine();
                }

            }

//                while (type != "1" || type != "2" || type.toLowerCase() != "pt" || type.toLowerCase() == "class") {
//                    System.out.println("Please enter the type of session");
//                    System.out.println("1. PT");
//                    System.out.println("2. Class");
//                    type = input.nextLine();
//                    if(type != "1" || type != "2" || type.toLowerCase() != "pt" || type.toLowerCase() == "class"){
//                        System.out.println("Incorrect selection");
//                    }
//                }

//                if(type == "1" || type.toLowerCase() == "pt") {
//                    bookingInsertQuery =
//                            "WITH booking AS (INSERT INTO Bookings (trainer_id, room_id, start_time, end_time, day) VALUES (?,?,?,?,?) RETURNING booking_id) INSERT INTO PTSession(member_id, booking_id, status) VALUES (?, booking_id, ?) FROM booking";
//                }
//                else if(type == "2" || type.toLowerCase() == "class"){}

            //SQL Operations
            Statement classGroup = connection.createStatement();
            classGroup.execute("INSERT INTO ClassGroup (class_id, max_capacity) VALUES ('"+class_id+"','"+session_size+"') RETURNING group_id");
            ResultSet classGroupRS = classGroup.getResultSet();
            int classGroup_id = classGroupRS.getInt("class_id");

            String bookingInsertQuery = "INSERT INTO Bookings (trainer_id, room_id, start_time, end_time, day) VALUES (?,?,?,?,?) RETURNING booking_id";

            int booking_id = -1;
                try (PreparedStatement bookingIns = connection.prepareStatement(bookingInsertQuery)) {
                    //Booking Session
                    bookingIns.setInt(1, trainer_id);
                    bookingIns.setInt(2, -1);
                    bookingIns.setTime(3, java.sql.Time.valueOf(st));
                    bookingIns.setTime(4, java.sql.Time.valueOf(et));
                    bookingIns.setDate(5, java.sql.Date.valueOf(date));

                    booking_id = bookingIns.getResultSet().getInt("booking_id");
                } catch (Exception bookingInsert) {
                    System.out.println("Can't insert new booking");
                }

                //Class Session
                if(booking_id != -1) {
                    String classSessionInsertQuery = "INSERT INTO ClassSession(class_id, group_id, booking_id) VALUES (?, ?, ?) FROM booking";
                    try(PreparedStatement classSessionIns = connection.prepareStatement(classSessionInsertQuery)){
                        classSessionIns.setInt(1, class_id);
                        classSessionIns.setInt(2, classGroup_id);
                        classSessionIns.setInt(3, booking_id);
                    }catch(Exception classInsert){
                        System.out.println("Issue adding class session");
                    }
                }

        } catch (Exception e) {
            System.out.println("Error Booking Room");
        }
    }

}
