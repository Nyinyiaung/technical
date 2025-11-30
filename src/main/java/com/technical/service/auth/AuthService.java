package com.technical.service.auth;

import com.technical.dto.request.LoginRequest;
import com.technical.dto.request.RegisterRequest;
import com.technical.dto.response.LoginResponse;

public interface AuthService {

    void registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest);

    void verifyEmail(String email);

    void initiatePasswordReset(String email);

    void resetPassword(String email, String token, String newPassword);
}