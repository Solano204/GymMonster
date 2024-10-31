package com.monster.gateway.Entities;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public  class Token {

    private String username;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private Instant refreshExpiresAt;

}