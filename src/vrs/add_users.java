/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrs;
import config.dbConnector;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import config.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 *
 * @author ROCO
 */
public class add_users extends javax.swing.JInternalFrame {

    private int selectedUserId = -1;
    
    
    public add_users() {
        initComponents();
        
            
        
        // Remove window borders
            this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                    BasicInternalFrameUI bi = (BasicInternalFrameUI) this.getUI();
                    bi.setNorthPane(null);
// Set up role combo box
                role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Admin", "Employee"}));
// Set up table columns
                setupTable();
// Load users from database
                loadUsers();
// In your add_users constructor, after initializing components:
jTable1.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        if (jTable1.getSelectedRow() != -1) {
            selectUser(); // User is selected
        } else {
            // User is deselected
            resetForm();
        }
    
    }
});
// Add action listeners to buttons
edit.addActionListener(e -> editUser());
delete.addActionListener(e -> deleteUser());
search_button.addActionListener(e -> searchUsers());
c_status.addActionListener(e -> toggleStatus());
add.addActionListener(e -> addOrUpdateUser());
    }
    
     private void setupTable() {
        // Set up table model with proper columns
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "ID", "Name", "Username", "Email", "Phone", "Role", "Status"
            }
        ) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }
    
    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows
        
        try {
            Connection conn = new dbConnector().getConnection();
            String query = "SELECT * FROM tbl_users";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("u_id"),
                    rs.getString("u_name"),
                    rs.getString("u_username"),
                    rs.getString("u_email"),
                    rs.getString("u_phone"),
                    rs.getString("u_role"),
                    rs.getString("u_status")
                });
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error loading users: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchUsers() {
        String searchTerm = search.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadUsers(); // Load all users if search is empty
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Clear existing rows
        
        try {
            Connection conn = new dbConnector().getConnection();
            String query = "SELECT * FROM tbl_users WHERE u_name LIKE ? OR u_username LIKE ? OR u_email LIKE ?";
            PreparedStatement pst = conn.prepareStatement(query);
            
            String searchPattern = "%" + searchTerm + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("u_id"),
                    rs.getString("u_name"),
                    rs.getString("u_username"),
                    rs.getString("u_email"),
                    rs.getString("u_phone"),
                    rs.getString("u_role"),
                    rs.getString("u_status")
                });
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Error searching users: " + ex.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectUser() {
        int row = jTable1.getSelectedRow();
        if (row >= 0) {
            selectedUserId = (int) jTable1.getValueAt(row, 0);
            fullname.setText((String) jTable1.getValueAt(row, 1));
            username.setText((String) jTable1.getValueAt(row, 2));
            email.setText((String) jTable1.getValueAt(row, 3));
            pass.setText(""); // Don't show password
            phone.setText((String) jTable1.getValueAt(row, 4));
            role.setSelectedItem(jTable1.getValueAt(row, 5));
            
            // Change button text
            add.setText("UPDATE USER");
        }
    }
    
   private void resetForm() {
    selectedUserId = -1;
    fullname.setText("");
    username.setText("");
    email.setText("");
    pass.setText("");
    phone.setText("");
    role.setSelectedIndex(0);
    // Don't clear selection here as this could cause infinite loop
    // jTable1.clearSelection();
    add.setText("ADD USER");
}
    
    private void editUser() {
        if (jTable1.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }
        // User already selected in the selectUser() method
        // This just ensures the row is selected
    }
    
    private void deleteUser() {
        if (jTable1.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this user?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String adminUser = System.getProperty("user.name");
            String deletedUsername = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 2);
            try {
                Connection conn = new dbConnector().getConnection();
                String query = "DELETE FROM tbl_users WHERE u_id = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, selectedUserId);
                
                int result = pst.executeUpdate();
                if (result > 0) {
                    Logger.log("Delete User", "Deleted user: " + deletedUsername, adminUser, userIp);
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    resetForm();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.");
                }
                
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                        "Database error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void toggleStatus() {
        if (jTable1.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }
        
        String currentStatus = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 6);
        String newStatus = currentStatus.equals("Active") ? "Inactive" : "Active";
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        String adminUser = System.getProperty("user.name");
        String toggledUsername = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 2);
        try {
            Connection conn = new dbConnector().getConnection();
            String query = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, newStatus);
            pst.setInt(2, selectedUserId);
            
            int result = pst.executeUpdate();
            if (result > 0) {
                Logger.log("Toggle User Status", "User: " + toggledUsername + " status changed to: " + newStatus, adminUser, userIp);
                JOptionPane.showMessageDialog(this, "User status updated to: " + newStatus);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user status.");
            }
            
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing not available", e);
        }
    }
    
    private void addOrUpdateUser() {
        // Validation
        if (fullname.getText().isEmpty() || username.getText().isEmpty() || 
                email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }
        
        // New users must have a password
        if (selectedUserId == -1 && new String(pass.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new users.");
            return;
        }
        
        String name = fullname.getText();
        String user = username.getText();
        String emailText = email.getText();
        String password = new String(pass.getPassword());
        String phoneText = phone.getText();
        String roleText = (String) role.getSelectedItem();
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        String adminUser = System.getProperty("user.name");
        try {
            Connection conn = new dbConnector().getConnection();
            
            if (selectedUserId == -1) {
                // Add new user
                String query = "INSERT INTO tbl_users (u_name, u_username, u_email, u_password, u_phone, u_role, u_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, user);
                pst.setString(3, emailText);
                pst.setString(4, hashPassword(password));
                pst.setString(5, phoneText);
                pst.setString(6, roleText);
                pst.setString(7, "Active");
                
                int result = pst.executeUpdate();
                if (result > 0) {
                    Logger.log("Add User", "Added user: " + user, adminUser, userIp);
                }
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } else {
                // Update existing user
                String query;
                PreparedStatement pst;
                
                if (password.isEmpty()) {
                    // Update without changing password
                    query = "UPDATE tbl_users SET u_name=?, u_username=?, u_email=?, u_phone=?, u_role=? WHERE u_id=?";
                    pst = conn.prepareStatement(query);
                    pst.setString(1, name);
                    pst.setString(2, user);
                    pst.setString(3, emailText);
                    pst.setString(4, phoneText);
                    pst.setString(5, roleText);
                    pst.setInt(6, selectedUserId);
                } else {
                    // Update including password
                    query = "UPDATE tbl_users SET u_name=?, u_username=?, u_email=?, u_password=?, u_phone=?, u_role=? WHERE u_id=?";
                    pst = conn.prepareStatement(query);
                    pst.setString(1, name);
                    pst.setString(2, user);
                    pst.setString(3, emailText);
                    pst.setString(4, hashPassword(password));
                    pst.setString(5, phoneText);
                    pst.setString(6, roleText);
                    pst.setInt(7, selectedUserId);
                }
                
                int result = pst.executeUpdate();
                if (result > 0) {
                    Logger.log("Update User", "Updated user: " + user, adminUser, userIp);
                }
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            }
            
            conn.close();
            resetForm();
            loadUsers();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                    "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Include existing generated code as is (don't modify it)
    // ...


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        main = new javax.swing.JPanel();
        users = new javax.swing.JPanel();
        username = new javax.swing.JTextField();
        fullname = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        pass = new javax.swing.JPasswordField();
        phone = new javax.swing.JTextField();
        role = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        user_m = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        search = new javax.swing.JTextField();
        search_button = new javax.swing.JButton();
        edit = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        c_status = new javax.swing.JButton();
        user_d = new javax.swing.JPanel();

        main.setPreferredSize(new java.awt.Dimension(930, 589));
        main.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        users.setBackground(new java.awt.Color(102, 0, 0));

        role.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        role.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roleActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Username:");

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Full Name:");

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Email:");

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Password:");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Phone Number:");

        add.setFont(new java.awt.Font("Microsoft YaHei", 3, 14)); // NOI18N
        add.setText("ADD USER");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout usersLayout = new javax.swing.GroupLayout(users);
        users.setLayout(usersLayout);
        usersLayout.setHorizontalGroup(
            usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addGroup(usersLayout.createSequentialGroup()
                        .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(username, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .addComponent(fullname)
                            .addComponent(email)
                            .addComponent(jLabel2))
                        .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(usersLayout.createSequentialGroup()
                                .addGap(124, 124, 124)
                                .addComponent(role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(usersLayout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(usersLayout.createSequentialGroup()
                                        .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(pass)
                                                .addComponent(phone, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
                                            .addComponent(jLabel5))
                                        .addGap(105, 105, 105)
                                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );
        usersLayout.setVerticalGroup(
            usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(usersLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(usersLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fullname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, usersLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(usersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(role, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );

        main.add(users, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, 210));

        user_m.setBackground(new java.awt.Color(110, 5, 0));
        user_m.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBackground(new java.awt.Color(110, 5, 0));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        user_m.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 920, 163));
        user_m.add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 150, -1));

        search_button.setText("Search");
        user_m.add(search_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, -1, -1));

        edit.setText("Edit");
        user_m.add(edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, -1, -1));

        delete.setText("Delete");
        user_m.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 10, -1, -1));

        c_status.setText("Change Status");
        c_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_statusActionPerformed(evt);
            }
        });
        user_m.add(c_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 10, -1, -1));

        main.add(user_m, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 920, 200));

        user_d.setBackground(new java.awt.Color(102, 50, 50));

        javax.swing.GroupLayout user_dLayout = new javax.swing.GroupLayout(user_d);
        user_d.setLayout(user_dLayout);
        user_dLayout.setHorizontalGroup(
            user_dLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 920, Short.MAX_VALUE)
        );
        user_dLayout.setVerticalGroup(
            user_dLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );

        main.add(user_d, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 920, 190));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void roleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roleActionPerformed
         String selectedRole = (String) role.getSelectedItem();
    
    // You can add logic here if you want different behavior based on role selection
    // For example:
    if ("Admin".equals(selectedRole)) {
        // If you want to update anything in the UI when Admin is selected
        // For example, maybe enable additional options or change label colors
    } else if ("Employee".equals(selectedRole)) {
        // If you want to update anything in the UI when Employee is selected
    }
    }//GEN-LAST:event_roleActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
       addOrUpdateUser();
    }//GEN-LAST:event_addActionPerformed

    private void c_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_statusActionPerformed
        toggleStatus();
    }//GEN-LAST:event_c_statusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton c_status;
    private javax.swing.JButton delete;
    private javax.swing.JButton edit;
    private javax.swing.JTextField email;
    private javax.swing.JTextField fullname;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel main;
    private javax.swing.JPasswordField pass;
    private javax.swing.JTextField phone;
    private javax.swing.JComboBox<String> role;
    private javax.swing.JTextField search;
    private javax.swing.JButton search_button;
    private javax.swing.JPanel user_d;
    private javax.swing.JPanel user_m;
    private javax.swing.JTextField username;
    private javax.swing.JPanel users;
    // End of variables declaration//GEN-END:variables
}
