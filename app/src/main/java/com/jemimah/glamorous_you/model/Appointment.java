package com.jemimah.glamorous_you.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Appointment implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userID")
    @Expose
    private Integer userID;
    @SerializedName("booked_by")
    @Expose
    private User booked_by;
    @SerializedName("business_id")
    @Expose
    private Integer business_id;
    @SerializedName("services")
    @Expose
    private List<BusinessService> services;
    @SerializedName("no_of_adults")
    @Expose
    private int no_of_adults;
    @SerializedName("no_of_children")
    @Expose
    private int no_of_children;
    @SerializedName("appointment_date")
    @Expose
    private String appointment_date;
    @SerializedName("appointment_time")
    @Expose
    private String appointment_time;
    @SerializedName("business")
    @Expose
    private Business business;
    @SerializedName("service")
    @Expose
    private Service service;
    @SerializedName("payment_type")
    @Expose
    private String payment_type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("review")
    @Expose
    private String review;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public User getBooked_by() {
        return booked_by;
    }

    public void setBooked_by(User booked_by) {
        this.booked_by = booked_by;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(Integer business_id) {
        this.business_id = business_id;
    }

    public List<BusinessService> getServices() {
        return services;
    }

    public void setServices(List<BusinessService> services) {
        this.services = services;
    }

    public int getNo_of_adults() {
        return no_of_adults;
    }

    public void setNo_of_adults(int no_of_adults) {
        this.no_of_adults = no_of_adults;
    }

    public int getNo_of_children() {
        return no_of_children;
    }

    public void setNo_of_children(int no_of_children) {
        this.no_of_children = no_of_children;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
