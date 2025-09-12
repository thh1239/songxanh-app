package com.example.songxanh.data.models;

public class Admin extends User{
    public Admin(String uid, String email, String name, String imageUrl, String type) {
        super(uid, email, name, imageUrl, "ADMIN");
    }
}
