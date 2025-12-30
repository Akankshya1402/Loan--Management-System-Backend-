package com.lms.auth.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("user1", Set.of("CUSTOMER"));

        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // JWT signature format
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtUtil.generateToken("user1", Set.of("CUSTOMER"));

        String username = jwtUtil.extractUsername(token);

        assertEquals("user1", username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        String token = jwtUtil.generateToken("user1", Set.of("CUSTOMER"));

        assertTrue(jwtUtil.isTokenValid(token));
    }
}
