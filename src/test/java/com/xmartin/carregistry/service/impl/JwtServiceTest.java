package com.xmartin.carregistry.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    String jwtSecretKey;
    Long jwtExpirationMs;

    @BeforeEach
    void setUp() {
        jwtSecretKey = "mockedSecretKey";
        jwtExpirationMs = 10000L;
    }

    @Test
    void extractUserName() {

    }

    @Test
    void generateToken() {
    }

    @Test
    void isTokenValid() {
    }
}