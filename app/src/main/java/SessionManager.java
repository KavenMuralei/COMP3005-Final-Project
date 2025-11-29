public class SessionManager {
    static int user_id;
    static String email;
    static String password; // Probably wont need this one. might remove for "security" reasons

    public static void setEmail(String _email) {
        email = _email;
    }

    public static void setUserId(int _user_id) {
        user_id = _user_id;
    }

    public static void setPassword(String _password) {
        _password = password;
    }

    public static String getEmail() {
        return email;
    }

    public static int getUserId() {
        return user_id;
    }

    public static String getPassword() {
        return password;
    }
}
