package DAO;

import db.DBConnection;
import java.sql.*;
import java.util.*;
import model.User;
import model.Order;
import model.MenuItems;

public class AdminDAO {

    public List<Order> getOrders(String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql;
        if (status == null || status.equals("all")) {
            sql = "SELECT id, customer_id, total_amount, status, delivery_address, created_at FROM orders ORDER BY created_at DESC";
        } else {
            sql = "SELECT id, customer_id, total_amount, status, delivery_address, created_at FROM orders WHERE status = ? ORDER BY created_at DESC";
        }
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null && !status.equals("all")) ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setCustomerId(rs.getInt("customer_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setDeliveryAddress(rs.getString("delivery_address"));
                o.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(o);
            }
        }
        return list;
    }

    public void assignChef(int orderId, int chefId) throws SQLException {
        String updateOrder = "UPDATE orders SET chef_id = ?, status = 'preparing' WHERE id = ?";
        String updateChef  = "UPDATE users SET is_free = 0 WHERE id = ?";
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateOrder);
                 PreparedStatement ps2 = conn.prepareStatement(updateChef)) {
                ps1.setInt(1, chefId); ps1.setInt(2, orderId); ps1.executeUpdate();
                ps2.setInt(1, chefId); ps2.executeUpdate();
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }

    public void assignDelivery(int orderId, int deliveryId) throws SQLException {
        String updateOrder  = "UPDATE orders SET delivery_id = ?, status = 'delivering' WHERE id = ?";
        String updateDriver = "UPDATE users SET is_free = 0 WHERE id = ?";
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(updateOrder);
                 PreparedStatement ps2 = conn.prepareStatement(updateDriver)) {
                ps1.setInt(1, deliveryId); ps1.setInt(2, orderId); ps1.executeUpdate();
                ps2.setInt(1, deliveryId); ps2.executeUpdate();
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }

    public void refundOrder(int orderId) throws SQLException {
        String getOrder      = "SELECT customer_id, total_amount FROM orders WHERE id = ?";
        String addBalance    = "UPDATE users SET balance = balance + ? WHERE id = ?";
        String cancelOrder   = "UPDATE orders SET status = 'cancelled' WHERE id = ?";
        String updatePayment = "UPDATE payments SET status = 'refunded' WHERE order_id = ?";
        try (Connection conn = DBConnection.get()) {
            conn.setAutoCommit(false);
            try {
                int customerId; double total;
                try (PreparedStatement ps = conn.prepareStatement(getOrder)) {
                    ps.setInt(1, orderId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) throw new SQLException("Order not found");
                    customerId = rs.getInt("customer_id");
                    total      = rs.getDouble("total_amount");
                }
                try (PreparedStatement ps = conn.prepareStatement(addBalance)) {
                    ps.setDouble(1, total); ps.setInt(2, customerId); ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(cancelOrder)) {
                    ps.setInt(1, orderId); ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(updatePayment)) {
                    ps.setInt(1, orderId); ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) { conn.rollback(); throw e; }
        }
    }

    public List<User> getAvailableStaff(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name FROM users WHERE role = ? AND is_free = 1";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                list.add(u);
            }
        }
        return list;
    }

    public List<MenuItems> getAllMenuItems() throws SQLException {
        List<MenuItems> list = new ArrayList<>();
        String sql = """
                SELECT m.id, m.name, m.description, m.price, m.is_available,
                       c.name AS category_name
                FROM menu_items m
                LEFT JOIN menu_categories c ON m.category_id = c.id
                ORDER BY c.name, m.name
                """;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MenuItems item = new MenuItems();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setAvailable(rs.getInt("is_available"));
                item.setCategoryName(rs.getString("category_name"));
                list.add(item);
            }
        }
        return list;
    }

    public void addMenuItem(String name, String description, double price, int categoryId)
            throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category_id, is_available) VALUES (?,?,?,?,1)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, description);
            ps.setDouble(3, price); ps.setInt(4, categoryId);
            ps.executeUpdate();
        }
    }

    public void toggleMenuItem(int itemId) throws SQLException {
        String sql = "UPDATE menu_items SET is_available = NOT is_available WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId); ps.executeUpdate();
        }
    }

    public void updateItemPrice(int itemId, double newPrice) throws SQLException {
        String sql = "UPDATE menu_items SET price = ? WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice); ps.setInt(2, itemId); ps.executeUpdate();
        }
    }

    // ── Staff management ─────────────────────────────────────────────────────

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, role FROM users ORDER BY role, name";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        }
        return list;
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }
}
