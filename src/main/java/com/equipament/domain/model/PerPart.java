package com.equipament.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_per_part")
public class PerPart {
    @Id
    private UUID id;
    private String name;
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipament_id")
    @JsonIgnore
    private Equipament equipament;

    protected PerPart() {}

    public PerPart(String name, String serialNumber, Equipament equipament) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.serialNumber = serialNumber;
        this.equipament = equipament;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSerialNumber() { return serialNumber; }
}
