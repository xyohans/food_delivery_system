package DAO;

import db.DBConnection;
import model.User;
import java.sql.*;

public class UserDAO {

    public User findByEmail(String email) throws SQLException, ClassNotFoundException {
        if (email == null) return null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String sql = "SELECT id, name, email, password, phone, role, balance FROM users WHERE email = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setBalance(rs.getDouble("balance"));
                return user;
            }
            return null;
        }
    }

    public String getEmailById(int userId) throws SQLException {
        String sql = "SELECT email FROM users WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("email");
        }
        return null;
    }

    public double getBalance(int userId) throws SQLException {
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        }
        return 0.0;
    }

    // Register a customer (called from RegisterServlet)
    public boolean register(String name, String email, String password, String phone)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        if (findByEmail(email) != null) return false;
        String sql = "INSERT INTO users (name, email, password, phone, role, balance) VALUES (?,?,?,?,'customer',100.00)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, email);
            ps.setString(3, password); ps.setString(4, phone);
            return ps.executeUpdate() == 1;
        }
    }

    // Register staff (admin, chef, delivery) — called from AdminServlet
    public boolean registerStaff(String name, String email, String password, String phone, String role)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        if (findByEmail(email) != null) return false;
        String sql = "INSERT INTO users (name, email, password, phone, role, balance, is_free) VALUES (?,?,?,?,?,0.00,1)";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, email);
            ps.setString(3, password); ps.setString(4, phone);
            ps.setString(5, role);
            return ps.executeUpdate() == 1;
        }
    }

    public void deductBalance(int userId, double amount) throws SQLException {
        String sql = "UPDATE users SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount); ps.setInt(2, userId); ps.setDouble(3, amount);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insufficient funds or user not found");
        }
    }

    public void updateProfile(int userId, String name, String phone) throws SQLException {
        String sql = "UPDATE users SET name = ?, phone = ? WHERE id = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name); ps.setString(2, phone); ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void deleteAccount(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }

    public void refundBalance(int userId, double amount) throws SQLException {
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount); ps.setInt(2, userId); ps.executeUpdate();
        }
    }
}
