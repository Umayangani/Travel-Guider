package com.travelguider.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private String token;

    public AuthResponse() {
        // default constructor for serialization
    }

    public AuthResponse(String token) {
        this.token = token;
    }

}
