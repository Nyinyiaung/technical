package com.technical.service.auth.impl;

import com.technical.config.jwt.JwtTokenUtil;
import com.technical.dao.UserRepository;
import com.technical.dto.UserDTO;
import com.technical.dto.request.LoginRequest;
import com.technical.dto.request.RegisterRequest;
import com.technical.dto.response.LoginResponse;
import com.technical.entity.user.User;
import com.technical.exception.ResourceNotFoundException;
import com.technical.exception.UserAlreadyExistsException;
import com.technical.mapper.UserMapper;
import com.technical.service.auth.AuthService;
import com.technical.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
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
        emailService.sendVerificationEmail(user);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());
        return new LoginResponse(jwt);
    }

    public void verifyEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with email: %s", email)));

        if (user.isVerified()) {
            throw new IllegalStateException("Email already verified.");
        }

        user.setVerified(true);
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
    
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailAndIsVerifiedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // Generate a password reset token with short expiration (15 minutes)
        String resetToken = jwtUtil.generateTokenWithExpiration(user.getEmail(), 15 * 60 * 1000); // 15 minutes
        
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDTO)
                .toList();
    }
}