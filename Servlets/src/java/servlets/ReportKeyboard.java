package servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libPack.Command;

public class ReportKeyboard extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Command c = new Command();

        try
        {
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            c = (Command)in.readObject();
            in.close();

            // set file name...
            File file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + c.name.toUpperCase() + ".KBD");

            try {
                ObjectOutputStream obOut = new ObjectOutputStream(new FileOutputStream(file));
                obOut.writeObject(c);
                obOut.close();
            }catch(Exception e) {
                System.out.println("Exception: " + e);
            }

            ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
            out.writeObject(c);
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
    }// </editor-fold>
}
