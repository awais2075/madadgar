package com.madadgar.model;

import com.madadgar.enums.RequestType;

import java.io.Serializable;
import java.util.Date;

public class Blood implements Serializable {

    private String bloodId;
    private String bloodRequestType;
    private String bloodRequestLocation;
    private Date bloodRequestDate;
    private int quantity;
    private String userName;

    public Blood() {
    }

    public Blood(String bloodId, String bloodRequestType, String bloodRequestLocation, Date bloodRequestDate, int quantity, String userName) {
        this.bloodId = bloodId;
        this.bloodRequestType = bloodRequestType;
        this.bloodRequestLocation = bloodRequestLocation;
        this.bloodRequestDate = bloodRequestDate;
        this.quantity = quantity;
        this.userName = userName;
    }

    public String getBloodId() {
        return bloodId;
    }

    public void setBloodId(String bloodId) {
        this.bloodId = bloodId;
    }

    public String getBloodRequestType() {
        return bloodRequestType;
    }

    public void setBloodRequestType(String bloodRequestType) {
        this.bloodRequestType = bloodRequestType;
    }

    public String getBloodRequestLocation() {
        return bloodRequestLocation;
    }

    public void setBloodRequestLocation(String bloodRequestLocation) {
        this.bloodRequestLocation = bloodRequestLocation;
    }

    public Date getBloodRequestDate() {
        return bloodRequestDate;
    }

    public void setBloodRequestDate(Date bloodRequestDate) {
        this.bloodRequestDate = bloodRequestDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
