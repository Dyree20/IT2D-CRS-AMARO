/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import javax.swing.JLabel;
import javax.swing.JPanel;
import vrs.Approval;
import vrs.add_vehicles;
import vrs.home;
import vrs.loginForm;
import vrs.add_users;
import vrs.passwordResetRequests;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyVetoException;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import vrs.user_logs;
import config.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JInternalFrame;
import vrs.v_clients;

/**
 *
 * @author ROCO
 */
public class adminDashboard extends javax.swing.JFrame {

    
    private String currentUsername;
    
    private JPanel logoPanel;
    

    
    
     /* Creates new form adminDashboard
     */
    public adminDashboard() {
        initComponents();
        mhome.setOpaque(false);
        mhome.setBackground(new Color(0,0,0,0));
       
        
        a_users.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            a_usersActionPerformed(evt);
        }
    });
        
        a_clients.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a_clientsActionPerformed(evt);
            }
        });
    }
    
    private void resetButtonColors() {
        javax.swing.JButton[] buttons = {
            dashboard, vehicles_dash, p_approval, 
            a_users, logs
        };
        for (javax.swing.JButton button : buttons) {
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBackground(null);      // Default NetBeans background
            button.setForeground(null);      // Default text color
            button.setBorder(javax.swing.UIManager.getBorder("Button.border")); // Default border
        }
    }
    
private void a_usersActionPerformed(java.awt.event.ActionEvent evt) {
    
    String userIp = "Unknown";
    try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
    Logger.log("Button Click", "Admin clicked: Users", currentUsername, userIp);
    resetButtonColors();
    a_users.setBackground(new Color(110, 0, 0));
    a_users.setForeground(Color.WHITE);
    mhome.removeAll();
    add_users usersPanel = new add_users();
    mhome.add(usersPanel);
    usersPanel.setVisible(true);
    mhome.revalidate();
    mhome.repaint();
}

private void showInternalFrame(javax.swing.JInternalFrame frame) {
    try {
        // Close any existing internal frames
        javax.swing.JInternalFrame[] frames = mhome.getAllFrames();
        for (javax.swing.JInternalFrame f : frames) {
            f.setClosed(true);
            f.dispose();
        }
        
        // Clear desktop pane
        mhome.removeAll();
        
        // Set frame properties
        frame.setVisible(true);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.setClosable(true);
        
        // Set frame size
        Dimension desktopSize = mhome.getSize();
        frame.setSize(desktopSize.width, desktopSize.height);
        
        // Add frame to desktop
        mhome.add(frame);
        
        // Set focus
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ex) {
            System.err.println("Error setting focus to frame: " + ex.getMessage());
        }
        
        // Update UI
        mhome.repaint();
        mhome.revalidate();
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error showing frame: " + e.getMessage());
    }
}

public void setUsername(String username) {
    this.currentUsername = username;
    
    // Format the welcome message to fit
    if (username.length() > 6) {
        // If username is too long, use multiple lines
        
        // Use a panel with BoxLayout for multiple lines
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Make it transparent
        
        // First line is "WELCOME"
        JLabel welcomeLabel = new JLabel("WELCOME");
        welcomeLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        welcomeLabel.setForeground(java.awt.Color.WHITE);
        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        // Second line is "Admin"
        JLabel adminLabel = new JLabel("Admin");
        adminLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        adminLabel.setForeground(java.awt.Color.WHITE);
        adminLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adminLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        // Third line is the username
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new java.awt.Font("Tahoma", 1, 12));
        usernameLabel.setForeground(java.awt.Color.WHITE);
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        textPanel.add(welcomeLabel);
        textPanel.add(adminLabel);
        textPanel.add(usernameLabel);
        
        // Clear and set panel
        welcomePanel.removeAll();
        welcomePanel.setLayout(new java.awt.BorderLayout());
        welcomePanel.add(textPanel, java.awt.BorderLayout.CENTER);
    } else {
        // If username is short enough, use two lines
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Make it transparent
        
        // First line
        JLabel welcomeLabel = new JLabel("WELCOME Admin");
        welcomeLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        welcomeLabel.setForeground(java.awt.Color.WHITE);
        welcomeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        // Second line
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        usernameLabel.setForeground(java.awt.Color.WHITE);
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usernameLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        textPanel.add(welcomeLabel);
        textPanel.add(usernameLabel);
        
        // Clear and set panel
        welcomePanel.removeAll();
        welcomePanel.setLayout(new java.awt.BorderLayout());
        welcomePanel.add(textPanel, java.awt.BorderLayout.CENTER);
    }
    
    welcomePanel.revalidate();
    welcomePanel.repaint();
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logout = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        vehicles_dash = new javax.swing.JButton();
        dashboard = new javax.swing.JButton();
        p_approval = new javax.swing.JButton();
        a_users = new javax.swing.JButton();
        welcomePanel = new javax.swing.JPanel();
        logs = new javax.swing.JButton();
        a_clients = new javax.swing.JButton();
        mhome = new javax.swing.JDesktopPane();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 153, 51));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 36)); // NOI18N
        jLabel1.setText("ADMIN PANEL");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 0, 504, 85));

        logout.setText("LOGOUT");
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        jPanel3.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, 120, 40));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1090, -1));

        jPanel2.setBackground(new java.awt.Color(255, 153, 0));

        vehicles_dash.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        vehicles_dash.setText("VEHICLES");
        vehicles_dash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vehicles_dashActionPerformed(evt);
            }
        });

        dashboard.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        dashboard.setText("DASHBOARD");
        dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashboardMouseEntered(evt);
            }
        });
        dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboardActionPerformed(evt);
            }
        });

        p_approval.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        p_approval.setText("APPROVAL");
        p_approval.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                p_approvalMouseClicked(evt);
            }
        });
        p_approval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                p_approvalActionPerformed(evt);
            }
        });

        a_users.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        a_users.setText("USERS");
        a_users.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                a_usersMouseClicked(evt);
            }
        });

        welcomePanel.setBackground(new java.awt.Color(255, 153, 0));

        javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(welcomePanel);
        welcomePanel.setLayout(welcomePanelLayout);
        welcomePanelLayout.setHorizontalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        welcomePanelLayout.setVerticalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 136, Short.MAX_VALUE)
        );

        logs.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        logs.setText("LOGS");
        logs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logsActionPerformed(evt);
            }
        });

        a_clients.setFont(new java.awt.Font("Microsoft YaHei", 3, 12)); // NOI18N
        a_clients.setText("VIEW CLIENTS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(welcomePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(a_clients, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dashboard, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                    .addComponent(vehicles_dash, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                    .addComponent(p_approval, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(a_users, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logs, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(welcomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vehicles_dash, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(p_approval, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(a_users, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logs, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(a_clients, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 150, 720));

        mhome.setBackground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout mhomeLayout = new javax.swing.GroupLayout(mhome);
        mhome.setLayout(mhomeLayout);
        mhomeLayout.setHorizontalGroup(
            mhomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 933, Short.MAX_VALUE)
        );
        mhomeLayout.setVerticalGroup(
            mhomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );

        jPanel1.add(mhome, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 933, 720));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vehicles_dashActionPerformed(java.awt.event.ActionEvent evt) {
        
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.log("Button Click", "Admin clicked: Vehicles", currentUsername, userIp);
        resetButtonColors();
        vehicles_dash.setBackground(new Color(110, 0, 0));
        vehicles_dash.setForeground(Color.WHITE);
        showInternalFrame(new add_vehicles());
    }

    private void dashboardActionPerformed(java.awt.event.ActionEvent evt) {
        
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.log("Button Click", "Admin clicked: Dashboard", currentUsername, userIp);
        resetButtonColors();
        dashboard.setBackground(new Color(110, 0, 0));
        dashboard.setForeground(Color.WHITE);
        showInternalFrame(new home());
    }

    private void dashboardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashboardMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_dashboardMouseEntered

    private void p_approvalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_p_approvalMouseClicked
        
    }//GEN-LAST:event_p_approvalMouseClicked

    private void p_approvalActionPerformed(java.awt.event.ActionEvent evt) {
        
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.log("Button Click", "Admin clicked: Approval", currentUsername, userIp);
        resetButtonColors();
        p_approval.setBackground(new Color(110, 0, 0));
        p_approval.setForeground(Color.WHITE);
        showInternalFrame(new Approval());
    }

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {                                    
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.logLogout(currentUsername, userIp);
        this.dispose();
        loginForm log = new loginForm();
        log.setVisible(true);
        log.setLocationRelativeTo(null);
    }

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_logoutActionPerformed

    private void a_usersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_a_usersMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_a_usersMouseClicked

    private void logsActionPerformed(java.awt.event.ActionEvent evt) {
        
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.log("Button Click", "Admin clicked: Logs", currentUsername, userIp);
        resetButtonColors();
        logs.setBackground(new Color(110, 0, 0));
        logs.setForeground(Color.WHITE);
        showInternalFrame(new user_logs());
    }

    private void reset_requestsActionPerformed(java.awt.event.ActionEvent evt) {
        
        String userIp = "Unknown";
        try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
        Logger.log("Button Click", "Admin clicked: Reset Requests", currentUsername, userIp);
        resetButtonColors();
        
        showInternalFrame(new passwordResetRequests());
    }

    private void a_clientsActionPerformed(java.awt.event.ActionEvent evt) {
        
        boolean found = false;
        for (JInternalFrame frame : mhome.getAllFrames()) {
            if (frame instanceof v_clients) {
                frame.moveToFront();
                frame.setVisible(true);
                try { frame.setSelected(true); } catch (Exception e) {}
                found = true;
                break;
            }
        }
        if (!found) {
            v_clients vc = new v_clients();
            mhome.add(vc);
            vc.setVisible(true);
            try { vc.setSelected(true); } catch (Exception e) {}
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new adminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton a_clients;
    private javax.swing.JButton a_users;
    private javax.swing.JButton dashboard;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton logout;
    private javax.swing.JButton logs;
    private javax.swing.JDesktopPane mhome;
    private javax.swing.JButton p_approval;
    private javax.swing.JButton vehicles_dash;
    private javax.swing.JPanel welcomePanel;
    // End of variables declaration//GEN-END:variables
}
