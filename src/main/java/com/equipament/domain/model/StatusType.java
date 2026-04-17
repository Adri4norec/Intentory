package com.equipament.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tb_status_type")
public class StatusType {

    @Id
    private UUID id;
    private String name;

    protected StatusType() {}

    public StatusType(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}