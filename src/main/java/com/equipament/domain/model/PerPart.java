package com.equipament.domain.model.event;

import jakarta.persistence.Id;

import java.util.UUID;

public class PerPart {
    @Id
    private UUID id;
    private String name;
    private String description;
}
