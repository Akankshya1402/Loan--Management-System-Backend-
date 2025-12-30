package com.lms.auth.controller;

import com.lms.auth.dto.AuthResponse;
import com.lms.auth.dto.LoginRequest;
import com.lms.auth.dto.RegisterRequest;
import com.lms.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

