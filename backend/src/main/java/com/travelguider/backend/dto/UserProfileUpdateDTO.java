package com.travelguider.backend.dto;

import java.time.LocalDate;

public class UserProfileUpdateDTO {
    private String name;
    private String email;
    private LocalDate dob;
    private String address;
    private String avatar;

    // Default constructor
    public UserProfileUpdateDTO() {}

    // Constructor with parameters
    public UserProfileUpdateDTO(String name, String email, LocalDate dob, String address, String avatar) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.address = address;
        this.avatar = avatar;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
