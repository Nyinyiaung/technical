package com.technical.service.auth;

import com.technical.dto.user.UserDTO;
import com.technical.dto.auth.request.LoginRequest;
import com.technical.dto.auth.request.RegisterRequest;
import com.technical.dto.auth.response.LoginResponse;

import java.util.List;

public interface AuthService {

    void registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest);

    void verifyEmail(String email);

    void initiatePasswordReset(String email);

    void resetPassword(String email, String token, String newPassword);
}