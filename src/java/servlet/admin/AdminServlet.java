package servlet.admin;

import DAO.AdminDAO;
import DAO.UserDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

    AdminDAO adminDao = new AdminDAO();
    UserDAO  userDao  = new UserDAO();
    private final Gson gson = new Gson();

    // ── Auth check helper ────────────────────────────────────────────────────
    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response,
                            PrintWriter out) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            out.write("{\"error\":\"Not logged in\"}");
            return false;
        }
        if (!"admin".equals(session.getAttribute("userRole"))) {
            response.setStatus(403);
            out.write("{\"error\":\"Access denied\"}");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!isAdmin(request, response, out)) return;

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "getOrders" -> {
                    String status = request.getParameter("status");
                    out.write(gson.toJson(adminDao.getOrders(status)));
                }
                case "getStaff" -> {
                    String role = request.getParameter("role");
                    out.write(gson.toJson(adminDao.getAvailableStaff(role)));
                }
                case "getMenu" -> {
                    out.write(gson.toJson(adminDao.getAllMenuItems()));
                }
                case "getAllUsers" -> {
                    out.write(gson.toJson(adminDao.getAllUsers()));
                }
                default -> {
                    response.setStatus(400);
                    out.write("{\"error\":\"Unknown action\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.write("{\"error\":\"Server error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!isAdmin(request, response, out)) return;

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);

        AdminReq req    = gson.fromJson(sb.toString(), AdminReq.class);
        String   action = req.getAction();

        try {
            switch (action) {
                case "assignChef" -> {
                    adminDao.assignChef(req.getOrderId(), req.getStaffId());
                    out.write("{\"message\":\"Chef assigned\"}");
                }
                case "assignDelivery" -> {
                    adminDao.assignDelivery(req.getOrderId(), req.getStaffId());
                    out.write("{\"message\":\"Delivery assigned\"}");
                }
                case "refund" -> {
                    adminDao.refundOrder(req.getOrderId());
                    out.write("{\"message\":\"Refund successful\"}");
                }
                case "toggleMenu" -> {
                    adminDao.toggleMenuItem(req.getItemId());
                    out.write("{\"message\":\"Menu item updated\"}");
                }
                case "addMenuItem" -> {
                    adminDao.addMenuItem(req.getName(), req.getDescription(),
                                        req.getPrice(), req.getCategoryId());
                    out.write("{\"message\":\"Item added\"}");
                }
                case "updatePrice" -> {
                    adminDao.updateItemPrice(req.getItemId(), req.getPrice());
                    out.write("{\"message\":\"Price updated\"}");
                }
                case "registerStaff" -> {
                    // validate
                    if (req.getEmail() == null || req.getEmail().isEmpty() ||
                        req.getPassword() == null || req.getPassword().isEmpty()) {
                        response.setStatus(400);
                        out.write("{\"error\":\"Email and password are required\"}");
                        return;
                    }
                    if (req.getPassword().length() < 6) {
                        response.setStatus(400);
                        out.write("{\"error\":\"Password must be at least 6 characters\"}");
                        return;
                    }
                    String allowed = "admin,chef,delivery";
                    if (req.getRole() == null || !allowed.contains(req.getRole())) {
                        response.setStatus(400);
                        out.write("{\"error\":\"Invalid role\"}");
                        return;
                    }
                    String hashed  = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
                    boolean created = userDao.registerStaff(
                        req.getName()  != null ? req.getName()  : "",
                        req.getEmail(),
                        hashed,
                        req.getPhone() != null ? req.getPhone() : "",
                        req.getRole()
                    );
                    if (created) {
                        out.write("{\"message\":\"Staff account created successfully\"}");
                    } else {
                        response.setStatus(409);
                        out.write("{\"error\":\"An account with this email already exists\"}");
                    }
                }
                case "deleteUser" -> {
                    adminDao.deleteUser(req.getUserId());
                    out.write("{\"message\":\"User deleted\"}");
                }
                default -> {
                    response.setStatus(400);
                    out.write("{\"error\":\"Unknown action\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.write("{\"error\":\"Action failed\"}");
        }
    }

    static class AdminReq {
        private String action;
        private int    orderId;
        private int    staffId;
        private int    itemId;
        private int    userId;
        private String name;
        private String description;
        private double price;
        private int    categoryId;
        private String email;
        private String password;
        private String phone;
        private String role;

        public String getAction()      { return action; }
        public int    getOrderId()     { return orderId; }
        public int    getStaffId()     { return staffId; }
        public int    getItemId()      { return itemId; }
        public int    getUserId()      { return userId; }
        public String getName()        { return name; }
        public String getDescription() { return description; }
        public double getPrice()       { return price; }
        public int    getCategoryId()  { return categoryId; }
        public String getEmail()       { return email; }
        public String getPassword()    { return password; }
        public String getPhone()       { return phone; }
        public String getRole()        { return role; }
    }
}
