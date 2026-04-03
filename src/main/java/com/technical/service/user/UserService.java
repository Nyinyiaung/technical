package com.technical.service.user;

import com.technical.dto.user.UpdateProfileRequest;
import com.technical.dto.user.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserByEmail();

    List<UserDTO> getAllUsers();

    UserDTO updateProfile(UpdateProfileRequest updateProfileRequest);
}
