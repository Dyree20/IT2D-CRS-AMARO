

package vrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Vrs {

    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/vrs";
        String user = ""; // Change if using another user
        String password = "";

        try {
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database Connected Successfully!");
            con.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection Failed: " + e.getMessage());
        }
    }
}
    
    


