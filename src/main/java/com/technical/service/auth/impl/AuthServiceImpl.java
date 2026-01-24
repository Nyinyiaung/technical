package com.technical.service.auth.impl;

import com.technical.commonutil.CommonUtil;
import com.technical.config.jwt.JwtTokenUtil;
import com.technical.dao.UserRepository;
import com.technical.dto.auth.request.LoginRequest;
import com.technical.dto.auth.request.RegisterRequest;
import com.technical.dto.auth.response.LoginResponse;
import com.technical.entity.user.User;
import com.technical.exception.ResourceNotFoundException;
import com.technical.exception.UserAlreadyExistsException;
import com.technical.mapper.UserMapper;
import com.technical.service.auth.AuthService;
import com.technical.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;
    private final EmailService emailService;

    private final UserMapper userMapper;
    
    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("username.already.registered");
        }

        // Map RegisterRequest to User entity using MapStruct
        User user = userMapper.toEntity(request);
        // Set the encoded password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        // Generate a password reset token with short expiration (15 minutes)
        String verificationToken = jwtUtil.generateToken(user.getEmail(), CommonUtil.VERIFICATION_TOKEN_TYPE); // 30 minutes

        emailService.sendVerificationEmail(user, verificationToken);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        userRepository.findByEmailAndIsVerifiedTrue(loginRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", loginRequest.getEmail())));

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateToken(authentication.getName(), CommonUtil.ACCESS_TOKEN_TYPE);
        String refreshToken = jwtUtil.generateToken(authentication.getName(), CommonUtil.REFRESH_TOKEN_TYPE);
        return new LoginResponse(accessToken, refreshToken);
    }

    public LoginResponse refreshToken(String refreshToken) {
        // Verify the token is valid and get the email from it
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String tokenEmail = jwtUtil.getEmailFromToken(refreshToken);
        if (StringUtils.isEmpty(tokenEmail)) {
            throw new IllegalArgumentException("Invalid token: missing email");
        }

        User user = userRepository.findByEmailAndIsVerifiedTrue(tokenEmail)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", tokenEmail)));

        jwtUtil.deleteToken(refreshToken);

        String newAccessToken = jwtUtil.generateToken(user.getEmail(), CommonUtil.ACCESS_TOKEN_TYPE);
        String newRefreshToken = jwtUtil.generateToken(user.getEmail(), CommonUtil.REFRESH_TOKEN_TYPE);
        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    public void verifyEmail(String email, String token) {
        // Verify the token is valid and get the email from it
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        String tokenEmail = jwtUtil.getEmailFromToken(token);
        if (!email.equals(tokenEmail)) {
            throw new IllegalArgumentException("Email does not match token");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", email)));

        if (user.isVerified()) {
            throw new IllegalStateException("Email already verified.");
        }

        user.setVerified(true);
        userRepository.save(user);
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailAndIsVerifiedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", email)));
        
        // Generate a password reset token with short expiration (15 minutes)
        String resetToken = jwtUtil.generateToken(user.getEmail(), CommonUtil.RESET_TOKEN_TYPE); // 15 minutes
        
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    public void resetPassword(String email, String token, String newPassword) {
        // Verify the token is valid and get the email from it
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
        
        String tokenEmail = jwtUtil.getEmailFromToken(token);
        if (!email.equals(tokenEmail)) {
            throw new IllegalArgumentException("Email does not match token");
        }
        
        User user = userRepository.findByEmailAndIsVerifiedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", email)));
        
        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmailAndIsVerifiedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}