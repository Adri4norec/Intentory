package com.notifications.domain.service;

import com.notifications.application.dto.EmailRequest;
import com.notifications.domain.model.EmailNotification;
import com.notifications.infraestructure.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailSender emailSender;

    @Override
    public EmailNotification sendEmail(EmailRequest request) {

        EmailNotification notification =
                new EmailNotification(
                        request.subject(),
                        request.body(),
                        request.recipients()
                );

        emailSender.send(notification);

        return notification;
    }
}