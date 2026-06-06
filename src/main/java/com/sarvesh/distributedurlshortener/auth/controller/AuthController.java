package com.sarvesh.distributedurlshortener.auth.controller;

import com.sarvesh.distributedurlshortener.auth.dto.AuthResponse;
import com.sarvesh.distributedurlshortener.auth.dto.LoginRequest;
import com.sarvesh.distributedurlshortener.auth.dto.RegisterRequest;
import com.sarvesh.distributedurlshortener.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        return authService.register(
                request
        );
    }
    @PostMapping("/login")
    public AuthResponse login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        return authService.login(
                request
        );
    }
}