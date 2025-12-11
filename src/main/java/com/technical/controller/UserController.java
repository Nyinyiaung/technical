package com.technical.controller;

import com.technical.dto.UserDTO;
import com.technical.service.auth.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthServiceImpl authService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO user = authService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<UserDTO>> getAllUserProfiles(@AuthenticationPrincipal UserDetails userDetails) {
        List<UserDTO> userList = authService.getAllUsers();
        return ResponseEntity.ok(userList);
    }
}