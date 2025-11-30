package com.technical.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "msg.email.required")
    @Email(message = "msg.email.must.be.valid")
    private String email;

    private String token; // For verification

    @NotBlank(message = "msg.password.required")
    @Size(min = 4, max = 8, message = "msg.password.must.be.between.4.and.8")
    private String newPassword;
}
