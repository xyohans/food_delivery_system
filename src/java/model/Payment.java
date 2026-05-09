package model;

public class Payment {
    private int    id;
    private int    orderId;
    private String method;
    private double amount;
    private String status;

    public int    getId()      { return id; }
    public int    getOrderId() { return orderId; }
    public String getMethod()  { return method; }
    public double getAmount()  { return amount; }
    public String getStatus()  { return status; }

    public void setId(int id)            { this.id = id; }
    public void setOrderId(int orderId)  { this.orderId = orderId; }
    public void setMethod(String method) { this.method = method; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}