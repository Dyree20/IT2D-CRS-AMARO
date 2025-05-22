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
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author ROCO
 */
public class add_vehicles extends javax.swing.JInternalFrame {

    
    private JPanel selectedCard = null;
    private File selectedFile;
    private String destination;
    private String path;
    private JLabel image;
    private JButton browse;
    private JButton browse1;
    /**
     * Creates new form add_vehicles
     */
    public add_vehicles() {
    initComponents();
    
    this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));   
    BasicInternalFrameUI bi = (BasicInternalFrameUI)this.getUI();
    bi.setNorthPane(null);
    
    
    delete.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteActionPerformed(evt);
    }
});
    
    // Configure the vehicle list panel
    vehicleListPanel.setLayout(new BoxLayout(vehicleListPanel, BoxLayout.Y_AXIS));
    
    // Add some space between cards
    vehicleListPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Add ActionListener to mv_type to filter vehicles
    mv_type.addActionListener(e -> loadVehiclesFromDB());
    
    // Add ActionListener to btnSearch to filter vehicles
    btnSearch.addActionListener(e -> loadVehiclesFromDB());
    
    // Style buttons
    styleButton(add);
    styleButton(edit);
    styleButton(delete);
    
    // Load vehicles from database
    loadVehiclesFromDB();
}
    private void styleButton(JButton button) {
        button.setBackground(new java.awt.Color(153, 0, 0));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    public void addVehicleCard(String name, String type, String price, String status, Object imageSource) {
        JPanel card = new JPanel();
        card.setBackground(new java.awt.Color(153, 0, 0));
        card.setMaximumSize(new java.awt.Dimension(880, 180));
        card.setPreferredSize(new java.awt.Dimension(880, 180));
        card.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
        card.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT));

        // Load and scale image
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 100));
        
        try {
            ImageIcon icon = null;
            if (imageSource instanceof byte[]) {
                icon = new ImageIcon((byte[]) imageSource);
            } else if (imageSource instanceof String) {
                icon = new ImageIcon((String) imageSource);
            }
            
            if (icon != null) {
                Image img = icon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No Image");
                imageLabel.setHorizontalAlignment(JLabel.CENTER);
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        } catch (Exception e) {
            imageLabel.setText("No Image");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        
        card.add(imageLabel);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new java.awt.Color(153, 0, 0));
        
        JLabel nameLabel = new JLabel("Name: " + name);
        JLabel typeLabel = new JLabel("Type: " + type);
        JLabel priceLabel = new JLabel("Price: " + price);
        JLabel statusLabel = new JLabel("Status: " + status);
        if (status != null) {
            if (status.equalsIgnoreCase("available")) {
                statusLabel.setForeground(new Color(0, 153, 0)); // Green
            } else if (status.equalsIgnoreCase("rented")) {
                statusLabel.setForeground(Color.RED);
            } else if (status.equalsIgnoreCase("maintenance")) {
                statusLabel.setForeground(new Color(255, 140, 0)); // Orange
            } else {
                statusLabel.setForeground(Color.GRAY);
            }
        } else {
            statusLabel.setForeground(Color.GRAY);
        }
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(statusLabel);
        
        JLabel[] labels = {nameLabel, typeLabel, priceLabel};
        for (JLabel label : labels) {
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            infoPanel.add(label);
        }
        
        card.add(infoPanel);

        // Mouse click listener to select card
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (selectedCard != null) {
                    selectedCard.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK));
                }
                selectedCard = card;
                card.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.YELLOW, 3));
            }
        });

        vehicleListPanel.add(card);
        vehicleListPanel.revalidate();
        vehicleListPanel.repaint();
    }

    private boolean addVehicleToDB(String make, String model, String year, String plate, String rate, String status, String vType, File imageFile) {
    try {
        Connection conn = new dbConnector().getConnection();
        
        // Prepare the statement with all fields
        String query = "INSERT INTO tbl_vehicles (v_make, v_model, v_year, v_plate, v_rate, v_status, v_type, v_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, make);
        pst.setString(2, model);
        pst.setString(3, year);
        pst.setString(4, plate);
        pst.setString(5, rate);
        pst.setString(6, status);
        pst.setString(7, vType);
        
        // Handle image file - resize it first
        if (imageFile != null && imageFile.exists()) {
            try {
                // Load the original image
                BufferedImage originalImage = ImageIO.read(imageFile);
                
                // Resize the image to a smaller size (e.g., 300x200)
                int maxWidth = 300;
                int maxHeight = 200;
                
                // Calculate new dimensions while maintaining aspect ratio
                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();
                
                double widthRatio = (double) maxWidth / originalWidth;
                double heightRatio = (double) maxHeight / originalHeight;
                double ratio = Math.min(widthRatio, heightRatio);
                
                int newWidth = (int) (originalWidth * ratio);
                int newHeight = (int) (originalHeight * ratio);
                
                // Create resized image
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g = resizedImage.createGraphics();
                g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g.dispose();
                
                // Convert to byte array with high compression
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                // Use JPEG format with high compression (lower quality)
                float quality = 0.5f; // 50% quality
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
                
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                writer.setOutput(ios);
                writer.write(null, new IIOImage(resizedImage, null, null), param);
                writer.dispose();
                ios.close();
                
                byte[] imageBytes = baos.toByteArray();
                baos.close();
                
                // Print the size of the compressed image
                System.out.println("Original image size: " + imageFile.length() + " bytes");
                System.out.println("Compressed image size: " + imageBytes.length + " bytes");
                
                // Set the compressed image bytes
                pst.setBytes(8, imageBytes);
            } catch (Exception e) {
                System.out.println("Error processing image: " + e.getMessage());
                e.printStackTrace();
                pst.setNull(8, java.sql.Types.BLOB);
            }
        } else {
            pst.setNull(8, java.sql.Types.BLOB);
        }
        
        int result = pst.executeUpdate();
        pst.close();
        conn.close();
        
        if (result > 0) {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name"); // Replace with actual username if available
            Logger.log("Add Vehicle", make + " " + model + " (" + year + ", " + plate + ") added.", username, userIp);
        }
        return result > 0;
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
        return false;
    }
}
    private void loadVehiclesFromDB() {
    try {
        // Clear the current list
        vehicleListPanel.removeAll();
        Connection conn = new dbConnector().getConnection();
        String selectedType = mv_type.getSelectedItem() != null ? mv_type.getSelectedItem().toString() : "All";
        String searchText = txtSearch.getText().trim();
        StringBuilder query = new StringBuilder("SELECT * FROM tbl_vehicles");
        boolean hasType = !selectedType.equals("All");
        boolean hasSearch = !searchText.isEmpty();
        if (hasType || hasSearch) {
            query.append(" WHERE ");
            if (hasType) {
                query.append("v_type = ?");
            }
            if (hasSearch) {
                if (hasType) query.append(" AND ");
                query.append("(LOWER(v_make) LIKE ? OR LOWER(v_model) LIKE ?)");
            }
        }
        PreparedStatement pst = conn.prepareStatement(query.toString());
        int paramIndex = 1;
        if (hasType) {
            pst.setString(paramIndex++, selectedType);
        }
        if (hasSearch) {
            String likeText = "%" + searchText.toLowerCase() + "%";
            pst.setString(paramIndex++, likeText);
            pst.setString(paramIndex++, likeText);
        }
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            String make = rs.getString("v_make");
            String model = rs.getString("v_model");
            String year = rs.getString("v_year");
            String plate = rs.getString("v_plate");
            String rate = rs.getString("v_rate");
            String vType = rs.getString("v_type");
            String status = rs.getString("v_status");
            String cardName = make + " " + model;
            String cardType = vType + " | Year: " + year + ", Plate: " + plate;
            String cardPrice = "₱" + rate + " per day";
            byte[] imageData = rs.getBytes("v_image");
            addVehicleCard(cardName, cardType, cardPrice, status, imageData);
        }
        rs.close();
        pst.close();
        conn.close();
        vehicleListPanel.revalidate();
        vehicleListPanel.repaint();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error loading vehicles: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
   private boolean deleteVehicleFromDB(String make, String model) {
        try {
            dbConnector db = new dbConnector();
            Connection conn = db.getConnection();
            
            // Create SQL statement to delete vehicle with matching make and model
            String query = "DELETE FROM tbl_vehicles WHERE v_make = ? AND v_model = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, make);
            pst.setString(2, model);
            
            // Execute the delete
            int rowsDeleted = pst.executeUpdate();
            
            // Close resources
            pst.close();
            conn.close();
            
            if (rowsDeleted > 0) {
                String userIp = "Unknown";
                try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
                String username = System.getProperty("user.name"); // Replace with actual username if available
                Logger.log("Delete Vehicle", make + " " + model + " deleted.", username, userIp);
            }
            // Return true if at least one row was deleted
            return rowsDeleted > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
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
        jPanel2 = new javax.swing.JPanel();
        add = new javax.swing.JButton();
        edit = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField(12);
        mv_type = new javax.swing.JComboBox<>(new String[] { "AUV", "SUV", "VAN" });
        jScrollPane2 = new javax.swing.JScrollPane();
        vehicleListPanel = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(102, 0, 0));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(204, 51, 0));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 18, 30));

        add.setBackground(new java.awt.Color(153, 153, 153));
        add.setFont(new java.awt.Font("Malgun Gothic", 1, 12));
        add.setText("ADD");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        edit.setBackground(new java.awt.Color(204, 204, 204));
        edit.setFont(new java.awt.Font("Malgun Gothic", 1, 12));
        edit.setText("EDIT");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        delete.setFont(new java.awt.Font("Malgun Gothic", 1, 12));
        delete.setText("DELETE");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        // Add controls to jPanel2
        jPanel2.add(add);
        jPanel2.add(edit);
        jPanel2.add(delete);
        jPanel2.add(txtSearch);
        jPanel2.add(btnSearch);
        jPanel2.add(mv_type);

        jScrollPane2.setBackground(new java.awt.Color(102, 0, 0));
        jScrollPane2.setVerifyInputWhenFocusTarget(false);
        vehicleListPanel.setBackground(new java.awt.Color(102, 0, 0));
        vehicleListPanel.setPreferredSize(new java.awt.Dimension(900, 2000));
        vehicleListPanel.setLayout(new javax.swing.BoxLayout(vehicleListPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(vehicleListPanel);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);
        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // Create a dialog for vehicle information input
    JDialog dialog = new JDialog();
    dialog.setTitle("Add New Vehicle");
    dialog.setSize(400, 420);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());
    
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(new JLabel("Make:"), gbc);
    gbc.gridx = 1;
    JTextField makeField = new JTextField(20);
    panel.add(makeField, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Model:"), gbc);
    gbc.gridx = 1;
    JTextField modelField = new JTextField(20);
    panel.add(modelField, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Year:"), gbc);
    gbc.gridx = 1;
    JTextField yearField = new JTextField(20);
    panel.add(yearField, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Plate Number:"), gbc);
    gbc.gridx = 1;
    JTextField plateField = new JTextField(20);
    panel.add(plateField, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Rate per Day (PHP):"), gbc);
    gbc.gridx = 1;
    JTextField rateField = new JTextField(20);
    panel.add(rateField, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Status:"), gbc);
    JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Rented", "Maintenance"});
    panel.add(statusCombo, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("MV Type:"), gbc);
    gbc.gridx = 1;
    JComboBox<String> mvTypeCombo = new JComboBox<>(new String[]{"AUV", "SUV", "VAN"});
    panel.add(mvTypeCombo, gbc);
    gbc.gridx = 0; gbc.gridy++;
    panel.add(new JLabel("Vehicle Image:"), gbc);
    gbc.gridx = 1;
    JTextField imageField = new JTextField(22);
    JButton browseButton = new JButton("Browse...");
    JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
    imagePanel.add(imageField, BorderLayout.CENTER);
    imagePanel.add(browseButton, BorderLayout.EAST);
    panel.add(imagePanel, gbc);
    
    // File reference for the selected image
    final File[] selectedImageFile = {null};
    
    // Add a file chooser for the image
    browseButton.addActionListener(e -> {
        handleImageUpload();
    });
    
    dialog.add(panel, BorderLayout.CENTER);
    
    // Add save button
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    
    saveButton.addActionListener(e -> {
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        String year = yearField.getText().trim();
        String plate = plateField.getText().trim();
        String rate = rateField.getText().trim();
        String status = statusCombo.getSelectedItem().toString();
        String vType = mvTypeCombo.getSelectedItem().toString();
        
        // Validate inputs
        if (make.isEmpty() || model.isEmpty() || year.isEmpty() || plate.isEmpty() || rate.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "All fields except image are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate year as a number
        try {
            int yearVal = Integer.parseInt(year);
            if (yearVal < 1900 || yearVal > 2100) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid year.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Year must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate rate as a number
        try {
            Double.parseDouble(rate);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Rate must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add to database
        if (addVehicleToDB(make, model, year, plate, rate, status, vType, selectedImageFile[0])) {
            JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Create a description for the vehicle card
            String cardName = make + " " + model;
            String cardType = "Year: " + year + ", Plate: " + plate;
            String cardPrice = "₱" + rate + " per day";
            
            // Create a temporary file if we need to display from the database BLOB
            String imagePath = "";
            if (selectedImageFile[0] != null) {
                imagePath = selectedImageFile[0].getAbsolutePath();
            }
            
            // Add to UI
            addVehicleCard(cardName, cardType, cardPrice, status, imagePath);
            dialog.dispose();
            
            // Refresh vehicle list from database (optional)
            // loadVehiclesFromDB();
        }
    });
    
    cancelButton.addActionListener(e -> dialog.dispose());
    
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);

    }//GEN-LAST:event_addActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {                                     
        if (selectedCard == null) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get vehicle information from the selected card
        final String[] vehicleInfo = {"", "", "", "", "", "", ""}; // make, model, year, plate, rate, status, type
        
        // Find JLabels in the selected card to extract information
        Component[] components = selectedCard.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] subComponents = panel.getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        String text = label.getText();
                        if (text != null) {
                            if (text.startsWith("Name:")) {
                                String[] nameParts = text.substring(6).trim().split(" ", 2);
                                if (nameParts.length == 2) {
                                    vehicleInfo[0] = nameParts[0]; // make
                                    vehicleInfo[1] = nameParts[1]; // model
                                }
                            } else if (text.startsWith("Type:")) {
                                String typeInfo = text.substring(6).trim();
                                String[] parts = typeInfo.split(", ");
                                // The first part is the type
                                String[] typeParts = parts[0].split(" ");
                                vehicleInfo[6] = typeParts[0]; // type
                                for (String part : parts) {
                                    if (part.startsWith("Year:")) {
                                        vehicleInfo[2] = part.substring(6).trim();
                                    } else if (part.startsWith("Plate:")) {
                                        vehicleInfo[3] = part.substring(7).trim();
                                    }
                                }
                            } else if (text.startsWith("Price:")) {
                                String price = text.substring(7).trim();
                                // Remove "₱" and " per day" to get just the number
                                price = price.replace("₱", "").replace(" per day", "");
                                vehicleInfo[4] = price;
                            }
                        }
                    }
                }
            }
        }
        
        // Create a dialog for editing vehicle information
        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Vehicle");
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add components to the panel
        JLabel makeLabel = new JLabel("Make:");
        JTextField makeField = new JTextField(vehicleInfo[0], 20);
        
        JLabel modelLabel = new JLabel("Model:");
        JTextField modelField = new JTextField(vehicleInfo[1], 20);
        
        JLabel yearLabel = new JLabel("Year:");
        JTextField yearField = new JTextField(vehicleInfo[2], 20);
        
        JLabel plateLabel = new JLabel("Plate Number:");
        JTextField plateField = new JTextField(vehicleInfo[3], 20);
        
        JLabel rateLabel = new JLabel("Rate per Day (PHP):");
        JTextField rateField = new JTextField(vehicleInfo[4], 20);
        
        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Rented", "Maintenance"});
        statusCombo.setSelectedItem(vehicleInfo[5]);
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"AUV", "SUV", "VAN"});
        typeCombo.setSelectedItem(vehicleInfo[6]);
        
        JLabel imageLabel = new JLabel("Vehicle Image:");
        JTextField imageField = new JTextField(20);
        JButton browseButton = new JButton("Browse...");
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);
        
        // File reference for the selected image
        final File[] selectedImageFile = {null};
        
        // Add a file chooser for the image
        browseButton.addActionListener(e -> {
            handleImageUpload();
        });
        
        panel.add(makeLabel);
        panel.add(makeField);
        panel.add(modelLabel);
        panel.add(modelField);
        panel.add(yearLabel);
        panel.add(yearField);
        panel.add(plateLabel);
        panel.add(plateField);
        panel.add(rateLabel);
        panel.add(rateField);
        panel.add(statusLabel);
        panel.add(statusCombo);
        panel.add(typeLabel);
        panel.add(typeCombo);
        panel.add(imageLabel);
        panel.add(imagePanel);
        
        // Add save button
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        saveButton.addActionListener(e -> {
            String newMake = makeField.getText().trim();
            String newModel = modelField.getText().trim();
            String newYear = yearField.getText().trim();
            String newPlate = plateField.getText().trim();
            String newRate = rateField.getText().trim();
            String newStatus = statusCombo.getSelectedItem().toString();
            String newType = typeCombo.getSelectedItem().toString();
            
            // Validate inputs
            if (newMake.isEmpty() || newModel.isEmpty() || newYear.isEmpty() || newPlate.isEmpty() || newRate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields except image are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate year as a number
            try {
                int yearVal = Integer.parseInt(newYear);
                if (yearVal < 1900 || yearVal > 2100) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid year.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Year must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate rate as a number
            try {
                Double.parseDouble(newRate);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Rate must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update in database
            if (updateVehicleInDB(vehicleInfo[0], vehicleInfo[1], newMake, newModel, newYear, newPlate, newRate, newStatus, newType, selectedImageFile[0])) {
                // Remove old card and add new one
                vehicleListPanel.remove(selectedCard);
                selectedCard = null;
                loadVehiclesFromDB();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean updateVehicleInDB(String oldMake, String oldModel, String newMake, String newModel, 
                                    String year, String plate, String rate, String status, String vType, File imageFile) {
        try {
            Connection conn = new dbConnector().getConnection();
            
            // Prepare the statement with all fields
            String query = "UPDATE tbl_vehicles SET v_make=?, v_model=?, v_year=?, v_plate=?, v_rate=?, v_status=?, v_type=?, v_image=? WHERE v_make=? AND v_model=?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, newMake);
            pst.setString(2, newModel);
            pst.setString(3, year);
            pst.setString(4, plate);
            pst.setString(5, rate);
            pst.setString(6, status);
            pst.setString(7, vType);
            // Handle image file if provided
            if (imageFile != null) {
                try {
                    InputStream is = new FileInputStream(imageFile);
                    pst.setBlob(8, is);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error with image file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                pst.setNull(8, java.sql.Types.BLOB);
            }
            pst.setString(9, oldMake);
            pst.setString(10, oldModel);
            
            int result = pst.executeUpdate();
            pst.close();
            conn.close();
            
            if (result > 0) {
                String userIp = "Unknown";
                try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
                String username = System.getProperty("user.name"); // Replace with actual username if available
                Logger.log("Update Vehicle", oldMake + " " + oldModel + " updated to " + newMake + " " + newModel + ", " + year + ", " + plate, username, userIp);
            }
            return result > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
       if (selectedCard == null) {
        JOptionPane.showMessageDialog(this, "Please select a vehicle to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Get vehicle name for confirmation message
    String vehicleName = "this vehicle";
    Component[] components = selectedCard.getComponents();
    for (Component component : components) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            Component[] subComponents = panel.getComponents();
            for (Component subComp : subComponents) {
                if (subComp instanceof JLabel) {
                    JLabel label = (JLabel) subComp;
                    String text = label.getText();
                    if (text != null && text.startsWith("Name:")) {
                        vehicleName = text.substring(6).trim();
                        break;
                    }
                }
            }
        }
    }
    
    // Confirm deletion
    int confirm = JOptionPane.showConfirmDialog(
        this, 
        "Are you sure you want to delete " + vehicleName + "?", 
        "Confirm Deletion", 
        JOptionPane.YES_NO_OPTION
    );
    
    if (confirm == JOptionPane.YES_OPTION) {
        // Extract make and model from the vehicle name
        // Assuming the name format is "Make Model"
        String[] parts = vehicleName.split(" ", 2);
        if (parts.length < 2) {
            JOptionPane.showMessageDialog(this, "Unable to identify vehicle information.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String make = parts[0];
        String model = parts[1];
        
        // Delete from database
        if (deleteVehicleFromDB(make, model)) {
            // Remove from UI
            vehicleListPanel.remove(selectedCard);
            vehicleListPanel.revalidate();
            vehicleListPanel.repaint();
            selectedCard = null;
            
            JOptionPane.showMessageDialog(this, "Vehicle deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete vehicle from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    }//GEN-LAST:event_deleteActionPerformed

    private void handleImageUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                selectedFile = fileChooser.getSelectedFile();
                
                // Validate file size (max 5MB)
                if (selectedFile.length() > 5 * 1024 * 1024) {
                    JOptionPane.showMessageDialog(this, 
                        "File size exceeds 5MB limit. Please choose a smaller image.",
                        "File Too Large", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Generate unique filename
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String newFileName = "vehicle_" + timestamp + extension;
                
                destination = "src/images/" + newFileName;
                path = selectedFile.getAbsolutePath();
                
                if (FileExistenceChecker(destination) == 1) {
                    JOptionPane.showMessageDialog(this, 
                        "File Already Exists. Please try again.",
                        "File Exists", 
                        JOptionPane.WARNING_MESSAGE);
                    destination = "";
                    path = "";
                } else {
                    // Resize and display image
                    ImageIcon resizedImage = ResizeImage(path, null, image);
                    if (resizedImage != null) {
                        image.setIcon(resizedImage);
                        browse.setVisible(true);
                        browse.setText("REMOVE");
                        browse1.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to process image. Please try another file.",
                            "Image Processing Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error processing image: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public int FileExistenceChecker(String path) {
        try {
            File file = new File(path);
            return file.exists() ? 1 : 0;
        } catch (Exception e) {
            System.out.println("Error checking file existence: " + e.getMessage());
            return 0;
        }
    }

    public static int getHeightFromWidth(String imagePath, int desiredWidth) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            if (image == null) {
                throw new IOException("Failed to read image");
            }
            
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            
            // Calculate new height maintaining aspect ratio
            return (int) ((double) desiredWidth / originalWidth * originalHeight);
        } catch (IOException ex) {
            System.out.println("Image processing error: " + ex.getMessage());
            return -1;
        }
    }

    public ImageIcon ResizeImage(String ImagePath, byte[] pic, JLabel label) {
        try {
            ImageIcon MyImage = null;
            if (ImagePath != null) {
                MyImage = new ImageIcon(ImagePath);
            } else if (pic != null) {
                MyImage = new ImageIcon(pic);
            } else {
                return null;
            }
            
            int newHeight = getHeightFromWidth(ImagePath, label.getWidth());
            if (newHeight <= 0) {
                return null;
            }
            
            Image img = MyImage.getImage();
            Image newImg = img.getScaledInstance(label.getWidth(), newHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        } catch (Exception e) {
            System.out.println("Error resizing image: " + e.getMessage());
            return null;
        }
    }

    public void imageUpdater(String existingFilePath, String newFilePath) {
        try {
            File existingFile = new File(existingFilePath);
            File newFile = new File(newFilePath);
            
            if (!newFile.exists()) {
                throw new IOException("New file does not exist");
            }
            
            if (existingFile.exists()) {
                // Delete old file and copy new one
                if (!existingFile.delete()) {
                    throw new IOException("Failed to delete existing file");
                }
            }
            
            // Copy new file to destination
            Files.copy(newFile.toPath(), 
                      new File(existingFilePath).toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Image updated successfully");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating image: " + e.getMessage(),
                "Update Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton delete;
    private javax.swing.JButton edit;
    private javax.swing.JButton btnSearch;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JComboBox<String> mv_type;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel vehicleListPanel;
    // End of variables declaration//GEN-END:variables
}
