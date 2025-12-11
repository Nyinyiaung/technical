package com.technical.controller;

import com.technical.config.MessageConfig;
import com.technical.config.jwt.service.JwtTokenService;
import com.technical.dto.SuccessResponse;
import com.technical.dto.request.ForgotPasswordRequest;
import com.technical.dto.request.LoginRequest;
import com.technical.dto.request.RegisterRequest;
import com.technical.dto.request.ResetPasswordRequest;
import com.technical.dto.response.LoginResponse;
import com.technical.service.auth.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8025", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authServiceImpl;
    private final JwtTokenService jwtTokenService;
    private final MessageConfig messageConfig;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authServiceImpl.registerUser(registerRequest);

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.registered"))
                .data(null).build(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authServiceImpl.loginUser(loginRequest);

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.login.success"))
                .data(loginResponse).build(), HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<SuccessResponse> verifyEmail(@RequestParam String email) {
        authServiceImpl.verifyEmail(email);

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.verified.email"))
                .data(null).build(), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authServiceImpl.initiatePasswordReset(request.getEmail());

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.sent.reset.email"))
                .data(null).build(), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authServiceImpl.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.reset.password.success"))
                .data(null).build(), HttpStatus.OK);
    }

    @PostMapping(value="/api/logout")
    public ResponseEntity<SuccessResponse> logout (@RequestParam String token) {
        jwtTokenService.invalidateJwtToken(token);

        return new ResponseEntity<>(SuccessResponse.builder()
                .message(messageConfig.getMessage("auth.logout.success"))
                .data(null).build(), HttpStatus.OK);
    }

}