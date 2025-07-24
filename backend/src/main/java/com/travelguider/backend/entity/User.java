package com.travelguider.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private LocalDate dob;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String avatar;

    // Getters and setters


    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setRole(Role role) {
        this.role = role;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
