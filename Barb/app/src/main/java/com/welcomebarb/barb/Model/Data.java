package com.welcomebarb.barb.Model;

public class Data {
    public String title, location, backgroundImage, city, pincode, state, availableBed, phone,patientProfile,patientName,hospitalName,patientStatus,patientAddress,patientAge,patientSymptoms,patientAadhar,hosId;

    public Data() {
    }

    public Data(String title, String location, String backgroundImage, String city, String pincode, String state, String availableBed, String phone, String patientProfile, String patientName, String hospitalName, String patientStatus, String patientAddress, String patientAge, String patientSymptoms, String patientAadhar, String hosId) {
        this.title = title;
        this.location = location;
        this.backgroundImage = backgroundImage;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.availableBed = availableBed;
        this.phone = phone;
        this.patientProfile = patientProfile;
        this.patientName = patientName;
        this.hospitalName = hospitalName;
        this.patientStatus = patientStatus;
        this.patientAddress = patientAddress;
        this.patientAge = patientAge;
        this.patientSymptoms = patientSymptoms;
        this.patientAadhar = patientAadhar;
        this.hosId=hosId;
    }

    public String getHosId() {
        return hosId;
    }

    public void setHosId(String hosId) {
        this.hosId = hosId;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientSymptoms() {
        return patientSymptoms;
    }

    public void setPatientSymptoms(String patientSymptoms) {
        this.patientSymptoms = patientSymptoms;
    }

    public String getPatientAadhar() {
        return patientAadhar;
    }

    public void setPatientAadhar(String patientAadhar) {
        this.patientAadhar = patientAadhar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAvailableBed() {
        return availableBed;
    }

    public String getPatientProfile() {
        return patientProfile;
    }

    public void setPatientProfile(String patientProfile) {
        this.patientProfile = patientProfile;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }



    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public void setAvailableBed(String availableBed) {
        this.availableBed = availableBed;
    }
}
