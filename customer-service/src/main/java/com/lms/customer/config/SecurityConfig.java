package com.lms.customer.config;

import com.lms.customer.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final String INTERNAL_SERVICE_TOKEN =
            "Bearer LMS_INTERNAL_SERVICE_TOKEN";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // CORS / actuator
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // ✅ INTERNAL PAYMENT → CUSTOMER CALL
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/customers/*/emi-liability"
                        ).permitAll()

                        // 🔐 everything else
                        .anyRequest().authenticated()
                )

                // ✅ INTERNAL SERVICE FILTER (FIRST)
                .addFilterBefore(
                        internalServiceFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )

                // 🔐 USER JWT FILTER
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // ✅ INTERNAL SERVICE AUTH
    @Bean
    public OncePerRequestFilter internalServiceFilter() {

        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain
            ) throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");

                if (INTERNAL_SERVICE_TOKEN.equals(authHeader)) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    "internal-service",
                                    null,
                                    List.of(
                                            new SimpleGrantedAuthority("ROLE_INTERNAL")
                                    )
                            );

                    SecurityContextHolder.getContext()
                            .setAuthentication(auth);
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}
