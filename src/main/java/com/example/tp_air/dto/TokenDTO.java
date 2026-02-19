package com.example.tp_air.dto;

public class TokenDTO {
    private final String token;
    private final int expiresIn;

    public TokenDTO(String token, int expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public int getExpiresIn() { return expiresIn; }
}