package vrs;

import config.dbConnector;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import net.proteanit.sql.DbUtils;
import config.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SCC-COLLEGE
 */
public class home extends javax.swing.JInternalFrame {

    /**
     * Creates new form home
     */
    public home() {
        initComponents();
        displayData();
     this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));   
        javax.swing.plaf.basic.BasicInternalFrameUI bi = (javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI();
     bi.setNorthPane(null);
        // Resize to parent desktop pane if available
        if (getParent() != null && getParent() instanceof javax.swing.JDesktopPane) {
            javax.swing.JDesktopPane parent = (javax.swing.JDesktopPane) getParent();
            this.setSize(parent.getSize());
            this.setLocation(0, 0);
        }
        // Log dashboard panel opened
        try {
            String userIp = "Unknown";
            try { userIp = java.net.InetAddress.getLocalHost().getHostAddress(); } catch (java.net.UnknownHostException e) {}
            String username = System.getProperty("user.name");
            config.Logger.log("VIEW DASHBOARD", "Dashboard panel opened", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }
   
    public void displayData(){
        userCardsPanel.removeAll();
        try {
            dbConnector db = new dbConnector();
            ResultSet rs = db.getData("SELECT * FROM tbl_users");
            while (rs.next()) {
                String name = rs.getString("u_name");
                String username = rs.getString("u_username");
                String email = rs.getString("u_email");
                String phone = rs.getString("u_phone");
                String role = rs.getString("u_role");
                String status = rs.getString("u_status");
                JPanel card = new JPanel();
                card.setLayout(new java.awt.BorderLayout(0, 0));
                card.setPreferredSize(new java.awt.Dimension(400, 180));
                card.setBackground(java.awt.Color.WHITE);
                card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 2, true),
                    javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                // Info panel
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new javax.swing.BoxLayout(infoPanel, javax.swing.BoxLayout.Y_AXIS));
                infoPanel.setBackground(java.awt.Color.WHITE);
                infoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 18));
                nameLabel.setForeground(java.awt.Color.BLACK);
                infoPanel.add(nameLabel);
                infoPanel.add(Box.createVerticalStrut(6));
                JLabel usernameLabel = new JLabel("Username: " + username);
                usernameLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
                infoPanel.add(usernameLabel);
                JLabel emailLabel = new JLabel("Email: " + email);
                emailLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
                infoPanel.add(emailLabel);
                JLabel phoneLabel = new JLabel("Phone: " + phone);
                phoneLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
                infoPanel.add(phoneLabel);
                JLabel roleLabel = new JLabel("Role: " + role);
                roleLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
                infoPanel.add(roleLabel);
                // Status badge
                JLabel statusLabel = new JLabel(status != null ? status.toUpperCase() : "");
                statusLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 12));
                statusLabel.setForeground(java.awt.Color.WHITE);
                statusLabel.setOpaque(true);
                statusLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 12, 4, 12));
                java.awt.Color statusColor;
                if (status != null && status.equalsIgnoreCase("active")) {
                    statusColor = new java.awt.Color(0, 150, 0);
                } else if (status != null && status.equalsIgnoreCase("inactive")) {
                    statusColor = new java.awt.Color(200, 0, 0);
                } else {
                    statusColor = new java.awt.Color(100, 100, 100);
                }
                statusLabel.setBackground(statusColor);
                statusLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                infoPanel.add(Box.createVerticalStrut(8));
                infoPanel.add(statusLabel);
                card.add(infoPanel, java.awt.BorderLayout.CENTER);
                // Hover effect
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 100, 200), 2, true),
                            javax.swing.BorderFactory.createEmptyBorder(14, 14, 14, 14)
                        ));
                    }
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 2, true),
                            javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
                        ));
                    }
                });
                userCardsPanel.add(card);
            }
            rs.close();
            userCardsPanel.revalidate();
            userCardsPanel.repaint();
        } catch (SQLException ex) {
            System.out.println("Errors:" + ex.getMessage());
        }
    }
        
        
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userCardsPanel = new JPanel();
        userCardsPanel.setLayout(new java.awt.GridLayout(0, 2, 20, 20));
        userCardsPanel.setBackground(new java.awt.Color(240, 240, 240));
        jScrollPane1.setViewportView(userCardsPanel);
        jPanel1.setPreferredSize(new java.awt.Dimension(930, 640));
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel userCardsPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
