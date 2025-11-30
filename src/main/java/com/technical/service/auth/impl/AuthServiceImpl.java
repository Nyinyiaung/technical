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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;
    private final EmailService emailService;

    public void registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("username.already.registered");
        }

        User user = User.builder().email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .isVerified(false).build();
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
        User user = userRepository.findByEmailAndIsVerifiedTrue(email).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmailAndIsVerifiedTrue(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        emailService.sendPasswordResetEmail(user.getEmail());
    }

    public void resetPassword(String email, String token, String newPassword) {
        User user = userRepository.findByEmailAndIsVerifiedTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return mapToUserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDTO).toList();
    }

    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isVerified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}