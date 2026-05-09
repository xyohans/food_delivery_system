package model.request;

import java.util.List;

public class OrderReq {

    private List<ItemReq> items;
    private int address_id;
    private String payment_method;
    private String notes;

    // getters & setters

    public List<ItemReq> getItems() {
        return items;
    }

    public void setItems(List<ItemReq> items) {
        this.items = items;
    }

    public int getAddressId() {
        return address_id;
    }

    public void setAddressId(int address_id) {
        this.address_id = address_id;
    }

    public String getPaymentMethod() {
        return payment_method;
    }

    public void setPaymentMethod(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}