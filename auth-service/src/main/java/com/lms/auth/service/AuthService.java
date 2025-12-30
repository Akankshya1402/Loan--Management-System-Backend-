package com.lms.auth.service;

import com.lms.auth.dto.LoginRequest;
import com.lms.auth.dto.RegisterRequest;
import com.lms.auth.exception.InvalidCredentialsException;
import com.lms.auth.exception.UserAlreadyExistsException;
import com.lms.auth.model.Role;
import com.lms.auth.model.User;
import com.lms.auth.repository.UserRepository;
import com.lms.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // =========================
    // REGISTER
    // =========================
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(Role.CUSTOMER))
                .build();

        userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================
    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}
