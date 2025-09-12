package com.example.songxanh.data.models;

public class User {
    protected String uid;
    protected String email;
    protected String name;
    protected String imageUrl;
    protected String type; // { "NORMAL_USER", "ADMIN" }

    public User() {}
    public User(String uid, String email, String name, String imageUrl, String type) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
