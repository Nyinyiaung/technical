package com.technical.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "msg.email.required")
    @Email(message = "msg.email.must.be.valid")
    private String email;
}