package DAO;

import db.DBConnection;
import model.MenuItems;
import model.MenuCategory;

import java.sql.*;
import java.util.*;

public class MenuDAO {

    // Get all categories
    public List<MenuCategory> getCategories() throws SQLException, ClassNotFoundException {
        List<MenuCategory> list = new ArrayList<>();
        String sql = "SELECT id, name FROM menu_categories";
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MenuCategory c = new MenuCategory();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        }
        return list;
    }

    // Get all available menu items
    public List<MenuItems> getAvailableItems() throws SQLException, ClassNotFoundException {
        List<MenuItems> list = new ArrayList<>();
        String sql = """
                SELECT m.id, m.name, m.description, m.price, m.is_available,
                       c.name AS category_name
                FROM menu_items m
                LEFT JOIN menu_categories c ON m.category_id = c.id
                WHERE m.is_available = 1
                ORDER BY c.name, m.name
                """;

        Class.forName("com.mysql.cj.jdbc.Driver");
        
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MenuItems item = new MenuItems();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getDouble("price"));
                item.setCategoryName(rs.getString("category_name"));
                list.add(item);
            }
        }
        return list;
    }

    // Admin: add a new item
    public void addItem(String name, String desc, double price, int categoryId)
            throws SQLException {
        String sql = "INSERT INTO menu_items (name, description, price, category_id) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setInt(4, categoryId);
            ps.executeUpdate();
        }
    }

    // Admin: toggle item availability
    public void toggleAvailability(int itemId) throws SQLException {
        String sql = "UPDATE menu_items SET is_available = NOT is_available WHERE id = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }
}