package com.example.carrentalapp.Model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity
public class Billing {

    @PrimaryKey
    private int billingID;

    private String billingStatus;
    private Calendar billingDate;

    private double lateFees;

    @ForeignKey(entity = Payment.class,
                parentColumns = "parentClassColumn",
                childColumns = "childClassColumn",
                onDelete = ForeignKey.SET_NULL)
    private int paymentID;


    public Billing(int billingID, String billingStatus, Calendar billingDate, double lateFees, int paymentID) {
        this.billingID = billingID;
        this.billingStatus = billingStatus;
        this.billingDate = billingDate;
        this.lateFees = lateFees;
        this.paymentID = paymentID;
    }

    public int getBillingID() {
        return billingID;
    }

    public void setBillingID(int billingID) {
        this.billingID = billingID;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public Calendar getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(Calendar billingDate) {
        this.billingDate = billingDate;
    }

    public double getLateFees() {
        return lateFees;
    }

    public void setLateFees(double lateFees) {
        this.lateFees = lateFees;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }
}
