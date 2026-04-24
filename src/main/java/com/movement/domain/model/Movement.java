package com.movement.domain.model;

import com.equipament.domain.model.Equipament;
import com.movement.domain.enums.MovementType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_movement")
public class Movement {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipament_id", nullable = false)
    private Equipament equipament;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimento", nullable = false)
    private MovementType movementType;

    @Column(name = "justification")
    private String justification;

    private String projeto;
    private String responsavel;
    private String local;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    private LocalDateTime dataHora;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_movement_images", joinColumns = @JoinColumn(name = "movement_id"))
    @Column(name = "image_url")
    private java.util.Set<String> imageUrls = new java.util.HashSet<>();

    protected Movement() {}

    public Movement(
            Equipament equipament,
            MovementType movementType,
            String justification,
            String projeto,
            String responsavel,
            String local,
            String observacao
    ) {
        this.id = UUID.randomUUID();
        this.equipament = equipament;
        this.movementType = movementType;
        this.justification = justification;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.local = local;
        this.dataHora = LocalDateTime.now();
        this.observacao = observacao;
    }

    public void update(
            MovementType movementType,
            String projeto,
            String responsavel,
            String local,
            String observacao
    ) {
        this.movementType = movementType;
        this.projeto = projeto;
        this.responsavel = responsavel;
        this.local = local;
        this.observacao = observacao;
    }

    public void addImageUrl(String url) {
        this.imageUrls.add(url);
    }

    public void clearImageUrls() {
        if (this.imageUrls != null) {
            this.imageUrls.clear();
        }
    }

    // Getters
    public UUID getId() { return id; }
    public Equipament getEquipament() { return equipament; }
    public MovementType getMovementType() { return movementType; }
    public String getProjeto() { return projeto; }
    public String getResponsavel() { return responsavel; }
    public String getLocal() { return local; }
    public String getObservacao() { return observacao; }
    public String getJustification() { return justification; }
    public LocalDateTime getDataHora() { return dataHora; }
    public java.util.Set<String> getImageUrls() { return imageUrls; }
}