package DAO;

import model.Address;
import db.DBConnection;
import java.sql.*;
import java.util.*;

public class AddressDAO {

    // Get all addresses for one customer
    public List<Address> getAddressesByUser(int userId) throws SQLException {
        List<Address> list = new ArrayList<>();
        String sql = """
                SELECT id, user_id, label, address, is_default
                FROM addresses
                WHERE user_id = ?
                ORDER BY is_default DESC
                """;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Address a = new Address();
                a.setID(rs.getInt("id"));
                a.setUserID(rs.getInt("user_id"));
                a.setLabel(rs.getString("label"));
                a.setAddress(rs.getString("address"));
                a.setIsDefault(rs.getBoolean("is_default"));
                list.add(a);
            }
        }
        return list;
    }

    // Get one address by its id
    // Used to snapshot the address text when placing an order
    public Address getAddressById(int addressId) throws SQLException {
        String sql = "SELECT id, user_id, label, address, is_default FROM addresses WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Address a = new Address();
                a.setID(rs.getInt("id"));
                a.setUserID(rs.getInt("user_id"));
                a.setLabel(rs.getString("label"));
                a.setAddress(rs.getString("address"));
                a.setIsDefault(rs.getBoolean("is_default"));
                return a;
            }
        }
        return null;
    }

    // Add a new address
    public void addAddress(int userId, String label, String address, int isDefault)
            throws SQLException {

        // if this is set as default remove default from others first
        if (isDefault == 1) {
            removeDefault(userId);
        }

        String sql = """
                INSERT INTO addresses (user_id, label, address, is_default)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, label);
            ps.setString(3, address);
            ps.setInt(4, isDefault);
            ps.executeUpdate();
        }
    }

    // Delete an address
    public void deleteAddress(int addressId) throws SQLException {
        String sql = "DELETE FROM addresses WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.executeUpdate();
        }
    }

    // Remove default from all addresses for this user
    // Called before setting a new default
    private void removeDefault(int userId) throws SQLException {
        String sql = "UPDATE addresses SET is_default = 0 WHERE user_id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}