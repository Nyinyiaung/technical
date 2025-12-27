package com.technical.service.user;

import com.technical.dto.user.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserByEmail(String username);

    List<UserDTO> getAllUsers();
}
