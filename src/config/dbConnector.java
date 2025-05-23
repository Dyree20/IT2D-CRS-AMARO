package config;

import java.sql.*;
import javax.swing.JOptionPane;

public class dbConnector {
    public Connection connect;

    public dbConnector() {
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/crs", "root", "");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Connection Error: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connect;
    }

    public ResultSet getData(String sql) throws SQLException {
        Statement stmt = connect.createStatement();
        return stmt.executeQuery(sql);
    }

    public boolean insertData(String sql) {
        try {
            PreparedStatement pst = connect.prepareStatement(sql);
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Insert Error: " + ex.getMessage());
            return false;
        }
    }

    public void updateData(String sql) {
        try {
            PreparedStatement pst = connect.prepareStatement(sql);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Data Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "No Data Updated!");
            }
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Update Error: " + ex.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connect != null && !connect.isClosed()) {
                connect.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error closing connection: " + e.getMessage());
        }
    }
}

