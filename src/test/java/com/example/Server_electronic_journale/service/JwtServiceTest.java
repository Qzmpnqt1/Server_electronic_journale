package com.example.Server_electronic_journale.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails user;

    @BeforeEach
    void setUp() {
        // Устанавливаем секрет
        String key = "veryVerySecretKey_32chars_min_len!!";
        String encoded = Base64.getEncoder().encodeToString(key.getBytes());
        ReflectionTestUtils.setField(jwtService, "jwtSecret", encoded);

        when(user.getUsername()).thenReturn("user@mail.com");
    }

    @Test
    void generateAndValidate() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertEquals("user@mail.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }
}
