package com.jemimah.glamorous_you.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Business implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("service")
    @Expose
    private Service service;
    @SerializedName("owner")
    @Expose
    private User owner;
    @SerializedName("county")
    @Expose
    private County county;
    @SerializedName("sub_county")
    @Expose
    private SubCounty subCounty;
    @SerializedName("business_services")
    @Expose
    private List<BusinessService> businessServices;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public SubCounty getSubCounty() {
        return subCounty;
    }

    public void setSubCounty(SubCounty subCounty) {
        this.subCounty = subCounty;
    }

    public List<BusinessService> getBusinessServices() {
        return businessServices;
    }

    public void setBusinessServices(List<BusinessService> businessServices) {
        this.businessServices = businessServices;
    }
}
