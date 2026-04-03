package com.technical.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String gender;
    private LocalDate birthday;
    private String phone;
    private String profileImgPath;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
