package servlets;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import JavaLib.*;
import libPack.Command;
import libPack.Feedback;
import libPack.HostSettings;
import rsaPack.PGPEncDec;

public class RequestOTP extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Command c = new Command();
        Feedback fb = new Feedback();
        HostSettings settings = new HostSettings();

        try
        {
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            c = (Command)in.readObject();
            in.close();
            
            // read settings file
            File file = new File(PathSettings.projectPath + "\\SETTINGS.DAT");

            if(file.exists()) {
                try {
                    ObjectInputStream in2 = new ObjectInputStream(new FileInputStream(file));
                    settings = (HostSettings)in2.readObject();
                    in2.close();
                    System.out.println("Settings Read!");
                    
                    // create new otp
                    settings.regenerateKeys();

                    try {
                        ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(PathSettings.projectPath + "\\SETTINGS.DAT"));
                        out1.writeObject(settings);
                        out1.close();
                    }catch(Exception e) {
                        System.out.println("Exception Updating Settings: " + e);
                    }
                    
                    // code to generate key .
                    PGPEncDec p = new PGPEncDec(settings.rsaPublic, settings.rsaModulus);
                    long enc = p.process(100);
                    String key = "OTP KEY: " + enc;
                    System.out.println("Current OTP Key: " + key);
                    
                    // code to send message.
                    try {
                        String pre, post, tempS;
                        pre = key;
                        post = "";
                        for (int i = 0; i < pre.length(); i++) {
                            tempS = Integer.toHexString((int) pre.charAt(i)).toUpperCase();
                            if (tempS.length() == 1) {
                                tempS = "0" + tempS;
                            }
                            tempS = "%" + tempS;
                            post += tempS;
                        }
                        
                        // now sending message
                        
                 URL url = new URL("http://india.timessms.com/http-api/receiverall.asp?username=" + settings.smsUserID + "&password=" + settings.smsPassword + "&sender=" + settings.smsSenderID + "&cdmasender=" + settings.smsCDMASenderID + "&to=" + settings.adminMobile + "&message=" + post + "");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.connect();
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String smsResponse = "";
                        String temp;
                        while ((temp = br.readLine()) != null) {
                            smsResponse += temp + "\n";
                        }
                        br.close();
                        conn.disconnect();
                        fb.response = smsResponse;
                        System.out.println("SMS RESPONSE: " + smsResponse);
                        fb.responseCode = 1; // otp generated and sent.
                        } catch (Exception e) {
                        fb.responseCode = -1; // error..
                        fb.response = "SMS Sending Failure: " + e;
                        System.out.println("SMS Sending Failure: " + e);
                    }
                    
                }catch(Exception e) {
                    System.out.println("Exception Reading Settings: " + e);
                    fb.responseCode = -1; // error..
                }
            }else {
                System.out.println("No Settings File Found!");
                fb.responseCode = -1;
            }

            ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
            out.writeObject(fb);
            out.close();
            
        }
        catch(Exception e)
        {
            response.getWriter().print("ERROR: " + e);
        }        
        
        

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new LoadForm();
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new LoadForm();
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
