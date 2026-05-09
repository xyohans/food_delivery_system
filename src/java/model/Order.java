package model;

import java.sql.Timestamp;

public class Order {
    private int       id;
    private int       customerId;
    private int       chefId;
    private int       deliveryId;
    private int       addressId;
    private String    status;
    private String    deliveryAddress;
    private double    totalAmount;
    private String    notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;


    public int       getId()              { return id; }
    public int       getCustomerId()      { return customerId; }
    public int       getChefId()          { return chefId; }
    public int       getDeliveryId()      { return deliveryId; }
    public int       getAddressId()       { return addressId; }
    public String    getStatus()          { return status; }
    public String    getDeliveryAddress() { return deliveryAddress; }
    public double    getTotalAmount()     { return totalAmount; }
    public String    getNotes()           { return notes; }
    public Timestamp getCreatedAt()       {return createdAt;}
    public Timestamp getUpdatedAt()       {return updatedAt;}


    public void setId(int id)                        { this.id = id; }
    public void setCustomerId(int customerId)        { this.customerId = customerId; }
    public void setChefId(int chefId)                { this.chefId = chefId; }
    public void setDeliveryId(int deliveryId)        { this.deliveryId = deliveryId; }
    public void setAddressId(int addressId)          { this.addressId = addressId; }
    public void setStatus(String status)             { this.status = status; }
    public void setDeliveryAddress(String address)   { this.deliveryAddress = address; }
    public void setTotalAmount(double totalAmount)   { this.totalAmount = totalAmount; }
    public void setNotes(String notes)               { this.notes = notes; }
    public void setCreatedAt(Timestamp createdAt)    {this.createdAt = createdAt;}
    public void setUpdatedAt(Timestamp updatedAt)    {this.updatedAt = updatedAt;}

}