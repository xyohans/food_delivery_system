/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet.auth;

import DAO.UserDAO;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 *
 * @author yohan
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        
        
        String email=request.getParameter("email");
        String pass=request.getParameter("password");
        try {
            
            
            
            
            UserDAO dao = new UserDAO();
            User user = dao.findByEmail(email);
            if(email.equals(user.getEmail()) && pass.equals(user.getPassword())){
                switch (user.getRole()) {
                    case "admin" -> response.sendRedirect("admin/admin.html");
                    case "chef" -> response.sendRedirect("chef/chef.html");
                    case "delivery" -> response.sendRedirect("delivert/delivery.html");
                    default -> {
                        response.sendRedirect("customers/menu.html");
                    }
                }
            }
            
            else
                out.print("wrong email or password");
        } catch (Exception ex) {
//            System.getLogger(LoginServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            out.print("Somthing went wrong" + ex);
            
        }
    }

 
}
