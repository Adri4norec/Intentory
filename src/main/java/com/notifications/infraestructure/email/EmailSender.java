package com.notifications.infraestructure.email;

import com.notifications.domain.model.EmailNotification;

public interface EmailSender {

    void send(EmailNotification notification);
}