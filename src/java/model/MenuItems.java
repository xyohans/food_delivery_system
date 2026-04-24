/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author yohan
 */
public class MenuItems {
    
    private int id;
    private String name;
    private String description;
    private double price;
    private boolean isAvailable;
    private String categoryName;
    
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return isAvailable; }
    public String getCategoryName() { return categoryName; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
}
