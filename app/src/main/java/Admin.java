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

            //SQL Operation
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
                            LocalDate.parse(day);
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

            //SQL Insertions

            //Booking Insert
            String bookingInsertQuery = "INSERT INTO Bookings (trainer_id, room_id, start_time, end_time, day) VALUES (?,?,?,?,?) RETURNING booking_id";
            int booking_id = -1;
                try (PreparedStatement bookingIns = connection.prepareStatement(bookingInsertQuery)) {

                    //Booking Session
                    bookingIns.setInt(1, trainer_id);
                    bookingIns.setInt(2, -1);
                    bookingIns.setTime(3, java.sql.Time.valueOf(st));
                    bookingIns.setTime(4, java.sql.Time.valueOf(et));
                    bookingIns.setDate(5, java.sql.Date.valueOf(date));

                    try{
                        bookingIns.executeUpdate();
                        booking_id = bookingIns.getResultSet().getInt("booking_id");
                        System.out.println("Booking (ID #"+booking_id+") for new class session added");
                    }catch(Exception bookingAdd){
                        System.out.println("Cannot add new booking\n"+bookingAdd);
                    }
                } catch (Exception bookingInsert) {
                    System.out.println("Can't insert values new booking\n"+bookingInsert);
                }

                //Class Session
                if(booking_id != -1) {
                    String classSessionInsertQuery = "INSERT INTO ClassSession(class_id, group_id, booking_id) VALUES (?, ?, ?) FROM booking";
                    try(PreparedStatement classSessionIns = connection.prepareStatement(classSessionInsertQuery)){
                        //Class Group Insert
                        Statement classGroup = connection.createStatement();
                        classGroup.execute("INSERT INTO ClassGroup (class_id, max_capacity) VALUES ('"+class_id+"','"+session_size+"') RETURNING group_id");
                        ResultSet classGroupRS = classGroup.getResultSet();
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