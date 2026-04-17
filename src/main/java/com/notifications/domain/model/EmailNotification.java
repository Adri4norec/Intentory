package com.notifications.domain.model;

import java.util.List;
import java.util.UUID;

public class EmailNotification {
    private UUID id;
    private String subject;
    private String body;
    private List<String> recipients;

    public EmailNotification(String subject, String body, List<String> recipients) {
        this.id = UUID.randomUUID();
        this.subject = subject;
        this.body = body;
        this.recipients = recipients;
    }

    public UUID getId() { return id; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public List<String> getRecipients() { return recipients; }
}
