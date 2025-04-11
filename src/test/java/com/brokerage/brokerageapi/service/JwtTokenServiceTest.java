package com.brokerage.brokerageapi.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtService;

    @Test
    public void shouldGenerateAndValidateTokenSuccessfully() {
        UserDetails user = User.withUsername("testuser")
                               .password("testpass")
                               .roles("USER")
                               .build();

        String token = jwtService.generateToken(user);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("testuser", extractedUsername);
    }
}

