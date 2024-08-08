package dev.aziz.course.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static dev.aziz.course.services.EmailUtils.getEmailMessage;
import static dev.aziz.course.services.EmailUtils.getEmailMessageWithCode;

@Service
@RequiredArgsConstructor
public class EmailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String NEW_PASSWORD_VERIFICATION = "New Password Verification";
    private final JavaMailSender emailSender;

    @Value("${spring.mail.verify.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendSimpleMailMessage(String name, String to, String activationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessage(name, host, activationCode));
            emailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Async
    public void sendSimpleMailMessageWithSecretCode(String name, String to, String secretCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_PASSWORD_VERIFICATION);
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setText(getEmailMessageWithCode(name, host, secretCode));
            emailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}
