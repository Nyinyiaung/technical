package com.technical.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    
    @Size(max = 100, message = "profile.name.must.not.exceed.100.characters")
    private String name;
    
    @Size(max = 10, message = "profile.gender.must.not.exceed.10.characters")
    private String gender;
    
    private LocalDate birthday;
    
    @Pattern(regexp = "^[+]?[1-9]\\d{1,14}$", message = "profile.phone.number.must.be.valid")
    private String phone;
    
    @Email(message = "profile.email.must.be.valid")
    private String email;
    
    private MultipartFile profileImage;
}
