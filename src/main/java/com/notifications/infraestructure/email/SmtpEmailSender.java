package com.notifications.infraestructure.email;

import com.notifications.domain.model.EmailNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(EmailNotification notification) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(notification.getRecipients().toArray(new String[0]));
        message.setSubject(notification.getSubject());
        message.setText(notification.getBody());

        mailSender.send(message);
    }
}