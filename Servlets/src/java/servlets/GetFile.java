package servlets;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import libPack.Command;
import libPack.Feedback;
import utilPack.MyFile;

public class GetFile extends HttpServlet {

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
                    fb.responseCode = -2; // file reading error..
                }

                if(!fb.serverPassword.equals(c.password)) {
                    System.out.println("PASSWORD INVALID!");
                    fb.responseCode = -3; // password denied...
                }else {
                    if(!fb.allowFile) {
                        System.out.println("FILE NOT ALLOWED!");
                        fb.responseCode = -5; // file transfer not allowed
                    }else {
                        System.out.println("FILE ALLOWED!");
                        // transfer the file...
                        fb.command = Command.COMMAND_FETCHFILE;
                        try {
                            file = new File((String)c.parameters);
                            if(!file.exists()) {
                                System.out.println("FILE DOES NOT EXIST!");
                                fb.response = "FILE DOES NOT EXIST!";
                                fb.responseCode = -1; // does not exist...
                            }else {
                                System.out.println("FILE READ!");
                                fb.responseCode = Command.COMMAND_FETCHFILE;
                                MyFile f = new MyFile();
                                f.openRead((String)c.parameters);
                                byte data[] = new byte[f.available()];
                                int length = f.available();
                                for(int i=0;i<length;i++) {
                                    data[i] = f.readByte();
                                }
                                fb.response = data;
                            }
                        }catch(Exception e) {
                            System.out.println("ERROR READING FILE: " + e);
                            fb.response = e.toString();
                            fb.responseCode = -1; // error reading file....
                        }
                    }
                }
            }else {
                fb.responseCode = -4; // no session started yet...
            }
            
            System.out.println("SENDING FEEDBACK");
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
