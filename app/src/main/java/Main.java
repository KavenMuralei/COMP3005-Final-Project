import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    private static int memberLoop(Connection connection, Scanner input) {
        Member.fetchMemberId(connection);

        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "change name":
                    User.changeName(connection, input);
                    break;
                case "change email":
                    User.changeEmail(connection, input);
                    break;
                case "change password":
                    User.changePassword(connection, input);
                    break;
                case "change phone number":
                    Member.changePhoneNumber(connection, input);
                    break;
                case "Change date of birth":
                    Member.changeDoB(connection, input);
                    break;
                case "change gender":
                    Member.changeGender(connection, input);
                    break;
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
            }    
            System.out.println("\n");
        }
        System.out.println("member loop exited");
        return 0;
    }

    private static int trainerLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "change name":
                    User.changeName(connection, input);
                    break;
                case "change email":
                    User.changeEmail(connection, input);
                    break;
                case "change password":
                    User.changePassword(connection, input);
                    break;
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
            }    
            System.out.println("\n");
        }
        System.out.println("trainer loop exited");
        return 0;
    }

    private static int adminLoop(Connection connection, Scanner input) {
        String option;
        loop: while (true) {
            System.out.println("Please select operation:");
            option = input.nextLine();
            switch (option.toLowerCase()) {
                case "change name":
                    User.changeName(connection, input);
                    break;
                case "change email":
                    User.changeEmail(connection, input);
                    break;
                case "change password":
                    User.changePassword(connection, input);
                    break;
                case "q", "quit":
                    break loop;
                default:
                    System.out.println("Invalid input");
            }    
            System.out.println("\n");
        }
        System.out.println("admin loop exited");
        return 0;
    }
  
    public static void mainLoop(Connection connection, Scanner input) {
        loop: while (true) {
            String option = "";
            System.out.println("Login or Register?");
            option = input.nextLine().toLowerCase();

            switch (option) {
                case "login" -> {
                    User.logIn(connection, input);
                    switch (User.getUserType()) {
                        case 0 -> memberLoop(connection, input);
                        case 1 -> trainerLoop(connection, input);
                        case 2 -> adminLoop(connection, input);
                    
                        default -> {
                            System.out.println("Error, unexpected user type");
                            return;
                        }
                    }
                }
                case "register" -> User.registerUser(connection, input);
                case "q", "quit" -> { break loop; }
                default -> {
                    System.out.println("Please enter 'login' or 'register'");
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
