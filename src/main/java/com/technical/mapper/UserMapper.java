package com.technical.mapper;

import com.technical.dto.UserDTO;
import com.technical.dto.request.RegisterRequest;
import com.technical.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(source = "verified", target = "isVerified")
    UserDTO toUserDTO(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isVerified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded in service
    User toEntity(RegisterRequest registerRequest);
}
