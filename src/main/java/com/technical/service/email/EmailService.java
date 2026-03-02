package com.technical.service.email;

import com.technical.entity.user.User;

public interface EmailService {
    void sendVerificationEmail(User user);

    void sendPasswordResetEmail(String email);
}
