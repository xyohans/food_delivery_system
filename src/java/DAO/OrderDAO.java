package DAO;

import db.DBConnection;
import model.Order;
//import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.request.ItemReq;

public class OrderDAO {

    public int placeOrder(int customerId, int addressId, String deliveryAddress, double total, String notes) throws SQLException, ClassNotFoundException {

        String sql = "INSERT INTO orders (customer_id, address_id, delivery_address, total_amount, notes, status)VALUES (?, ?, ?, ?, ?, 'pending')";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DBConnection.get();) {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, customerId);
            ps.setInt(2, addressId);
            ps.setString(3, deliveryAddress);
            ps.setDouble(4, total);
            ps.setString(5, notes);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);

        } catch (Exception ex) {

            System.out.println("error:" + ex);
        }
        return 0;
    }

    public void insertOrderItems(int orderId, List<ItemReq> items)
            throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, unit_price) VALUES (?,?,?,?)";

        MenuDAO menuDAO = new MenuDAO();
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DBConnection.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (ItemReq item : items) {
                int itemId = item.getItemId();
                int quantity = item.getQuantity();

                double price = menuDAO.getItemPrice(itemId);
                ps.setInt(1, orderId);
                ps.setInt(2, itemId);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                ps.addBatch();

            }
            ps.executeBatch();
        }
    }

    public List<Order> getOrdersByCustomer(int customerId) throws SQLException, ClassNotFoundException {
        List<Order> list = new ArrayList<>();
        String sql = """
                    SELECT id, status, total_amount, delivery_address
                    FROM orders
                    WHERE customer_id = ?
                    ORDER BY created_at DESC
                    """;

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DBConnection.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setStatus(rs.getString("status"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setDeliveryAddress(rs.getString("delivery_address"));
                list.add(o);
            }
        }
        return list;
    }

    public void updateOrderStatus(int orderId, String status) throws SQLException, ClassNotFoundException {
        String query = "UPDATE orders SET status = ?, updated_at = NOW() WHERE id = ?";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection con = DBConnection.get();) {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, status);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        } catch (Exception ex) {
            System.out.println("error:" + ex);
        }
    }
    // Add to OrderDAO.java

    public Order getOrderById(int orderId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT id, status, total_amount FROM orders WHERE id = ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DBConnection.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setStatus(rs.getString("status"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                return o;
            }
        }
        return null;
    }

    public boolean cancelOrder(int orderId) throws SQLException, ClassNotFoundException {
        // Only allow cancellation if status is 'pending' or 'paid'
        String sql = "UPDATE orders SET status = 'cancelled' WHERE id = ? AND (status = 'pending' OR status = 'paid')";
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DBConnection.get(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        }
    }
}
