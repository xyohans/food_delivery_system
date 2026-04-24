/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author yohan
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    private double balance;
    
    //getting the values
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getName(){ return name; }
    public String getPassword(){ return password; }
    public String getPhone(){ return phone; }
    public String getRole(){ return role; }
    public double getBalance(){ return balance; }
    
    //setting values
    public void setId(int id)            { this.id = id; }
    public void setName(String name)     { this.name = name; }
    public void setEmail(String email)   { this.email = email; }
    public void setPassword(String p)    { this.password = p; }
    public void setRole(String role)     { this.role = role; }
    public void setBalance(double b)     { this.balance = b; }
}
