package org.bank.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.bank.userservice.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;

        log.info("SecurityConfig initialized");
    }

    @Bean
    public SecurityFilterChain getContext(HttpSecurity http) throws Exception {

        log.info("Configuring UserService security filter chain");

        SecurityFilterChain filterChain = http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasAnyRole("USER", "SERVICE")
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();

        log.info("UserService security filter chain configured successfully");

        return filterChain;
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        log.debug("Creating UserService AuthenticationManager");

        return authentication -> authentication;
    }
}