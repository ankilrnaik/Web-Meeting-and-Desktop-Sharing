package serverPack;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import JavaLib.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import libPack.Command;
import libPack.Feedback;
import libPack.MousePositions;
import libPack.SingleKeyEvent;


public class HostSession extends javax.swing.JFrame {
    MainForm parent;
    
    public boolean runningSession;
    Timer sessionTimer;
    
    public boolean runningSS;
    Timer ssTimer;
    
    BufferedImage biSS;

    public String acceptFrom;

    int sx=0, sy=0;
    
    public HostSession(MainForm parent) {
        initComponents();
        this.parent = parent;
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);
        sx = sd.width;
        sy = sd.height;

        biSS = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);

        acceptFrom = "user"; //parent.jTextConnFrom.getText();
        clearOldSS();

        sessionTimer = new Timer();
        SessionTimerTask monitorTT = new SessionTimerTask();
        runningSession = true;
        sessionTimer.schedule(monitorTT,500);

        ssTimer = new Timer();
        ScreenShotTimerTask ssTT = new ScreenShotTimerTask();
        runningSS = true;
        ssTimer.schedule(ssTT,500);

        informSessionStart();
    }

    public void informSessionStart() {
        Feedback fb = new Feedback();
        fb.serverPassword = new String("pass"); //parent.jPasswordField1.getPassword());
        fb.command = 0; // ok
        fb.allowFile = parent.jCheckAllowFile.isSelected();
        fb.allowKeyboard = parent.jCheckAllowKbd.isSelected();
        fb.allowMouse = parent.jCheckAllowMouse.isSelected();
        try {
            // prepare for respective client...
            ObjectOutputStream obOut = new ObjectOutputStream(new FileOutputStream(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom.toUpperCase() + ".CURR"));
            obOut.writeObject(fb);
            obOut.close();
        }catch(Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public void informSessionEnd() {
        try {
            new File(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom.toUpperCase() + ".CURR").delete();
        }catch(Exception e) {
            System.out.println("Exception Deleting Current Session: " + e);
        }
    }

    class SessionTimerTask extends TimerTask {
        int progress;
        String progressChar[];

        public SessionTimerTask() {
            progress = 0;
            progressChar = new String[] {"|","/","-","\\"};
        }

        public void run() {
            while(runningSession) {

                progress++;
                if(progress == 12) {
                    progress = 0;
                }
                jLabelProgress.setText(progressChar[progress%4]);

                // mouse.
                File file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom + ".MOUSE");
                if(file.exists()) {
                    try {
                        Command c = new Command();
                        // read object
                        ObjectInputStream in1 = new ObjectInputStream(new FileInputStream(file));
                        c = (Command)in1.readObject();
                        in1.close();
                        file.delete();

                        if(c.password.equals(new String("pass"))) { //parent.jPasswordField1.getPassword()))) {
                           processMouse(c);
                        }else {
                            System.out.println("MOUSE COMMAND: AUTHENTICATION FAILED!");
                        }
                    }catch(Exception e) {
                        System.out.println("Exception Reading Mouse Command: " + e);
                        e.printStackTrace();
                    }
                }

                // keyboard...
                file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom + ".KBD");
                if(file.exists()) {
                    try {
                        Command c = new Command();
                        // read object
                        ObjectInputStream in1 = new ObjectInputStream(new FileInputStream(file));
                        c = (Command)in1.readObject();
                        in1.close();
                        file.delete();

                        if(c.password.equals("pass")) { //new String(parent.jPasswordField1.getPassword()))) {
                           processKeyboard(c);
                        }else {
                            System.out.println("KEYBOARD COMMAND: AUTHENTICATION FAILED!");
                        }
                    }catch(Exception e) {
                        System.out.println("Exception Reading Keyboard Command: " + e);
                        e.printStackTrace();
                    }
                }

                // message.
                file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom + ".MSG");
                if(file.exists()) {
                    try {
                        Command c = new Command();
                        // read object
                        ObjectInputStream in1 = new ObjectInputStream(new FileInputStream(file));
                        c = (Command)in1.readObject();
                        in1.close();
                        file.delete();

                        if(c.password.equals("pass")) { //new String(parent.jPasswordField1.getPassword()))) {
                           processMessage(c);
                        }else {
                            System.out.println("MESSAGE COMMAND: AUTHENTICATION FAILED!");
                        }
                    }catch(Exception e) {
                        System.out.println("Exception Reading Command: " + e);
                    }
                }
                try { Thread.sleep(100); } catch(Exception e) { ; }
            }
        }
    }

    public void clearOldSS() {
        File f = new File(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom.toUpperCase() + ".SS");
        try {
            f.delete();
        }catch(Exception e) {
            ;
        }

        f = new File(PathSettings.projectPath + "\\ConnectRequest");
        File files[] = f.listFiles();
        for(File file: files) {
            if(file.getName().endsWith(".SS")) {
                try {
                    file.delete();
                }catch(Exception e) {
                    System.out.println("Exception: " + e);
                }
            }
        }
    }

    public void processMessage(Command c) {
        addText("Message Received: " + (String)c.parameters);
    }

    public void processMouse(Command c) {
        if((Integer)c.parameters != MousePositions.NORMAL) {
            ;//addText("Mouse Command Received " + c.name + ", TYPE: " + (Integer)c.parameters);
        }
        
        int x = c.x;
        int y = c.y;
        int mx = (x * sx) / 640;
        int my = (y * sy) / 480;
        //System.out.println("RECEIVED POS AT: " + x + ", " + y);
        //System.out.println("MOUSE POS AT: " + mx + ", " + my);
        if(parent.jCheckAllowMouse.isSelected()) {
            try {
                Robot r = new Robot();
                r.mouseMove(mx, my);
                r.delay(25);
                int action = (Integer)c.parameters;
                switch(action) {
                    case MousePositions.NORMAL:
                        break;
                    case MousePositions.CLICKED:
                        r.mousePress(MouseEvent.BUTTON1_MASK);
                        r.delay(10);
                        r.mouseRelease(MouseEvent.BUTTON1_MASK);
                        break;
                    case MousePositions.DOUBLE_CLICKED:
                        r.mousePress(MouseEvent.BUTTON1_MASK);
                        r.delay(10);
                        r.mouseRelease(MouseEvent.BUTTON1_MASK);
                        r.delay(10);
                        r.mousePress(MouseEvent.BUTTON1_MASK);
                        r.delay(10);
                        r.mouseRelease(MouseEvent.BUTTON1_MASK);
                        break;
                    case MousePositions.PRESSED:
                        r.mousePress(MouseEvent.BUTTON1_MASK);
                        break;
                    case MousePositions.RELEASED:
                        r.mouseRelease(MouseEvent.BUTTON1_MASK);
                        r.delay(10);
                        break;
                    case MousePositions.RIGHT_CLICKED:
                        r.mousePress(MouseEvent.BUTTON3_MASK);
                        r.delay(10);
                        r.mouseRelease(MouseEvent.BUTTON3_MASK);
                        break;
                }
            }catch(Exception e) {
                System.out.println("MOUSE CONTROL ERROR: " + e);
            }
        }
    }

    public void processKeyboard(Command c) {
        addText("Keyboard Command Received " + c.name);
        if(parent.jCheckAllowKbd.isSelected()) {
            try {
                Robot r = new Robot();
                for(SingleKeyEvent ske: c.keyEvents) {
                    if(ske.pressed) {
                        r.keyPress(ske.keyCode);
                    }else {
                        r.keyRelease(ske.keyCode);
                    }
                }
            }catch(Exception e) {
                System.out.println("KEYBOARD CONTROL ERROR: " + e);
            }
        }
    }
    
    public void process(Command c) {
        addText("Command received from: " + c.name);
    }

    class ScreenShotTimerTask extends TimerTask {

        public ScreenShotTimerTask( ) {
            ;
        }

        public void run() {

            Feedback fb = new Feedback();
            fb.serverPassword = new String("pass"); //parent.jPasswordField1.getPassword());
            fb.command = 0; // ok
            
            fb.allowFile = parent.jCheckAllowFile.isSelected();
            fb.allowKeyboard = parent.jCheckAllowKbd.isSelected();
            fb.allowMouse = parent.jCheckAllowMouse.isSelected();
            
            while(runningSS) {
                // update time so that client knows data is being updated...
                fb.refreshTime = Calendar.getInstance();
                // capture screen shot...
                try {
                    BufferedImage sc = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    BufferedImage scSmall = new BufferedImage(fb.WW,fb.HH,BufferedImage.TYPE_INT_RGB);
                    Graphics2D gSrc = sc.createGraphics();
                    Graphics2D gDest = scSmall.createGraphics();
                    gDest.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    //gDest.drawImage(sc,0, 0, fb.WW, fb.HH, null);
                    AffineTransform at = AffineTransform.getScaleInstance((double)fb.WW/sc.getWidth(),(double)fb.HH/sc.getHeight());
                    gDest.drawRenderedImage(sc, at);
                    for(int y=0;y<fb.HH;y++) {
                        for(int x=0;x<fb.WW;x++) {
                            fb.ss[y][x] = scSmall.getRGB(x,y);
                        }
                    }
                }catch(Exception e) {
                    System.out.println("Exception: " + e);
                }

                try {
                    // prepare for respective client...
                    ObjectOutputStream obOut = new ObjectOutputStream(new FileOutputStream(PathSettings.projectPath + "\\ConnectRequest\\" + acceptFrom.toUpperCase() + ".SS"));
                    obOut.writeObject(fb);
                    obOut.close();
                }catch(Exception e) {
                    System.out.println("Exception: " + e);
                }

                try { Thread.sleep(250); } catch(Exception e) { ; }
            }
        }
    }

    public void addText(String str) {
        jTextStatus.setText(str + "\n" + jTextStatus.getText());
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextStatus = new javax.swing.JTextArea();
        jButton18 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jLabelProgress = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        new LoadForm();

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextStatus.setColumns(20);
        jTextStatus.setEditable(false);
        jTextStatus.setRows(5);
        jScrollPane1.setViewportView(jTextStatus);

        jButton18.setBackground(new java.awt.Color(51, 51, 51));
        jButton18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton18.setText("C L E A R");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(51, 51, 51));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton5.setText("C L O S E");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(51, 51, 51));
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("STATUS");
        jLabel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton19.setBackground(new java.awt.Color(51, 51, 51));
        jButton19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton19.setText("Audio Tx");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setBackground(new java.awt.Color(51, 51, 51));
        jButton20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton20.setText("Audio Rx");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton18)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton19)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton20)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton5)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton18)
                    .add(jButton5)
                    .add(jButton19)
                    .add(jButton20))
                .addContainerGap())
        );

        jLabelProgress.setBackground(new java.awt.Color(51, 51, 51));
        jLabelProgress.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabelProgress.setForeground(new java.awt.Color(255, 0, 0));
        jLabelProgress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelProgress.setText("|");
        jLabelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("SESSION");
        jLabel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabelProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jLabel5, jLabelProgress}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
// TODO add your handling code here:
        jTextStatus.setText("");
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
// TODO add your handling code here:
        informSessionEnd();
        
        runningSS = false;
        runningSession = false;

        setVisible(false);
        parent.setVisible(true);
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed

        try {
            new ProcessBuilder("java","-jar",PathSettings.projectPath  + "\\RemoteHost\\Audio-Support-Server\\AudioTransmitServer\\dist\\AudioTransmitServer.jar").start();
        }catch(Exception e) {
            System.out.println("Exception Starting SMS Server: " + e);
        }

                
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed

        try {
            new ProcessBuilder("java","-jar",PathSettings.projectPath  + "\\RemoteHost\\Audio-Support-Server\\AudioReceiveServer\\dist\\AudioReceiveServer.jar").start();
        }catch(Exception e) {
            System.out.println("Exception Starting SMS Server: " + e);
        }
        
    }//GEN-LAST:event_jButton20ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabelProgress;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextStatus;
    // End of variables declaration//GEN-END:variables
    
}

