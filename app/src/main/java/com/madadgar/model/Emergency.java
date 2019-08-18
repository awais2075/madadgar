package com.madadgar.model;

import java.io.Serializable;
import java.util.Date;

public class Emergency implements Serializable {
    private String emergencyId;
    private String emergencyType;
    private String emergencyDetails;
    private String emergencyPhotoUrl;
    private String emergencyStatus;
    private String emergencyLocation;
    private String emergencyLocationAddress;
    private Date emergencyReportingTime;
    private String userId;
    public Emergency() {
    }

    public Emergency(String emergencyId, String emergencyType, String emergencyDetails, String emergencyPhotoUrl, String emergencyStatus, String emergencyLocation, String emergencyLocationAddress, Date emergencyReportingTime, String userId) {
        this.emergencyId = emergencyId;
        this.emergencyType = emergencyType;
        this.emergencyDetails = emergencyDetails;
        this.emergencyPhotoUrl = emergencyPhotoUrl;
        this.emergencyStatus = emergencyStatus;
        this.emergencyLocation = emergencyLocation;
        this.emergencyLocationAddress = emergencyLocationAddress;
        this.emergencyReportingTime = emergencyReportingTime;
        this.userId = userId;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getEmergencyDetails() {
        return emergencyDetails;
    }

    public void setEmergencyDetails(String emergencyDetails) {
        this.emergencyDetails = emergencyDetails;
    }

    public String getEmergencyPhotoUrl() {
        return emergencyPhotoUrl;
    }

    public void setEmergencyPhotoUrl(String emergencyPhotoUrl) {
        this.emergencyPhotoUrl = emergencyPhotoUrl;
    }

    public String getEmergencyStatus() {
        return emergencyStatus;
    }

    public void setEmergencyStatus(String emergencyStatus) {
        this.emergencyStatus = emergencyStatus;
    }

    public String getEmergencyLocation() {
        return emergencyLocation;
    }

    public void setEmergencyLocation(String emergencyLocation) {
        this.emergencyLocation = emergencyLocation;
    }

    public String getEmergencyLocationAddress() {
        return emergencyLocationAddress;
    }

    public void setEmergencyLocationAddress(String emergencyLocationAddress) {
        this.emergencyLocationAddress = emergencyLocationAddress;
    }

    public Date getEmergencyReportingTime() {
        return emergencyReportingTime;
    }

    public void setEmergencyReportingTime(Date emergencyReportingTime) {
        this.emergencyReportingTime = emergencyReportingTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
