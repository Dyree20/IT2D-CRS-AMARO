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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import com.toedter.calendar.JDateChooser;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
/**
 *
 * @author ROCO
 */
public class add_clients extends javax.swing.JInternalFrame {
    
    private javax.swing.JTextField txtClientEmail;
    private int selectedVehicleId = -1;
    private JPanel selectedCardPanel = null;
    private Color originalCardColor = new Color(128, 0, 0); // Dark red background
    private Color selectedCardColor = new Color(200, 100, 0); // Orange-ish highlight
    
    /**
     * Creates new form add_clients
     */
    public add_clients() {
        initComponents();
        setupComponents();
        // Remove border and title bar for borderless internal frame
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));   
        javax.swing.plaf.basic.BasicInternalFrameUI bi = (javax.swing.plaf.basic.BasicInternalFrameUI)this.getUI();
        bi.setNorthPane(null);
        
        // Initialize the vehicle cards container
        if (vehicleCardsContainer != null) {
            vehicleCardsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            vehicleCardsContainer.setBackground(new Color(180, 180, 180));
        }
        
        // Initialize the filter combo box
        if (cboFilter != null) {
            cboFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Truck", "Motorcycle", "AUV", "SUV" }));
        }
        
        // Initialize buttons and labels
        if (btnRentVehicle != null) btnRentVehicle.setEnabled(false);
        if (btnViewDetails != null) btnViewDetails.setEnabled(false);
        if (lblSelectedVehicle != null) lblSelectedVehicle.setText("Selected Vehicle: None");
        
        // Load vehicle cards
        loadVehicleCards();
        
        // Add listeners for full search functionality
        if (btnSearch != null) btnSearch.addActionListener(this::btnSearchActionPerformed);
        if (cboFilter != null) cboFilter.addActionListener(e -> btnSearchActionPerformed(null));
        if (txtSearch != null) txtSearch.addActionListener(e -> btnSearchActionPerformed(null));
    }
    
    private void setupComponents() {
        // Set FlowLayout for the vehicle cards container
        vehicleCardsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Update filter combo box items
        cboFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { 
            "All", "Truck", "Motorcycle", "AUV", "SUV" 
        }));
        
        // Set the selected vehicle label text
        lblSelectedVehicle.setText("Selected Vehicle: None");
    }

    private void loadVehicleCards() {
        try {
            vehicleCardsContainer.removeAll();
            vehicleCardsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
            vehicleCardsContainer.setBackground(new Color(240, 240, 240));

            Connection con = new dbConnector().getConnection();
            StringBuilder query = new StringBuilder("SELECT * FROM tbl_vehicles");
            boolean hasFilter = !cboFilter.getSelectedItem().toString().equals("All");
            String searchText = txtSearch.getText().trim().toLowerCase();
            boolean hasSearch = !searchText.isEmpty();
            if (hasFilter || hasSearch) {
                query.append(" WHERE ");
                if (hasFilter) {
                    query.append("v_type = ?");
                }
                if (hasSearch) {
                    if (hasFilter) query.append(" AND ");
                    query.append("(LOWER(v_make) LIKE ? OR LOWER(v_model) LIKE ? OR LOWER(v_type) LIKE ?)");
                }
            }
            PreparedStatement pst = con.prepareStatement(query.toString());
            int paramIndex = 1;
            if (hasFilter) {
                pst.setString(paramIndex++, cboFilter.getSelectedItem().toString());
            }
            if (hasSearch) {
                String likeText = "%" + searchText + "%";
                pst.setString(paramIndex++, likeText);
                pst.setString(paramIndex++, likeText);
                pst.setString(paramIndex++, likeText);
            }
            ResultSet rs = pst.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                final int vehicleId = rs.getInt("v_id");
                final String make = rs.getString("v_make");
                final String model = rs.getString("v_model");
                final String year = rs.getString("v_year");
                final String plate = rs.getString("v_plate");
                final String rate = rs.getString("v_rate");
                final String status = rs.getString("v_status");
                final String vType = rs.getString("v_type");
                byte[] imageData = rs.getBytes("v_image");
                
                final JPanel cardPanel = createVehicleCard(make, model, year, plate, 
                    rate, status, vType, imageData, Color.WHITE, new Dimension(320, 400));
                
                cardPanel.putClientProperty("vehicleId", vehicleId);
                cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        selectVehicleCard(cardPanel, vehicleId, make + " " + model);
                    }
                });
                
                vehicleCardsContainer.add(cardPanel);
            }
            
            if (count == 0) {
                JPanel noResultsPanel = new JPanel();
                noResultsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                noResultsPanel.setBackground(new Color(240, 240, 240));
                
                JLabel noVehiclesLabel = new JLabel("No vehicles found matching your criteria");
                noVehiclesLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
                noVehiclesLabel.setForeground(new Color(100, 100, 100));
                noResultsPanel.add(noVehiclesLabel);
                
                vehicleCardsContainer.add(noResultsPanel);
            }
            
            vehicleCardsContainer.revalidate();
            vehicleCardsContainer.repaint();
            
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading vehicles: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createVehicleCard(String make, String model, String year, String plate, 
                                   String rate, String status, String vType, byte[] imageData, 
                                   Color bgColor, Dimension cardSize) {
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
                if (cardPanel != selectedCardPanel) {
                    cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 100, 200), 2),
                        BorderFactory.createEmptyBorder(9, 9, 9, 9)
                    ));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (cardPanel != selectedCardPanel) {
                    cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
        });

        return cardPanel;
    }

    private void selectVehicleCard(JPanel cardPanel, int vehicleId, String vehicleName) {
        // If there was a previously selected card, reset its border
        if (selectedCardPanel != null) {
            selectedCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }
        
        // Update the selected vehicle and card
        selectedVehicleId = vehicleId;
        selectedCardPanel = cardPanel;
        
        // Change the selected card's border
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 200), 3),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Update the selected vehicle label
        lblSelectedVehicle.setText("Selected Vehicle: " + vehicleName);
        
        // Get the status from the card's status label
        String status = null;
        for (java.awt.Component comp : ((JPanel)cardPanel.getComponent(1)).getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (java.awt.Component label : panel.getComponents()) {
                    if (label instanceof JLabel) {
                        JLabel statusLabel = (JLabel) label;
                        status = statusLabel.getText();
                        break;
                    }
                }
            }
        }
        
        // Enable the rent and view buttons only if available
        if (status != null && status.equalsIgnoreCase("AVAILABLE")) {
            btnRentVehicle.setEnabled(true);
        } else {
            btnRentVehicle.setEnabled(false);
        }
        btnViewDetails.setEnabled(true);
        
        // Repaint to show the changes
        vehicleCardsContainer.repaint();
    }
    // Additional reset method (add this too)
    private void resetSelection() {
        selectedVehicleId = -1;
        selectedCardPanel = null;
        lblSelectedVehicle.setText("Selected Vehicle: None");
        btnRentVehicle.setEnabled(false);
        btnViewDetails.setEnabled(false);
    }
    // Add this variable declaration at the class leve    
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        String searchText = txtSearch.getText().trim();
        String filterType = cboFilter.getSelectedItem() != null ? cboFilter.getSelectedItem().toString() : "All";
        // Log search action
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("SEARCH VEHICLE", "Search text: '" + searchText + "', Type: '" + filterType + "'", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
        loadVehicleCards();
    }
    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
        txtSearch.setText("");
        loadVehicleCards();
        // Log clear action
        try {
            String userIp = "Unknown";
            try { userIp = java.net.InetAddress.getLocalHost().getHostAddress(); } catch (java.net.UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("CLEAR CLIENT SEARCH", "Cleared client search/filter", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }


    private void cboFilterActionPerformed(java.awt.event.ActionEvent evt) {
        loadVehicleCards();
        // Log filter action
        try {
            String userIp = "Unknown";
            try { userIp = java.net.InetAddress.getLocalHost().getHostAddress(); } catch (java.net.UnknownHostException e) {}
            String username = System.getProperty("user.name");
            String filterType = cboFilter.getSelectedItem() != null ? cboFilter.getSelectedItem().toString() : "All";
            Logger.log("FILTER CLIENTS", "Filter selected: '" + filterType + "'", username, userIp);
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

        jPanel1 = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        cboFilter = new javax.swing.JComboBox<>();
        vehicleDisplayPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vehicleCardsContainer = new javax.swing.JPanel();
        actionPanel = new javax.swing.JPanel();
        btnRentVehicle = new javax.swing.JButton();
        btnViewDetails = new javax.swing.JButton();
        lblSelectedVehicle = new javax.swing.JLabel();
        edit_client = new javax.swing.JButton();

        jPanel1.setLayout(new BorderLayout());

        searchPanel.setBackground(new java.awt.Color(110, 0, 0));

        btnSearch.setText("SEARCH");

        btnClear.setText("CLEAR");

        cboFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Truck", "Motorcycle", "AUV", "SUV" }));

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClear)
                .addGap(18, 18, 18)
                .addComponent(cboFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(541, Short.MAX_VALUE))
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnClear)
                    .addComponent(cboFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel1.add(searchPanel, BorderLayout.NORTH);

        vehicleDisplayPanel.setBackground(new java.awt.Color(110, 50, 50));

        jScrollPane1.setViewportView(vehicleCardsContainer);

        javax.swing.GroupLayout vehicleDisplayPanelLayout = new javax.swing.GroupLayout(vehicleDisplayPanel);
        vehicleDisplayPanel.setLayout(vehicleDisplayPanelLayout);
        vehicleDisplayPanelLayout.setHorizontalGroup(
            vehicleDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
        );
        vehicleDisplayPanelLayout.setVerticalGroup(
            vehicleDisplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );

        jPanel1.add(vehicleDisplayPanel, BorderLayout.CENTER);

        actionPanel.setBackground(new java.awt.Color(80, 50, 50));

        btnRentVehicle.setText("RENT VEHICLE");
        btnRentVehicle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRentVehicleActionPerformed(evt);
            }
        });

        btnViewDetails.setText("VIEW DETAILS");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        lblSelectedVehicle.setForeground(new java.awt.Color(255, 255, 255));

        edit_client.setText("EDIT");

        javax.swing.GroupLayout actionPanelLayout = new javax.swing.GroupLayout(actionPanel);
        actionPanel.setLayout(actionPanelLayout);
        actionPanelLayout.setHorizontalGroup(
            actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPanelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(actionPanelLayout.createSequentialGroup()
                        .addComponent(btnRentVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(edit_client, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 262, Short.MAX_VALUE)
                .addComponent(lblSelectedVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(145, 145, 145))
        );
        actionPanelLayout.setVerticalGroup(
            actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(actionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(edit_client, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRentVehicle, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(actionPanelLayout.createSequentialGroup()
                .addComponent(lblSelectedVehicle, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );

        jPanel1.add(actionPanel, BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRentVehicleActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate vehicle status before proceeding
        try {
            Connection con = new dbConnector().getConnection();
            String checkQuery = "SELECT v_status FROM tbl_vehicles WHERE v_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedVehicleId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next() || !rs.getString("v_status").equalsIgnoreCase("available")) {
                JOptionPane.showMessageDialog(this, "This vehicle is no longer available for rent.", 
                    "Vehicle Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            rs.close();
            checkStmt.close();
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking vehicle status: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Continue with rental dialog...
        // ... rest of the existing rental dialog code ...
    }

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection con = new dbConnector().getConnection();
            String query = "SELECT * FROM tbl_vehicles WHERE v_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, selectedVehicleId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("VEHICLE DETAILS\n\n");
                details.append("ID: ").append(rs.getInt("v_id")).append("\n");
                details.append("Make: ").append(rs.getString("v_make")).append("\n");
                details.append("Model: ").append(rs.getString("v_model")).append("\n");
                details.append("Year: ").append(rs.getString("v_year")).append("\n");
                details.append("Plate Number: ").append(rs.getString("v_plate")).append("\n");
                details.append("Weekly Rate: ₱").append(rs.getString("v_rate")).append("\n");
                details.append("Status: ").append(rs.getString("v_status")).append("\n");
                String status = rs.getString("v_status");
                if (status.equalsIgnoreCase("rented")) {
                    // Query rental and client info for dialog display
                    try {
                        Connection con2 = new dbConnector().getConnection();
                        String rentalQuery = "SELECT r.r_start_date, r.r_end_date, r.r_total_amount, c.c_name, c.c_phone, c.c_email, c.c_address, c.c_image FROM tbl_rentals r JOIN tbl_clients c ON r.r_client_id = c.c_id WHERE r.r_vehicle_id = ? AND r.r_status = 'active'";
                        PreparedStatement pst2 = con2.prepareStatement(rentalQuery);
                        pst2.setInt(1, selectedVehicleId);
                        ResultSet rs2 = pst2.executeQuery();
                        if (rs2.next()) {
                            showAgreementDialog(
                                details.toString(),
                                rs2.getString("c_name"),
                                rs2.getString("c_phone"),
                                rs2.getString("c_email"),
                                rs2.getString("c_address"),
                                rs2.getString("r_start_date"),
                                rs2.getString("r_end_date"),
                                String.format("%.2f", rs2.getDouble("r_total_amount")),
                                rs.getString("v_make") + " " + rs.getString("v_model"),
                                rs.getString("v_year"),
                                rs.getString("v_plate"),
                                rs2.getBytes("c_image")
                            );
                        } else {
                            JOptionPane.showMessageDialog(this, "No active rental found for this vehicle.");
                        }
                        rs2.close();
                        pst2.close();
                        con2.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error loading rental/client info: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Show normal details dialog
                    JOptionPane.showMessageDialog(this, details.toString(), "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving vehicle details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void edit_clientActionPerformed(java.awt.event.ActionEvent evt) {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
}
        // Add your edit functionality here
        JOptionPane.showMessageDialog(this, "Edit functionality for vehicle ID: " + selectedVehicleId, "Edit Vehicle", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean processRentalWithCustomDates(
        int vehicleId,
        String name,
        String phone,
        String email,
        String idType,
        String idNumber,
        String address,
        String startDate,
        String endDate,
        byte[] imageBytes,
        int weeks
    ) {
        Connection con = null;
        PreparedStatement clientStmt = null;
        PreparedStatement rentalStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement rateStmt = null;
        ResultSet rs = null;
        
        try {
            con = new dbConnector().getConnection();
            con.setAutoCommit(false);
            
            // Check if vehicle is still available
            String checkQuery = "SELECT v_status FROM tbl_vehicles WHERE v_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setInt(1, vehicleId);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (!checkRs.next() || !checkRs.getString("v_status").equalsIgnoreCase("available")) {
                JOptionPane.showMessageDialog(this, "Vehicle is no longer available for rent.", 
                    "Vehicle Unavailable", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            checkRs.close();
            checkStmt.close();
            
            // Insert or update client
            String clientQuery = "INSERT INTO tbl_clients (c_name, c_phone, c_email, id_type, id_number, c_address, c_image) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE c_phone=?, c_email=?, id_type=?, id_number=?, c_address=?, c_image=?";
            clientStmt = con.prepareStatement(clientQuery, Statement.RETURN_GENERATED_KEYS);
            
            // Set parameters for INSERT
            clientStmt.setString(1, name);
            clientStmt.setString(2, phone);
            clientStmt.setString(3, email);
            clientStmt.setString(4, idType);
            clientStmt.setString(5, idNumber);
            clientStmt.setString(6, address);
            clientStmt.setBytes(7, imageBytes);
            
            // Set parameters for UPDATE
            clientStmt.setString(8, phone);
            clientStmt.setString(9, email);
            clientStmt.setString(10, idType);
            clientStmt.setString(11, idNumber);
            clientStmt.setString(12, address);
            clientStmt.setBytes(13, imageBytes);

            int clientResult = clientStmt.executeUpdate();
            
            // Get client ID
            int clientId;
            rs = clientStmt.getGeneratedKeys();
            if (rs.next()) {
                clientId = rs.getInt(1);
            } else {
                String getClientQuery = "SELECT c_id FROM tbl_clients WHERE c_name = ? AND c_phone = ?";
                PreparedStatement getClientStmt = con.prepareStatement(getClientQuery);
                getClientStmt.setString(1, name);
                getClientStmt.setString(2, phone);
                ResultSet clientRs = getClientStmt.executeQuery();
                if (clientRs.next()) {
                    clientId = clientRs.getInt("c_id");
                    clientRs.close();
                    getClientStmt.close();
                } else {
                    throw new SQLException("Could not get client ID");
                }
            }
            
            // Get vehicle rate
            rateStmt = con.prepareStatement("SELECT v_rate FROM tbl_vehicles WHERE v_id = ?");
            rateStmt.setInt(1, vehicleId);
            ResultSet rateRs = rateStmt.executeQuery();
            
            double rate = 0;
            if (rateRs.next()) {
                rate = rateRs.getDouble("v_rate");
            }
            rateRs.close();
            
            // Calculate total amount
            double totalAmount = rate * weeks;
            
            // Create rental record
            rentalStmt = con.prepareStatement(
                "INSERT INTO tbl_rentals (r_vehicle_id, r_client_id, r_start_date, r_end_date, " +
                "r_total_amount, r_status, r_created_by) VALUES (?, ?, ?, ?, ?, 'active', ?)"
            );
            rentalStmt.setInt(1, vehicleId);
            rentalStmt.setInt(2, clientId);
            rentalStmt.setString(3, startDate);
            rentalStmt.setString(4, endDate);
            rentalStmt.setDouble(5, totalAmount);
            rentalStmt.setString(6, System.getProperty("user.name"));
            
            int rentalResult = rentalStmt.executeUpdate();
            
            // Update vehicle status
            updateStmt = con.prepareStatement("UPDATE tbl_vehicles SET v_status = 'rented' WHERE v_id = ?");
            updateStmt.setInt(1, vehicleId);
            updateStmt.executeUpdate();
            
            // Log actions
            try {
                String userIp = InetAddress.getLocalHost().getHostAddress();
                String username = System.getProperty("user.name");
                
                if (clientResult > 0) {
                    Logger.log("Add/Update Client", name + " (" + phone + ", " + email + ") added/updated.", username, userIp);
                }
                
                if (rentalResult > 0) {
                    Logger.log("Add Rental", "Vehicle ID: " + vehicleId + ", Client: " + name + 
                        ", Dates: " + startDate + " to " + endDate, username, userIp);
                }
            } catch (Exception e) {
                // Log error but don't fail the transaction
                System.err.println("Error logging actions: " + e.getMessage());
            }
            
            con.commit();
            JOptionPane.showMessageDialog(this, 
                String.format("Vehicle rented successfully for %d weeks!\nTotal Amount: ₱%.2f", weeks, totalAmount),
                "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
            
        } catch (SQLException ex) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            
            String errorMessage = "Error processing rental: " + ex.getMessage();
            if (ex.getMessage().contains("Duplicate entry")) {
                errorMessage = "A client with this information already exists. Please check the details.";
            }
            JOptionPane.showMessageDialog(this, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
            
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (clientStmt != null) clientStmt.close();
                if (rentalStmt != null) rentalStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (rateStmt != null) rateStmt.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private void showAgreementDialog(String vehicleDetails, String clientName, String clientPhone, String clientEmail, String clientAddress, String startDate, String endDate, String totalAmount, String vehicle, String year, String plate, byte[] clientImage) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Rental Agreement Preview", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Preview panel (styled like the print panel)
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(Color.WHITE);
        previewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("CAR RENTAL AGREEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        previewPanel.add(title);
        previewPanel.add(Box.createVerticalStrut(10));

        // Client image
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        if (clientImage != null && clientImage.length > 0) {
            ImageIcon icon = new ImageIcon(clientImage);
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setText("No Image");
        }
        previewPanel.add(imageLabel);
        previewPanel.add(Box.createVerticalStrut(10));

        previewPanel.add(new JLabel("Renter: " + clientName));
        previewPanel.add(new JLabel("Phone: " + clientPhone));
        previewPanel.add(new JLabel("Email: " + clientEmail));
        previewPanel.add(new JLabel("Address: " + clientAddress));
        previewPanel.add(Box.createVerticalStrut(10));
        previewPanel.add(new JLabel("Start Date: " + startDate));
        previewPanel.add(new JLabel("End Date: " + endDate));
        previewPanel.add(new JLabel("Total Amount: ₱" + totalAmount));
        previewPanel.add(Box.createVerticalStrut(10));
        previewPanel.add(new JLabel("Vehicle: " + vehicle));
        previewPanel.add(new JLabel("Year: " + year));
        previewPanel.add(new JLabel("Plate: " + plate));
        previewPanel.add(Box.createVerticalStrut(20));
        previewPanel.add(Box.createVerticalStrut(30));
        JLabel signatureLabel = new JLabel("Client Signature: __________________________");
        signatureLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        signatureLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        previewPanel.add(signatureLabel);
        previewPanel.add(Box.createVerticalStrut(10));

        JScrollPane scrollPane = new JScrollPane(previewPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton savePdfBtn = new JButton("Save as PDF");
        JButton printBtn = new JButton("Print");
        JButton closeBtn = new JButton("Close");
        buttonPanel.add(savePdfBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Save as PDF action
        savePdfBtn.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save as PDF");
            fileChooser.setSelectedFile(new File("rental_agreement.pdf"));
            int userSelection = fileChooser.showSaveDialog(dialog);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (PDDocument doc = new PDDocument()) {
                    PDPage page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    PDPageContentStream content = new PDPageContentStream(doc, page);
                    float y = page.getMediaBox().getHeight() - 50;
                    content.setFont(PDType1Font.TIMES_BOLD, 18);
                    content.beginText();
                    content.newLineAtOffset(200, y);
                    content.showText("CAR RENTAL AGREEMENT");
                    content.endText();
                    y -= 40;
                    if (clientImage != null && clientImage.length > 0) {
                        BufferedImage bimg = ImageIO.read(new java.io.ByteArrayInputStream(clientImage));
                        PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, clientImage, "client");
                        content.drawImage(pdImage, 50, y - 100, 100, 100);
                    }
                    y -= 20;
                    content.setFont(PDType1Font.TIMES_ROMAN, 12);
                    String[] lines = {
                        "Renter: " + clientName,
                        "Phone: " + clientPhone,
                        "Email: " + clientEmail,
                        "Address: " + clientAddress,
                        "Start Date: " + startDate,
                        "End Date: " + endDate,
                        "Total Amount: ₱" + totalAmount,
                        "Vehicle: " + vehicle,
                        "Year: " + year,
                        "Plate: " + plate,
                        "",
                        "Client Signature: __________________________"
                    };
                    for (String line : lines) {
                        y -= 20;
                        content.beginText();
                        content.newLineAtOffset(50, y);
                        content.showText(line);
                        content.endText();
                        }
                    content.close();
                    doc.save(fileToSave);
                    JOptionPane.showMessageDialog(dialog, "PDF saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Print action
        printBtn.addActionListener(ev -> {
            try {
                previewPanel.print(null);
                JOptionPane.showMessageDialog(dialog, "Receipt sent to printer.", "Print", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error printing: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

        closeBtn.addActionListener(ev -> dialog.dispose());
                    dialog.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionPanel;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnRentVehicle;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cboFilter;
    private javax.swing.JButton edit_client;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSelectedVehicle;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JPanel vehicleCardsContainer;
    private javax.swing.JPanel vehicleDisplayPanel;
    // End of variables declaration//GEN-END:variables
}

