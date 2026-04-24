/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import db.DBConnection;
import model.User;

import java.sql.*;

public class UserDAO {
    public User findByEmail(String email) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try(Connection con =DBConnection.get();
            PreparedStatement ps = con.prepareStatement("select id, name, email, password, phone, role, balance from users where email=?");){
                
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            
            if(rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setBalance(rs.getDouble("balance"));
                
                return user;
            }
            return null;
        }
        
    }
    
    public boolean register(String name, String email, String password, String phone) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        if(findByEmail(email) != null) return false;
        
        String sql = "INSERT INTO users (name, email, password, phone, role) VALUES (?, ?, ?, ?, 'customer')";
        try(Connection con =DBConnection.get();
            PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, phone);

            return ps.executeUpdate() == 1;

        }
    }
    
}
