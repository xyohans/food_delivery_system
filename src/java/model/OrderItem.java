package model;

public class OrderItem {
    private int    id;
    private int    orderId;
    private int    itemId;
    private int    quantity;
    private double unitPrice;

    // This comes from joining with menu_items table
    // so we can show the item name without a second query
    private String itemName;

    public int    getId()        { return id; }
    public int    getOrderId()   { return orderId; }
    public int    getItemId()    { return itemId; }
    public int    getQuantity()  { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getItemName()  { return itemName; }

    public void setId(int id)               { this.id = id; }
    public void setOrderId(int orderId)     { this.orderId = orderId; }
    public void setItemId(int itemId)       { this.itemId = itemId; }
    public void setQuantity(int quantity)   { this.quantity = quantity; }
    public void setUnitPrice(double price)  { this.unitPrice = price; }
    public void setItemName(String name)    { this.itemName = name; }

//    // Helper method — total price for this line
//    // quantity x unitPrice
//    public double getLineTotal() {
//        return quantity * unitPrice;
//    }
}