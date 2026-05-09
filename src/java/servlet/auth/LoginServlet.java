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
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to login page
        response.sendRedirect("index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String pass = request.getParameter("password");

        if (email == null || pass == null || email.isEmpty() || pass.isEmpty()) {
            response.sendRedirect("index.html?error=missing");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.findByEmail(email);


            
            if (user == null || !BCrypt.checkpw(pass, user.getPassword())) {
                response.sendRedirect(request.getContextPath() + "/index.html?error=invalid");
                return;
            }

            // Store user info in session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            switch (user.getRole()) {
                case "admin" ->
                    response.sendRedirect("admin/admin.html");
                case "chef" ->
                    response.sendRedirect("chef/chef.html");
                case "delivery" ->
                    response.sendRedirect("delivery/delivery.html");
                default ->
                    response.sendRedirect("customers/menu.html");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("index.html?error=server");
        }
    }
}
