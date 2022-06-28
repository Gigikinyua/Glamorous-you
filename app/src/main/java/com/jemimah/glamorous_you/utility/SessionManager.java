package com.jemimah.glamorous_you.utility;

import com.jemimah.glamorous_you.model.User;

public enum SessionManager {

    INSTANCE;

    private User user;
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

