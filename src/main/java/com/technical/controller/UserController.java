package com.technical.controller;

import com.technical.dto.user.UpdateProfileRequest;
import com.technical.dto.user.UserDTO;
import com.technical.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile() {
        UserDTO user = userService.getUserByEmail();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<UserDTO>> getAllUserProfiles() {
        List<UserDTO> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @PatchMapping("/name")
    public ResponseEntity<UserDTO> updateName(
            @RequestBody @Size(max = 100, message = "profile.name.must.not.exceed.100.characters") String name) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().name(name).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/gender")
    public ResponseEntity<UserDTO> updateGender(
            @RequestBody @Size(max = 10, message = "profile.gender.must.not.exceed.10.characters") String gender) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().gender(gender).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/birthday")
    public ResponseEntity<UserDTO> updateBirthday(
            @RequestBody LocalDate birthday) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().birthday(birthday).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/phone")
    public ResponseEntity<UserDTO> updatePhone(
            @RequestBody @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "profile.phone.number.must.be.valid") String phone) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().phone(phone).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/email")
    public ResponseEntity<UserDTO> updateEmail(
            @RequestBody @Email(message = "profile.email.must.be.valid") String email) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().email(email).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping(value = "/profile-img", consumes = "multipart/form-data")
    public ResponseEntity<UserDTO> updateProfileImage(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UpdateProfileRequest request = UpdateProfileRequest.builder().profileImage(profileImage).build();
        UserDTO updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(updatedUser);
    }
}
