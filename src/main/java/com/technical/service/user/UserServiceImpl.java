package com.technical.service.user;

import com.technical.dao.UserRepository;
import com.technical.dto.user.UserDTO;
import com.technical.exception.ResourceNotFoundException;
import com.technical.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(userMapper::toUserDTO)
                .toList();
    }
}
