
package servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libPack.Command;
import libPack.Feedback;
import libPack.HostSettings;
import rsaPack.PGPEncDec;

public class AuthorizeOTP extends HttpServlet {


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

                    // verify key.
                    long key = Long.parseLong((String)c.parameters);
                    PGPEncDec p = new PGPEncDec(settings.rsaPrivate, settings.rsaModulus);
                    long dec = p.process(key);
                    System.out.println("Decrypted OTP Key: " + dec);
                    
                    if(dec==100) {
                        System.out.println("Access Granted!");
                        fb.responseCode = 1; // authorization ok...
                    }else {
                        System.out.println("Access Denied!");
                        fb.responseCode = -1; // access denied...
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
        }catch(Exception e) {
            response.getWriter().print("ERROR: " + e);
        }
        
    }

        @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
