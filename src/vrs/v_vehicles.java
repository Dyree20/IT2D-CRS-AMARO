/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrs;

import config.dbConnector;
import config.Logger;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ROCO
 */
public class v_vehicles extends javax.swing.JInternalFrame {

    /**
     * Creates new form v_vehicles
     */
    public v_vehicles() {
        initComponents();
        
        loadVehicleCards(txtSearch.getText().trim(), (String)mv_type.getSelectedItem());
        
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        javax.swing.plaf.basic.BasicInternalFrameUI bi = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
                    bi.setNorthPane(null);
        // Resize to parent desktop pane if available
        if (getParent() != null && getParent() instanceof javax.swing.JDesktopPane) {
            javax.swing.JDesktopPane parent = (javax.swing.JDesktopPane) getParent();
            this.setSize(parent.getSize());
            this.setLocation(0, 0);
        }
        // Log viewing the vehicles panel
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("VIEW VEHICLES", "Vehicles panel opened", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
        // Add action listeners for search and clear
        jButton1.addActionListener(e -> {
            String searchText = txtSearch.getText().trim();
            String selectedType = (String) mv_type.getSelectedItem();
            loadVehicleCards(searchText, selectedType);
        });
        jButton2.addActionListener(e -> {
            txtSearch.setText("");
            mv_type.setSelectedIndex(0);
            loadVehicleCards("", (String)mv_type.getSelectedItem());
        });
        // Add action listener for mv_type combo box
        mv_type.addActionListener(e -> {
            String searchText = txtSearch.getText().trim();
            String selectedType = (String) mv_type.getSelectedItem();
            loadVehicleCards(searchText, selectedType);
        });
        // Set mv_type options
        mv_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AUV", "SUV", "VAN" }));
    }

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
        loadVehicleCards(txtSearch.getText().trim(), (String)mv_type.getSelectedItem()); // Load vehicles when the form opens
    }
    
    
private void loadVehicleCards(String searchText, String selectedType) {
    try {
        JPanel containerPanel = (JPanel) jScrollPane1.getViewport().getView();
        containerPanel.removeAll();
        containerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        containerPanel.setBackground(new Color(240, 240, 240)); // Light gray background

        Connection con = new dbConnector().getConnection();
        StringBuilder query = new StringBuilder("SELECT * FROM tbl_vehicles");
        boolean hasType = selectedType != null && !selectedType.equals("All");
        boolean hasSearch = searchText != null && !searchText.isEmpty();
        if (hasType || hasSearch) {
            query.append(" WHERE ");
            if (hasType) {
                query.append("v_type = ?");
            }
            if (hasSearch) {
                if (hasType) query.append(" AND ");
                query.append("(LOWER(v_make) LIKE ? OR LOWER(v_model) LIKE ? OR LOWER(v_type) LIKE ?)");
            }
        }
        PreparedStatement pst = con.prepareStatement(query.toString());
        int paramIndex = 1;
        if (hasType) {
            pst.setString(paramIndex++, selectedType);
        }
        if (hasSearch) {
            String likeText = "%" + searchText.toLowerCase() + "%";
            pst.setString(paramIndex++, likeText);
            pst.setString(paramIndex++, likeText);
            pst.setString(paramIndex++, likeText);
        }
        ResultSet rs = pst.executeQuery();
        boolean found = false;
        while (rs.next()) {
            found = true;
            // Get vehicle data
            final int vehicleId = rs.getInt("v_id");
            final String make = rs.getString("v_make");
            final String model = rs.getString("v_model");
            final String year = rs.getString("v_year");
            final String plate = rs.getString("v_plate");
            final String rate = rs.getString("v_rate");
            final String status = rs.getString("v_status");
            final String vType = rs.getString("v_type");
            byte[] imageData = rs.getBytes("v_image");
            
            // Create a card panel for this vehicle
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BorderLayout(0, 0));
            cardPanel.setPreferredSize(new Dimension(320, 400));
            cardPanel.setBackground(Color.WHITE);
            cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Image panel (top)
            JPanel imagePanel = new JPanel();
            imagePanel.setPreferredSize(new Dimension(300, 200));
            imagePanel.setBackground(Color.WHITE);
            imagePanel.setLayout(new BorderLayout());

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            if (imageData != null && imageData.length > 0) {
                ImageIcon imageIcon = new ImageIcon(imageData);
                Image img = imageIcon.getImage().getScaledInstance(280, 180, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No Image Available");
                imageLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
                imageLabel.setForeground(Color.GRAY);
            }
            imagePanel.add(imageLabel, BorderLayout.CENTER);

            // Info panel (bottom)
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            // Vehicle name
            JLabel nameLabel = new JLabel(make + " " + model);
            nameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));

            // Vehicle type and year
            JLabel typeYearLabel = new JLabel(vType + " • " + year);
            typeYearLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
            typeYearLabel.setForeground(new Color(100, 100, 100));
            typeYearLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(typeYearLabel);
            infoPanel.add(Box.createVerticalStrut(5));

            // Plate number
            JLabel plateLabel = new JLabel("Plate: " + plate);
            plateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
            plateLabel.setForeground(new Color(100, 100, 100));
            plateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(plateLabel);
            infoPanel.add(Box.createVerticalStrut(5));

            // Price
            JLabel priceLabel = new JLabel("₱" + rate + " per day");
            priceLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            priceLabel.setForeground(new Color(0, 100, 0));
            priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(priceLabel);
            infoPanel.add(Box.createVerticalStrut(10));

            // Status badge
            JPanel statusPanel = new JPanel();
            statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            statusPanel.setBackground(Color.WHITE);
            statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel statusLabel = new JLabel(status.toUpperCase());
            statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            // Set status color
            Color statusColor;
            if (status.equalsIgnoreCase("available")) {
                statusColor = new Color(0, 150, 0); // Green
            } else if (status.equalsIgnoreCase("rented")) {
                statusColor = new Color(200, 0, 0); // Red
            } else if (status.equalsIgnoreCase("maintenance")) {
                statusColor = new Color(255, 140, 0); // Orange
            } else {
                statusColor = new Color(100, 100, 100); // Gray
            }
            statusLabel.setBackground(statusColor);
            statusLabel.setOpaque(true);
            statusPanel.add(statusLabel);

            infoPanel.add(statusPanel);

            // Add panels to card
            cardPanel.add(imagePanel, BorderLayout.NORTH);
            cardPanel.add(infoPanel, BorderLayout.CENTER);

            // Add hover effect
            cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 100, 200), 2),
                        BorderFactory.createEmptyBorder(9, 9, 9, 9)
                    ));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            });

            // Add the card to the container
            containerPanel.add(cardPanel);
        }
        
        if (!found) {
            JPanel noResultsPanel = new JPanel();
            noResultsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            noResultsPanel.setBackground(new Color(240, 240, 240));
            
            JLabel noVehiclesLabel = new JLabel("No vehicles found matching your criteria");
            noVehiclesLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            noVehiclesLabel.setForeground(new Color(100, 100, 100));
            noResultsPanel.add(noVehiclesLabel);
            
            containerPanel.add(noResultsPanel);
        }
        
        // Refresh the container
        containerPanel.revalidate();
        containerPanel.repaint();
        
        rs.close();
        pst.close();
        con.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
    }
}

    private void logSearchAction() {
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            String searchTerm = txtSearch.getText();
            Logger.log("SEARCH VEHICLES", "Search performed: '" + searchTerm + "'", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void logClearAction() {
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("CLEAR SEARCH", "Vehicle search cleared", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void logTypeFilterAction() {
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            String selectedType = (String) mv_type.getSelectedItem();
            Logger.log("FILTER VEHICLES", "Vehicle type filter selected: '" + selectedType + "'", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        vehicleCardsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        mv_type = new javax.swing.JComboBox<>();

        jScrollPane1.setBackground(new java.awt.Color(102, 0, 0));
        jScrollPane1.setViewportView(vehicleCardsPanel);

        jPanel1.setBackground(new java.awt.Color(110, 50, 50));

        jButton1.setText("Search");

        jButton2.setText("Clear");

        mv_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AUV", "SUV", "VAN" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mv_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(534, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(mv_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> mv_type;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JPanel vehicleCardsPanel;
    // End of variables declaration//GEN-END:variables
}
