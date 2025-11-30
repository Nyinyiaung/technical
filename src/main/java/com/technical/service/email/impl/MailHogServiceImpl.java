package com.technical.service.email.impl;

import com.technical.config.MessageConfig;
import com.technical.entity.user.User;
import com.technical.exception.EmailSendFailedException;
import com.technical.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailHogServiceImpl implements EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final MessageConfig messageConfig;

    @Value("${domain.name}")
    private String domain;

    private static final String VERIFY_EMAIL = "%s/api/auth/verify-email?email=%s";
    private static final String RESET_EMAIL = "%s/api/auth/reset-password?email=%s";

    public void sendVerificationEmail(User user) {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("url", String.format(VERIFY_EMAIL, domain, user.getEmail()));

        String htmlContent = templateEngine.process("verification-form", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject(messageConfig.getMessage("verification.email.subject"));
            helper.setText(htmlContent, true); // true = HTML
        } catch (MessagingException e) {
            throw new EmailSendFailedException("Verification Email sending failed", e);
        }
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail) {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("url", String.format(VERIFY_EMAIL, domain, user.getEmail()));

        String htmlContent = templateEngine.process("reset-form", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(messageConfig.getMessage("reset.email.subject"));
            helper.setText(htmlContent, true); // true = HTML
        } catch (MessagingException e) {
            throw new EmailSendFailedException("Reset Email sending failed", e);
        }
        mailSender.send(message);
    }
}
