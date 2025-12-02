import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    private static void memberLoop(Connection connection, Scanner input) {
        while (true) {
            System.out.println("Member selection Options:");
            System.out.println("1. Profile Management");
            System.out.println("2. Health History");
            System.out.println("3. Dashboard");
            System.out.println("4. PT Session Scheduling");
            System.out.println("5. Group Class Registration");
            System.out.println("6. Logout");
            System.out.println("------------------------------------------------");
            System.out.print("Select an option: ");

            String option = input.nextLine();

            switch (option) {
                case "1":
                    loop: while (true) {
                        System.out.println("Profile Management Options:");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Email");
                        System.out.println("3. Change Password");
                        System.out.println("4. Change Phone Number");
                        System.out.println("5. Change Date of Birth");
                        System.out.println("6. Change Gender");
                        System.out.println("7. Add Fitness Goal");
                        System.out.println("8. Manage Fitness Goals");
                        System.out.println("9. Add/Update Health Metrics");
                        System.out.println("10. Back");
                        System.out.println("------------------------------------------------");
                        System.out.print("Choose an option: ");

                        String pm = input.nextLine();

                        switch (pm) {
                            case "1": User.changeName(connection, input); break;
                            case "2": User.changeEmail(connection, input); break;
                            case "3": User.changePassword(connection, input); break;
                            case "4": Member.changePhoneNumber(connection, input); break;
                            case "5": Member.changeDoB(connection, input); break;
                            case "6": Member.changeGender(connection, input); break;
                            case "7": Member.addFitnessGoal(connection, input); break;
                            case "8": Member.manageFitnessGoals(connection, input); break;
                            case "9": Member.manageHealthMetrics(connection, input); break;
                            case "10": break loop;  // Back to main menu
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;
                case "2": Member.viewHealthHistory(connection); break;
                case "3": Member.dashboard(connection); break;
                case "4": Member.managePTSession(connection, input); break;
                case "5": Member.registerForGroupClass(connection, input);break;
                case "6": User.logout(); System.out.println("You have been logged out."); return;

                default:
                    System.out.println("Invalid selection.");
            }
        }
    }


    private static void trainerLoop(Connection connection, Scanner input) {

        while (true) {
            System.out.println("Trainer selection Options:");
            System.out.println("1. Profile Management");
            System.out.println("2. Set Availability");
            System.out.println("3. Schedule View");
            System.out.println("4. Member Lookup");
            System.out.println("5. Logout");
            System.out.println("------------------------------------------------");
            System.out.print("Select an option: ");

            String option = input.nextLine();

            switch (option) {
                case "1":
                    loop: while (true) {
                        System.out.println("Profile Management Options:");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Email");
                        System.out.println("3. Change Password");
                        System.out.println("4. Back");
                        System.out.println("------------------------------------------------");
                        System.out.print("Choose an option: ");

                        String pm = input.nextLine();

                        switch (pm) {
                            case "1": User.changeName(connection, input); break;
                            case "2": User.changeEmail(connection, input); break;
                            case "3": User.changePassword(connection, input); break;
                            case "4": break loop;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;
                case "2": Trainer.setAvailability(connection, input);break;
                case "3": Trainer.displaySchedule(connection);break;
                case "4": Trainer.searchMember(connection, input);break;
                case "5": User.logout(); System.out.println("You have been logged out."); return;
                default: System.out.println("Invalid selection.");
            }
        }
    }


    private static int adminLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select an *Admin* operation:");
            System.out.println("1. Room Booking");
            System.out.println("2. Equipment Maintenance");
            System.out.println("3. Class Management");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "q", "quit":
                    break loop;
                case "1", "room booking":
                    Admin.roomBooking(connection, input);
                    break;
                case "2", "equipment maintenance":
                case "3", "class management":
                default:
                    System.out.println("Invalid input");
                System.out.println("\n");
            }    
        }
        System.out.println("admin loop exited");
        return 0;
    }
  
    public static void mainLoop(Connection connection, Scanner input) {
        loop: while (true) {
            String option = "";
            System.out.println("Login or Register? ('q'/'quit' to quit)");
            option = input.nextLine().toLowerCase();

            switch (option) {
                case "login" -> {
                    User.logIn(connection, input);
                    switch (User.getUserType()) {
                        case 0 -> memberLoop(connection, input);
                        case 1 -> trainerLoop(connection, input);
                        case 2 -> adminLoop(connection, input);
                        case -1 -> System.out.println("Wrong username or password.");
                        default -> {
                            System.out.println("Error, unexpected user type");
                            break loop;
                        }
                    }
                }
                case "register" -> User.registerUser(connection, input);
                case "q", "quit" -> { break loop; }
                default -> {
                    System.out.println("Please enter 'login' or 'register'. ('q'/'quit' to quit)");
                    option = input.nextLine().toLowerCase();
                }
            }
        }
    }

    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        try{
            String url = "jdbc:postgresql://localhost:5432/finalproject";
            String user = "postgres";
            String password = "admin";
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url,user,password);
            if (connection != null) {
                System.out.println("Connected to database");

                mainLoop(connection, input);
                
                connection.close();
            } else {
                System.out.println("Failed to connect to database.");
            }
        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }

        System.out.println("program terminating");
        input.close();
    }

}
