package servlet.customers;

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

@WebServlet(name = "AccountServlet", urlPatterns = {"/AccountServlet"})
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        try {
            UserDAO dao  = new UserDAO();
            User    user = dao.findByEmail(dao.getEmailById(userId));
            if (user == null) {
                response.setStatus(404);
                out.write("{\"error\":\"User not found\"}");
                return;
            }
            // Return balance, name, phone for the account page
            out.write(String.format(
                "{\"balance\":%.2f,\"name\":\"%s\",\"phone\":\"%s\"}",
                user.getBalance(),
                user.getName()  != null ? user.getName().replace("\"", "'")  : "",
                user.getPhone() != null ? user.getPhone().replace("\"", "'") : ""
            ));
        } catch (Exception e) {
            response.setStatus(500);
            out.write("{\"error\":\"Could not load account\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out    = response.getWriter();
        String      action = request.getParameter("action");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.write("{\"error\":\"Not logged in\"}");
            return;
        }
        int userId = (int) session.getAttribute("userId");

        try {
            UserDAO dao = new UserDAO();
            if ("update".equals(action)) {
                String name  = request.getParameter("name");
                String phone = request.getParameter("phone");
                dao.updateProfile(userId, name, phone);
                out.write("{\"message\":\"Profile updated successfully\"}");

            } else if ("delete".equals(action)) {
                dao.deleteAccount(userId);
                session.invalidate();
                out.write("{\"message\":\"Account deleted\"}");

            } else {
                response.setStatus(400);
                out.write("{\"error\":\"Unknown action\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.write("{\"error\":\"Action failed\"}");
        }
    }
}
