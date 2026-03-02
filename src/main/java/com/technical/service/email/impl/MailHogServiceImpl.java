package com.technical.service.email.impl;

import com.technical.commonutil.CommonUtil;
import com.technical.config.MessageConfig;
import com.technical.config.jwt.JwtTokenUtil;
import com.technical.entity.user.User;
import com.technical.exception.EmailSendFailedException;
import com.technical.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailHogServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final MessageConfig messageConfig;
    private final JwtTokenUtil jwtUtil;

    @Value("${domain.name}")
    private String domain;

    @Value("${verification.email.url}")
    private String verifyEmailUrl;

    @Value("${reset.email.url}")
    private String resetEmailUrl;

    @Async
    public void sendVerificationEmail(User user) {

        // Generate a password reset token with short expiration (30 minutes)
        String verificationToken = jwtUtil.generateToken(
                user.getEmail(),
                CommonUtil.VERIFICATION_TOKEN_TYPE
        );

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("url", String.format(verifyEmailUrl, domain, user.getEmail(), verificationToken));

        String htmlContent = templateEngine.process("verification-form", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(messageConfig.getMessage("verification.email.subject"));
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new EmailSendFailedException("Verification Email sending failed", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail) {

        // Generate a password reset token with short expiration (15 minutes)
        String resetToken = jwtUtil.generateToken(toEmail, CommonUtil.RESET_TOKEN_TYPE);

        Context context = new Context();
        context.setVariable("url", String.format(resetEmailUrl, domain, toEmail, resetToken));
        context.setVariable("email", toEmail);
        context.setVariable("token", resetToken);

        String htmlContent = templateEngine.process("reset-form", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(messageConfig.getMessage("reset.email.subject"));
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new EmailSendFailedException("Reset Email sending failed", e);
        }
    }
}
