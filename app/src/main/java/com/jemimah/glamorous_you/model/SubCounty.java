package com.jemimah.glamorous_you.model;

import java.io.Serializable;

public class SubCounty  implements Serializable {
    private Integer id;
    private String name;
    private int county_code;

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

    public int getCounty_code() {
        return county_code;
    }

    public void setCounty_code(int county_code) {
        this.county_code = county_code;
    }
}
