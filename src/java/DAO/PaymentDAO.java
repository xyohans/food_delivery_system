package DAO;

import db.DBConnection;
import java.sql.*;

public class PaymentDAO {

    // Insert payment record right after order is placed
    // cash → status is 'pending' (they pay on delivery)
    // card or wallet → status is 'completed' (paid now)
    public void insertPayment(int orderId, String method, double amount)
            throws SQLException {

        String status = method.equals("cash") ? "pending" : "completed";

        String sql = """
                INSERT INTO payments (order_id, method, amount, status, paid_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, method);
            ps.setDouble(3, amount);
            ps.setString(4, status);
            // Set paid_at only for non-cash payments
            if (!method.equals("cash")) {
                ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }
            ps.executeUpdate();
        }
    }

    // Also update order status to 'paid' for card/wallet
    public void markOrderPaid(int orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'paid' WHERE id = ? AND status = 'pending'";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    // Complete a cash payment when delivery guy delivers
    public void completePayment(int orderId) throws SQLException {
        String sql = "UPDATE payments SET status = 'completed', paid_at = NOW() WHERE order_id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
}
