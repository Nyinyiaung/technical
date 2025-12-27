package com.technical.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "msg.email.required")
    @Email(message = "msg.email.must.be.valid")
    private String email;

    private String token; // For verification

    @NotBlank(message = "msg.password.required")
    private String newPassword;
}
