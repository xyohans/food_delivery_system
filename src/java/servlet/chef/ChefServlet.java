package servlet.chef;

import DAO.OrderDAO;
import db.DBConnection;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet(name = "ChefServlet", urlPatterns = {"/ChefServlet"})
public class ChefServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Not logged in\"}");
            return;
        }
        int chefId = (int) session.getAttribute("userId");

        List<Map<String, Object>> orders = new ArrayList<>();
        // Include order items in the response so chef can see what to cook
        String sql = """
            SELECT o.id, o.delivery_address, o.notes,
                   GROUP_CONCAT(CONCAT(oi.quantity,'x ',m.name) SEPARATOR ', ') AS items
            FROM orders o
            LEFT JOIN order_items oi ON oi.order_id = o.id
            LEFT JOIN menu_items  m  ON m.id = oi.item_id
            WHERE o.chef_id = ? AND o.status = 'preparing'
            GROUP BY o.id
            """;

        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chefId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",      rs.getInt("id"));
                map.put("address", rs.getString("delivery_address"));
                map.put("notes",   rs.getString("notes"));
                map.put("items",   rs.getString("items"));
                orders.add(map);
            }
            response.getWriter().write(new Gson().toJson(orders));
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Could not load orders\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Not logged in\"}");
            return;
        }

        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"orderId required\"}");
            return;
        }

        int orderId = Integer.parseInt(orderIdParam);
        OrderDAO dao = new OrderDAO();
        try {
            dao.updateOrderStatus(orderId, "ready");
            response.getWriter().write("{\"message\":\"Order marked as ready for delivery!\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Failed to update order\"}");
        }
    }
}
