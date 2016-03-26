package remoteMonitor;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import libPack.Command;
import libPack.Feedback;
import libPack.MousePositions;
import libPack.SingleKeyEvent;


public class Monitor extends javax.swing.JFrame {
    MainForm parent;

    Vector <SingleKeyEvent> queuedKeyEvents;
    
    public boolean runningSession;
    Timer sessionTimer;
    
    public boolean runningSS;
    Timer ssTimer;

    BufferedImage biSS;

    int mousePosition;
    
    public Monitor(MainForm parent) {
        initComponents();
        this.parent = parent;
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);

        biSS = new BufferedImage(Feedback.WW, Feedback.HH, BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<Feedback.HH;y++) {
            for(int x=0;x<Feedback.WW;x++) {
                biSS.setRGB(x, y, 0xffffff);
            }
        }
        jLabelSS.setIcon(new ImageIcon(biSS));

        sessionTimer = new Timer();
        SessionTimerTask sessionTT = new SessionTimerTask();
        runningSession = true;
        sessionTimer.schedule(sessionTT,500);

        ssTimer = new Timer();
        ScreenShotTimerTask ssTT = new ScreenShotTimerTask();
        runningSS = true;
        ssTimer.schedule(ssTT,500);

        mousePosition = MousePositions.NORMAL;

        queuedKeyEvents = new Vector<SingleKeyEvent>();
    }
    
    class ScreenShotTimerTask extends TimerTask {
        int progress;
        String progressChar[];

        public ScreenShotTimerTask() {
            progress = 0;
            progressChar = new String[] {"|","/","-","\\"};
        }

        public void run() {

            Command c = new Command();
            Feedback ssFB = new Feedback();
            
            c.name = "user"; //parent.jTextConnectAs.getText();
            c.password = new String("pass"); //parent.jPasswordField1.getPassword());
            
            while(runningSS) {

                progress++;
                if(progress == 12) {
                    progress = 0;
                }
                jLabelProgress.setText(progressChar[progress%4]);
                Calendar lastEntry = null;
                Calendar currEntry = null;

                // try fetching screen shot...
                try {
                    String urlstr = "http://" + parent.jTextIPName.getText() + ":8084/Servlets/GetSS";
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
                    ssFB = (Feedback)in.readObject();
                    in.close();

                    //System.out.println("COMMAND: " + ssFB.command);
                    if(ssFB.command == 0) {
                        currEntry = ssFB.refreshTime;
                        if(lastEntry == null) {
                            lastEntry = ssFB.refreshTime;
                        }
                        jLabelTime.setText(currEntry.get(currEntry.MINUTE) + ":" + currEntry.get(currEntry.SECOND));
                        
                        for(int y=0;y<Feedback.HH;y++) {
                            for(int x=0;x<Feedback.WW;x++) {
                                biSS.setRGB(x, y, ssFB.ss[y][x]);
                            }
                        }
                        jLabelSS.repaint();
                        
                        jLabelStatus.setText("CONNECTED!");
                    }else if(ssFB.command == -2) {
                        jLabelStatus.setText("PASSWORD DENIED!");
                    }
                    
                }catch(Exception e) {
                    System.out.println("Exception Sending Command: " + e);
                }
                if(lastEntry == null) {
                    jLabelStatus.setText("WAITING!");
                }
                try { Thread.sleep(1000); } catch(Exception e) { ; }
                
            }
        }
    }

    class SessionTimerTask extends TimerTask {

        Command commandEvent;
        
        public SessionTimerTask() {
            commandEvent = new Command();
            commandEvent.name = "user"; //parent.jTextConnectAs.getText();
            commandEvent.password = new String("pass"); //parent.jPasswordField1.getPassword());
        }

        public void run() {
            while(runningSession) {
                sendMouseEvent();

                // clear old sent strokes..
                commandEvent.keyEvents.clear();
                sendKeyboardEvent();

                try { Thread.sleep(500); } catch(Exception e) { ; }
            }
        }

        public void sendKeyboardEvent() {
            if(queuedKeyEvents.size()==0) {
                return;
            }

            commandEvent.command = commandEvent.COMMAND_KEYBOARD;
            while(queuedKeyEvents.size()!=0) {
                SingleKeyEvent ske = queuedKeyEvents.remove(0);
                commandEvent.keyEvents.add(ske);
            }

            try {
                String urlstr = "http://" + parent.jTextIPName.getText() + ":8084/Servlets/ReportKeyboard";
                URL url = new URL(urlstr);
                URLConnection connection = url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);

                // don't use a cached version of URL connection
                connection.setUseCaches(false);
                connection.setDefaultUseCaches(false);

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                out.writeObject(commandEvent);
                out.close();

                // just to ensure object sent and servlet executed..
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                in.readObject();
                in.close();
            }catch(Exception e) {
                System.out.println("Exception Sending Mouse: " + e);
            }
        }
        
        public void sendMouseEvent() {
            int mX = MouseInfo.getPointerInfo().getLocation().x;
            int mY = MouseInfo.getPointerInfo().getLocation().y;
            int lX = jLabelSS.getX() + jPanel2.getX() + jPanel1.getX() + getLocation().x + 4;
            int lY = jLabelSS.getY() + jPanel2.getY() + jPanel1.getY() + getLocation().y + 30;
            int w = jLabelSS.getWidth();
            int h = jLabelSS.getHeight();
            int rX = mX - lX;
            int rY = mY - lY;
            if(rX < 0 || rY < 0) {
                rX = rY = -1;
            }
            if(rX > w) {
                rX = rY = -1;
            }
            if(rY > h) {
                rX = rY = -1;
            }
            if(rX!=-1) {
                //System.out.println("RELATIVE: " + rX + ", " + rY);
            }else {
                return;
            }

            commandEvent.command = commandEvent.COMMAND_MOUSE;
            commandEvent.x = rX;
            commandEvent.y = rY;

            // wait periods to check final mouse events...
            if(mousePosition==MousePositions.PRESSED) {
                try { Thread.sleep(100); } catch(Exception e) { } ;
            }
            if(mousePosition==MousePositions.RELEASED) {
                try { Thread.sleep(100); } catch(Exception e) { } ;
            }
            if(mousePosition==MousePositions.CLICKED) {
                try { Thread.sleep(100); } catch(Exception e) { } ;
            }

            switch(mousePosition) {
                case MousePositions.NORMAL:
                    //System.out.println("Reporting Normal");
                    commandEvent.parameters = MousePositions.NORMAL;
                    break;
                case MousePositions.CLICKED:
                    //System.out.println("Reporting Clicked");
                    commandEvent.parameters = MousePositions.CLICKED;
                    mousePosition = MousePositions.NORMAL;
                    break;
                case MousePositions.DOUBLE_CLICKED:
                    //System.out.println("Reporting Double Clicked");
                    commandEvent.parameters = MousePositions.DOUBLE_CLICKED;
                    mousePosition = MousePositions.NORMAL;
                    break;
                case MousePositions.RIGHT_CLICKED:
                    //System.out.println("Reporting Right Clicked");
                    commandEvent.parameters = MousePositions.RIGHT_CLICKED;
                    mousePosition = MousePositions.NORMAL;
                    break;
                case MousePositions.PRESSED:
                    //System.out.println("Reporting Pressed");
                    commandEvent.parameters = MousePositions.PRESSED;
                    break;
                case MousePositions.RELEASED:
                    //System.out.println("Reporting Released");
                    commandEvent.parameters = MousePositions.RELEASED;
                    mousePosition = MousePositions.NORMAL;
                    break;
            }

            try {
                String urlstr = "http://" + parent.jTextIPName.getText() + ":8084/Servlets/ReportMouse";
                URL url = new URL(urlstr);
                URLConnection connection = url.openConnection();

                connection.setDoOutput(true);
                connection.setDoInput(true);

                // don't use a cached version of URL connection
                connection.setUseCaches(false);
                connection.setDefaultUseCaches(false);

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                out.writeObject(commandEvent);
                out.close();

                // just to ensure object sent and servlet executed..
                ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                in.readObject();
                in.close();
            }catch(Exception e) {
                System.out.println("Exception Sending Mouse: " + e);
            }
            
        }
    }


    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabelSS = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelTime = new javax.swing.JLabel();
        jLabelTime1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabelProgress = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        new LoadForm();
        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabelSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zImgPack/Profile640x480.PNG"))); // NOI18N
        jLabelSS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelSSMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelSSMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelSSMouseReleased(evt);
            }
        });
        jLabelSS.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabelSSMouseMoved(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 255, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SEND MESSAGE");
        jLabel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 255, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("LIST DIRECTORY");
        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 255, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("FETCH FILE");
        jLabel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        jLabel5.setForeground(new java.awt.Color(255, 51, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("CLOSE");
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        jLabelStatus.setBackground(new java.awt.Color(51, 51, 51));
        jLabelStatus.setForeground(new java.awt.Color(255, 51, 0));
        jLabelStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStatus.setText("STATUS");
        jLabelStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelTime.setBackground(new java.awt.Color(51, 51, 51));
        jLabelTime.setForeground(new java.awt.Color(255, 51, 0));
        jLabelTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTime.setText("STATUS");
        jLabelTime.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabelTime.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelTimeMouseClicked(evt);
            }
        });

        jLabelTime1.setBackground(new java.awt.Color(51, 51, 51));
        jLabelTime1.setForeground(new java.awt.Color(255, 51, 0));
        jLabelTime1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTime1.setText("LAST FRAME");
        jLabelTime1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 255, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("AUDIO TRANSMIT");
        jLabel6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 255, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("AUDIO RECEIVE");
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabelSS)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(jLabelStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabelTime1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabelTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabelSS)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(46, 46, 46)
                        .add(jLabelStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabelTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabelTime1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MONITOR");
        jLabel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabelProgress.setBackground(new java.awt.Color(51, 51, 51));
        jLabelProgress.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabelProgress.setForeground(new java.awt.Color(255, 0, 0));
        jLabelProgress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelProgress.setText("|");
        jLabelProgress.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabelProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 833, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelProgress)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jLabel1, jLabelProgress}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // TODO add your handling code here:
        runningSS = false;
        runningSession = false;

        sessionTimer.cancel();
        ssTimer.cancel();
        
        setVisible(false);
        parent.setVisible(true);
        
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabelSSMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSSMouseMoved
        // TODO add your handling code here:


    }//GEN-LAST:event_jLabelSSMouseMoved

    private void jLabelSSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSSMouseClicked
        // TODO add your handling code here:

        if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount()==1) {
            mousePosition = MousePositions.CLICKED;
            System.out.println("LEFT CLICK!");
        }else if(evt.getButton() == MouseEvent.BUTTON3) {
            mousePosition = MousePositions.RIGHT_CLICKED;
            System.out.println("RIGHT CLICK!");
        }else if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount()==2) {
            mousePosition = MousePositions.DOUBLE_CLICKED;
            System.out.println("DOUBLE CLICK!");
        }
        
//
//        System.out.println("ACTUAL: " + evt.getX() + ", " + evt.getY());
//
//        int mX = MouseInfo.getPointerInfo().getLocation().x;
//        int mY = MouseInfo.getPointerInfo().getLocation().y;
//
//        int lX = jLabelSS.getX() + jPanel2.getX() + jPanel1.getX() + this.getLocation().x + 4;
//        int lY = jLabelSS.getY() + jPanel2.getY() + jPanel1.getY() + this.getLocation().y + 30;
//
//        //System.out.println("MOUSE POS: " + mX + ", " + mY);
//
//        int w = jLabelSS.getWidth();
//        int h = jLabelSS.getHeight();
//
//        int rX = mX - lX;
//        int rY = mY - lY;
//
//        if(rX > w) {
//            rX = rY = -1;
//        }
//        if(rY > h) {
//            rX = rY = -1;
//        }
//
//        if(rX!=-1) {
//            System.out.println("RELATIVE: " + rX + ", " + rY);
//        }
        



        
    }//GEN-LAST:event_jLabelSSMouseClicked

    private void jLabelSSMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSSMousePressed
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON1) {
            mousePosition = MousePositions.PRESSED;
            System.out.println("PRESSED!");
        }

    }//GEN-LAST:event_jLabelSSMousePressed

    private void jLabelSSMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelSSMouseReleased
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON1) {
            mousePosition = MousePositions.RELEASED;
            System.out.println("RELEASED!");
        }


    }//GEN-LAST:event_jLabelSSMouseReleased

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped

    }//GEN-LAST:event_jPanel1KeyTyped

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        // TODO add your handling code here:
//        lastEvent = evt;
//        System.out.println("KEY CODE " + evt.getKeyCode() + ", CHAR: " + evt.getKeyChar() + " CHAR IT: " + (int)(evt.getKeyChar()));
                
    }//GEN-LAST:event_formKeyTyped

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        SingleKeyEvent ske = new SingleKeyEvent();
        ske.pressed = true;
        ske.keyCode = evt.getKeyCode();
        queuedKeyEvents.add(ske);

    }//GEN-LAST:event_formKeyPressed

    private void jLabelTimeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelTimeMouseClicked
        // TODO add your handling code here:


    }//GEN-LAST:event_jLabelTimeMouseClicked

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        // TODO add your handling code here:
        SingleKeyEvent ske = new SingleKeyEvent();
        ske.pressed = false;
        ske.keyCode = evt.getKeyCode();
        queuedKeyEvents.add(ske);


        
    }//GEN-LAST:event_formKeyReleased

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        // TODO add your handling code here:
        new FetchFileDialog(this).setVisible(true);
        

    }//GEN-LAST:event_jLabel4MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        new FetchFileList(this).setVisible(true);
        
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        // TODO add your handling code here:
        new SendMessageDialog(this).setVisible(true);
        
    }//GEN-LAST:event_jLabel2MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked

        try {
            new ProcessBuilder("java","-jar",System.getProperty("user.dir")  + "\\Audio-Support-Client\\AudioTransmitClient\\dist\\AudioTransmitClient.jar").start();
        }catch(Exception e) {
            System.out.println("Exception Starting SMS Server: " + e);
        }
        
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked

        try {
            new ProcessBuilder("java","-jar",System.getProperty("user.dir") + "\\Audio-Support-Client\\AudioReceiveClient\\dist\\AudioReceiveClient.jar").start();
        }catch(Exception e) {
            System.out.println("Exception Starting SMS Server: " + e);
        }
        
    }//GEN-LAST:event_jLabel7MouseClicked

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabelProgress;
    private javax.swing.JLabel jLabelSS;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelTime;
    private javax.swing.JLabel jLabelTime1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    
}

