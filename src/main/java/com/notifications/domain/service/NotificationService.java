package com.notifications.domain.service;

import com.notifications.application.dto.EmailRequest;
import com.notifications.domain.model.EmailNotification;

public interface NotificationService {
    EmailNotification sendEmail(EmailRequest request);
}
