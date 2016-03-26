package remoteMonitor;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*; 
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.event.*;
import libPack.Command;
import libPack.Feedback;

public class MainForm extends javax.swing.JFrame {
    
    /** Creates new form MainMenu */
    public MainForm() {
        initComponents();
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);
        
        jButtonConnect.setEnabled(false);
        jButtonVerifyConnection.setEnabled(false);
        
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButtonConnect = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextIPName = new javax.swing.JTextField();
        jButtonVerifyConnection = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButtonRequestOTP = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextOTP = new javax.swing.JTextField();
        jButtonAuthorizeOTP = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jButtonConnect.setBackground(new java.awt.Color(51, 51, 51));
        jButtonConnect.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonConnect.setText("CONNECT");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setForeground(new java.awt.Color(204, 204, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("ADDRESS TO CONNECT TO");
        jLabel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextIPName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextIPName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextIPName.setText("localhost");

        jButtonVerifyConnection.setBackground(new java.awt.Color(51, 51, 51));
        jButtonVerifyConnection.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonVerifyConnection.setText("VERIFY CONNECTION");
        jButtonVerifyConnection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerifyConnectionActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonVerifyConnection, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonConnect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jTextIPName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextIPName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonVerifyConnection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonConnect)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jLabel6, jTextIPName}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel1Layout.linkSize(new java.awt.Component[] {jButtonConnect, jButtonVerifyConnection}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("MAIN MENU");
        jLabel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton5.setBackground(new java.awt.Color(51, 51, 51));
        jButton5.setText("E X I T");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton5)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButtonRequestOTP.setBackground(new java.awt.Color(51, 51, 51));
        jButtonRequestOTP.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonRequestOTP.setText("REQUEST OTP");
        jButtonRequestOTP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRequestOTPActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setForeground(new java.awt.Color(204, 204, 204));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("OTP");
        jLabel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextOTP.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextOTP.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButtonAuthorizeOTP.setBackground(new java.awt.Color(51, 51, 51));
        jButtonAuthorizeOTP.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonAuthorizeOTP.setText("AUTHORIZE OTP");
        jButtonAuthorizeOTP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAuthorizeOTPActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonAuthorizeOTP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jTextOTP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jButtonRequestOTP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButtonRequestOTP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextOTP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonAuthorizeOTP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {jLabel7, jTextOTP}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .add(18, 18, 18)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// TODO add your handling code here:
        setVisible(false);
        System.exit(0);
    }//GEN-LAST:event_jButton5ActionPerformed

    
    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        // TODO add your handling code here:

        setVisible(false);
        new Monitor(this).setVisible(true);
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void jButtonVerifyConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerifyConnectionActionPerformed
        // TODO add your handling code here:

        Command c = new Command();
        c.name = "user"; //jTextConnectAs.getText();
        c.password = new String("pass"); //jPasswordField1.getPassword());
        try {
            String urlstr = "http://" + jTextIPName.getText() + ":8084/Servlets/Connect";
            URL url = new URL(urlstr);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            // don't use a cached version of URL connection
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            out.writeObject(c);
            out.close();

            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            Feedback fb = (Feedback)in.readObject();
            in.close();

            if(fb.command == c.COMMAND_NOTHING) {
                MessageBox mb = new MessageBox(this, "HOST READY! DO YOU WISH TO CONNECT!");
                mb.setVisible(true);
                if(mb.ok) {
                    new Monitor(this).setVisible(true);
                    setVisible(false);
                }
            }else if(fb.command == -3) {
                new MessageBox(this, "INVALID PASSWORD! ACCESS DENIED!").setVisible(true);
            }else {
                new MessageBox(this, "HOST NOT READY!").setVisible(true);
            }
        }catch(Exception e) {
            new MessageBox(this, "Host Not Ready!").setVisible(true);
            System.out.println("Exception Checking Session: " + e);
        }


    }//GEN-LAST:event_jButtonVerifyConnectionActionPerformed

    private void jButtonRequestOTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRequestOTPActionPerformed
        // TODO add your handling code here:
        
        Command c = new Command();
        c.name = "user"; //jTextConnectAs.getText();
        c.password = new String("pass"); //jPasswordField1.getPassword());
        try {
            String urlstr = "http://" + jTextIPName.getText() + ":8084/Servlets/RequestOTP";
            URL url = new URL(urlstr);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);


            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            out.writeObject(c);
            out.close();

            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            Feedback fb = (Feedback)in.readObject();
            in.close();

            if(fb.responseCode == 1) {
                new MessageBox(this, "OTP SENT. RESPONSE: " + (String)fb.response ).setVisible(true);
            }else {
                new MessageBox(this, "ERROR SENDING OTP!").setVisible(true);
            }
        }catch(Exception e) {
            new MessageBox(this, "Host Not Ready!").setVisible(true);
            System.out.println("Exception Checking Session: " + e);
        }
        
    }//GEN-LAST:event_jButtonRequestOTPActionPerformed

    private void jButtonAuthorizeOTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAuthorizeOTPActionPerformed
        // TODO add your handling code here:
        Command c = new Command();
        c.name = "user"; //jTextConnectAs.getText();
        c.password = new String("pass"); //jPasswordField1.getPassword());
        
        c.parameters = jTextOTP.getText();

        try {
            String urlstr = "http://" + jTextIPName.getText() + ":8084/Servlets/AuthorizeOTP";
            
            URL url = new URL(urlstr);
            URLConnection connection = url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            out.writeObject(c);
            out.close();

            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            Feedback fb = (Feedback)in.readObject();
            in.close();

            if(fb.responseCode == 1) {
                new MessageBox(this, "OTP AUTHORIZATION OK!").setVisible(true);
                
                jButtonConnect.setEnabled(true);
                jButtonVerifyConnection.setEnabled(true);
                
            }else {
                new MessageBox(this, "ERROR AUTHORIZING OTP!").setVisible(true);
            }
        }catch(Exception e) {
            new MessageBox(this, "Host Not Ready!").setVisible(true);
            System.out.println("Exception Checking Session: " + e);
        }        
        
    }//GEN-LAST:event_jButtonAuthorizeOTPActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButtonAuthorizeOTP;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JButton jButtonRequestOTP;
    private javax.swing.JButton jButtonVerifyConnection;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JTextField jTextIPName;
    public javax.swing.JTextField jTextOTP;
    // End of variables declaration//GEN-END:variables
    
}
