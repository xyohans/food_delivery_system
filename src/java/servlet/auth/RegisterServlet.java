package servlet.auth;

import DAO.UserDAO;
import com.google.gson.Gson;
import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    static class RegReq {

        String name, email, phone, password;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Gson gson = new Gson();
            RegReq req = gson.fromJson(request.getReader(), RegReq.class);

            if (req == null || req.email == null || req.password == null
                    || req.email.isEmpty() || req.password.isEmpty()) {
                response.setStatus(400);
                out.write("{\"error\":\"Email and password are required\"}");
                return;
            }

            if (req.password.length() < 6) {
                response.setStatus(400);
                out.write("{\"error\":\"Password must be at least 6 characters\"}");
                return;
            }

            UserDAO dao = new UserDAO();

            String hashed = BCrypt.hashpw(req.password, BCrypt.gensalt());
            boolean created = dao.register(
                    req.name != null ? req.name : "",
                    req.email,
                    hashed,
                    req.phone != null ? req.phone : ""
            );

            if (created) {
                out.write("{\"message\":\"Account created successfully\"}");
            } else {
                response.setStatus(409);
                out.write("{\"error\":\"An account with this email already exists\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.write("{\"error\":\"Registration failed\"}");
        }
    }
}
