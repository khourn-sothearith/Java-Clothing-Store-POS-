package com.posapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Sale {
    private int saleId;
    private String invoiceNo;
    private Timestamp saleDate;
    private Integer customerId;
    private String customerName;
    private int employeeId;
    private BigDecimal grandTotal;
    private BigDecimal amountPaid;
    private BigDecimal changeDue;
    private String paymentMethod;

    public Sale() {
    }

    public Sale(int saleId, String invoiceNo, Timestamp saleDate, Integer customerId,
                String customerName, int employeeId, BigDecimal grandTotal,
                BigDecimal amountPaid, BigDecimal changeDue, String paymentMethod) {
        this.saleId = saleId;
        this.invoiceNo = invoiceNo;
        this.saleDate = saleDate;
        this.customerId = customerId;
        this.customerName = customerName;
        this.employeeId = employeeId;
        this.grandTotal = grandTotal;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentMethod = paymentMethod;
    }

    public int getSaleId() {
        return saleId;
    }
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }
    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Timestamp getSaleDate() {
        return saleDate;
    }
    public void setSaleDate(Timestamp saleDate) {
        this.saleDate = saleDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }
    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }
    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getChangeDue() {
        return changeDue;
    }
    public void setChangeDue(BigDecimal changeDue) {
        this.changeDue = changeDue;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
