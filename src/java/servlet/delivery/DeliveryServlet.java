package servlet.delivery;

import DAO.OrderDAO;
import db.DBConnection;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet(name = "DeliveryServlet", urlPatterns = {"/DeliveryServlet"})
public class DeliveryServlet extends HttpServlet {

    /**
     * GET: Fetches orders assigned to the logged-in driver that are "ready" for pickup.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Integer driverId = (Integer) session.getAttribute("userId"); // Retrieved from Login session

        List<Map<String, Object>> orders = new ArrayList<>();
        // Query targets orders where this driver is assigned and the chef has finished cooking
        String sql = "SELECT id, delivery_address, total_amount FROM orders WHERE delivery_id = ? AND status = 'delivering'";

        try (Connection conn = DBConnection.get(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, driverId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("address", rs.getString("delivery_address"));
                map.put("total", rs.getDouble("total_amount"));
                orders.add(map);
            }
            response.getWriter().write(new Gson().toJson(orders));
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Could not load deliveries\"}");
        }
    }

    /**
     * POST: Marks an order as "delivered" and frees up the chef and driver for new tasks[cite: 4, 22].
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        HttpSession session = request.getSession();
        int driverId = (int) session.getAttribute("userId");

        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false); // Use transaction for data integrity[cite: 22]

            // 1. Update the order status to 'delivered'[cite: 4]
            String updateOrderSql = "UPDATE orders SET status = 'delivered' WHERE id = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(updateOrderSql)) {
                ps1.setInt(1, orderId);
                ps1.executeUpdate();
            }

            // 2. Set both the driver and the order's chef back to 'is_free = 1'[cite: 22]
            String updateStaffSql = "UPDATE users SET is_free = 1 WHERE id = ? " +
                                    "OR id = (SELECT chef_id FROM orders WHERE id = ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(updateStaffSql)) {
                ps2.setInt(1, driverId);
                ps2.setInt(2, orderId);
                ps2.executeUpdate();
            }

            conn.commit();
            response.getWriter().write("{\"message\":\"Delivery confirmed and staff freed\"}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Transaction failed\"}");
        }
    }
}