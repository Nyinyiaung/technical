package com.technical.service.user;

import com.technical.commonutil.UserUtil;
import com.technical.dao.UserRepository;
import com.technical.dto.user.UpdateProfileRequest;
import com.technical.dto.user.UserDTO;
import com.technical.entity.user.User;
import com.technical.exception.ResourceNotFoundException;
import com.technical.mapper.UserMapper;
import com.technical.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FileStorageService fileStorageService;

    @Override
    public UserDTO getUserByEmail() {
        String email = UserUtil.getCurrentUsername();

        UserDTO userDTO = userRepository.findByEmail(email)
                .map(userMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        
        // Convert profile image path to full URL
        if (StringUtils.hasText(userDTO.getProfileImgPath())) {
            String imageUrl = fileStorageService.getFileUrl(userDTO.getProfileImgPath());
            userDTO.setProfileImgPath(imageUrl);
        }
        
        return userDTO;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDTO)
                .toList();
    }

    @Override
    public UserDTO updateProfile(UpdateProfileRequest updateProfileRequest) {
        String email = UserUtil.getCurrentUsername();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Only update fields that are not null or empty
        if (StringUtils.hasText(updateProfileRequest.getName())) {
            user.setName(updateProfileRequest.getName());
        }

        if (StringUtils.hasText(updateProfileRequest.getGender())) {
            user.setGender(updateProfileRequest.getGender());
        }

        if (updateProfileRequest.getBirthday() != null) {
            user.setBirthday(updateProfileRequest.getBirthday());
        }

        if (StringUtils.hasText(updateProfileRequest.getPhone())) {
            user.setPhone(updateProfileRequest.getPhone());
        }

        if (StringUtils.hasText(updateProfileRequest.getEmail()) && 
            !updateProfileRequest.getEmail().equals(email)) {
            // Check if email is already taken by another user
            if (userRepository.findByEmail(updateProfileRequest.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is already taken by another user.");
            }
            user.setEmail(updateProfileRequest.getEmail());
        }

        // Handle profile image file upload
        MultipartFile profileImageFile = updateProfileRequest.getProfileImage();
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            try {
                // Delete old profile image if exists
                String oldProfileImage = user.getProfileImgPath();
                if (StringUtils.hasText(oldProfileImage)) {
                    fileStorageService.deleteFile(oldProfileImage);
                }
                
                // Store new profile image
                String filePath = fileStorageService.storeFile(profileImageFile);
                user.setProfileImgPath(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile image", e);
            }
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toUserDTO(updatedUser);
    }
}
