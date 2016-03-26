/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libPack.Command;

/**
 *
 * @author Administrator
 */
public class SendMessage extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Command c = new Command();

        try
        {
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            c = (Command)in.readObject();
            in.close();

            // set file name...
            File file = new File(PathSettings.projectPath + "\\ConnectRequest\\" + c.name.toUpperCase() + ".MSG");

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
