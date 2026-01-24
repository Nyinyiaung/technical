package com.technical.controller;

import com.technical.commonutil.MasterCodeBase;
import com.technical.config.jwt.service.JwtTokenService;
import com.technical.dto.auth.request.ForgotPasswordRequest;
import com.technical.dto.auth.request.LoginRequest;
import com.technical.dto.auth.request.RegisterRequest;
import com.technical.dto.auth.request.ResetPasswordRequest;
import com.technical.dto.auth.response.LoginResponse;
import com.technical.dto.common.SuccessResponse;
import com.technical.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends MasterCodeBase {
    private final AuthService authService;
    private final JwtTokenService jwtTokenService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
        return successResponse("auth.registered", null, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.loginUser(loginRequest);
        return successResponse("auth.login.success", loginResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<SuccessResponse> refreshToken(@RequestParam String refreshToken) {
        LoginResponse refreshResponse = authService.refreshToken(refreshToken);
        return successResponse("auth.refresh.token.success", refreshResponse, HttpStatus.OK);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<SuccessResponse> verifyEmail(@RequestParam String email, @RequestParam String token) {
        authService.verifyEmail(email, token);
        return successResponse("auth.verified.email", null, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request.getEmail());
        return successResponse("auth.sent.reset.email", null, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        return successResponse("auth.reset.password.success", null, HttpStatus.OK);
    }

    @PostMapping(value="/api/logout")
    public ResponseEntity<SuccessResponse> logout (@RequestParam String token) {
        jwtTokenService.invalidateJwtToken(token);
        return successResponse("auth.logout.success", null, HttpStatus.OK);
    }

}