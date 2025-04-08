package com.example.ordersystem.payload.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
}
