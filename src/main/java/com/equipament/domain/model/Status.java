package com.equipament.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_status")
public class Status {
    @Id
    private UUID id;
    private String status;
    @OneToOne
    @JoinColumn(name = "equipament_id", referencedColumnName = "id")
    private Equipament equipament;

    @ManyToOne
    @JoinColumn(name = "status_type_id")
    private StatusType statusType;

    protected Status() {}

    public Status(Equipament equipament, StatusType statusType) {
        this.id = UUID.randomUUID();
        this.equipament = equipament;
        this.statusType = statusType;
        this.status = statusType.getName();
    }

    public void updateStatus(StatusType statusType, String status) {
        this.statusType = statusType;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getStatus() { return status; }
    public Equipament getEquipament() { return equipament;}
    public StatusType getStatusType() { return statusType; }
}
