package com.posapp.model;

public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private int discount; // VIP discount percentage (0 = no discount)

    public Customer() {
    }

    public Customer(int customerId, String name, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        if (discount > 0) {
            return name + " (" + phone + ") VIP " + discount + "% off";
        }
        return name + " (" + phone + ")";
    }
}
