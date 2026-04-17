package com.notifications.domain.model;

import com.shared.AuditableEntity;

import java.util.List;
import java.util.UUID;

public class SendNotification extends AuditableEntity {
    private UUID id;
    private String subject;
    private String body;
    private List<String> recipients;

    public SendNotification(String subject, String body, List<String> recipients) {
        this.id = UUID.randomUUID();
        this.subject = subject;
        this.body = body;
        this.recipients = recipients;
    }

    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public List<String> getRecipients() { return recipients; }
}
