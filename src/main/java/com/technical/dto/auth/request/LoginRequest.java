package com.technical.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "msg.email.required")
    @Email(message = "msg.email.must.be.valid")
    private String email;

    @NotBlank(message = "msg.password.required")
    private String password;
}
