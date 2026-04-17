package com.equipment.application.dto;

public record EquipamentRequest (
        String name,
        String description,
        String serialNumber,
        double price
){ }
