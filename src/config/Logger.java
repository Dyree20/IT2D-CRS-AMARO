package config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Logger {
    private static final String INSERT_LOG = "INSERT INTO tbl_user_logs (log_action, log_details, log_user, log_ip) VALUES (?, ?, ?, ?)";

    public static void log(String action, String details, String user, String ip) {
        try {
            Connection con = new dbConnector().getConnection();
            PreparedStatement pst = con.prepareStatement(INSERT_LOG);
            pst.setString(1, action);
            pst.setString(2, details);
            pst.setString(3, user);
            pst.setString(4, ip); // You can pass null if you don't have IP
            pst.executeUpdate();
            pst.close();
            con.close();
        } catch (SQLException ex) {
            System.err.println("Error logging activity: " + ex.getMessage());
        }
    }

    // Convenience methods
    public static void logLogin(String user, boolean success, String ip) {
        String action = success ? "Login Success" : "Login Failed";
        String details = success ? "User logged in successfully" : "Failed login attempt";
        log(action, details, user, ip);
    }

    public static void logLogout(String user, String ip) {
        log("Logout", "User logged out", user, ip);
    }

    public static void logVehicleAction(String username, String action, String vehicleDetails) {
        log(action, vehicleDetails, username, null);
    }
    
    public static void logClientAction(String username, String action, String clientDetails) {
        log(action, clientDetails, username, null);
    }
    
    public static void logRentalAction(String username, String action, String rentalDetails) {
        log(action, rentalDetails, username, null);
    }
    
    public static void logUserAction(String username, String action, String userDetails) {
        log(action, userDetails, username, null);
    }
} 