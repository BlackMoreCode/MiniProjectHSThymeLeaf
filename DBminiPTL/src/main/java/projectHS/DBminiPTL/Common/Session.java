package projectHS.Common;

public class Session {
    public static String loggedInUserId = null; // For normal customers
    public static String storeId = null;         // Store ID for admin users
    public static boolean isAdminLoggedIn = false; // For admin login status
    public static boolean isHQLoggedIn = false;   // For HQ login status
    public static String userRole = null;          // To keep track of the user's role (e.g., customer, admin, HQ)

    public static void clear() {
        loggedInUserId = null;
        storeId = null; // Clear store ID on logout
        isAdminLoggedIn = false;
        isHQLoggedIn = false;
        userRole = null; // Clear user role
    }
}