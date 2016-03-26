
package servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import JavaLib.*;
import libPack.Command;
import libPack.Feedback;


public class Connect extends HttpServlet {

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Command c = new Command();
        Feedback fb = new Feedback();

        try
        {
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            c = (Command)in.readObject();
            in.close();

            // set file name...
            File file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + c.name.toUpperCase() + ".CURR");

            if(file.exists()) {
                try {
                    ObjectInputStream in2 = new ObjectInputStream(new FileInputStream(file));
                    fb = (Feedback)in2.readObject();
                    in2.close();
                    System.out.println("SESSION CHECKED!");
                }catch(Exception e) {
                    fb.responseCode = -2; // invalid session..
                }

                if(fb.serverPassword.equals(c.password)) {
                    System.out.println("Connection Verified!");
                    fb.responseCode = c.COMMAND_NOTHING;
                    fb.response = "CONNECTION VERIFIED";
                }else {
                    System.out.println("Access Denied!");
                    fb.responseCode = -3;
                    fb.response = "INVALID PASSWORD";
                }
            }else {
                System.out.println("No session!");
                fb.responseCode = -2; // no session started yet...
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
     
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new LoadForm();
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        new LoadForm();
        processRequest(request, response);
    }

   
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
