package com.equipament.domain.model;

import com.equipament.domain.enums.EquipmentUsage;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tb_equipament")
public class Equipament {

    @Id
    private UUID id;

    private String name;
    private String description;
    private Long topo;

    @Column(name = "codigo", unique = true)
    private String codigo;

    private LocalDateTime dateHour;

    @Column(name = "categoria")
    private String categoria;

    @Enumerated(EnumType.STRING)
    private EquipmentUsage usageType;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "proprietary_id", referencedColumnName = "id")
    private Proprietary proprietary;

    @OneToMany(mappedBy = "equipament", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20) // <--- ADICIONE AQUI
    private Set<PerPart> perParts = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "tb_equipament_images", joinColumns = @JoinColumn(name = "equipament_id"))
    @Column(name = "image_url")
    @BatchSize(size = 20) // <--- ADICIONE AQUI TAMBÉM (Essencial para ElementCollection)
    private Set<String> imageUrls = new HashSet<>();

    @OneToOne(mappedBy = "equipament", cascade = CascadeType.ALL)
    private Status status;

    protected Equipament() {}

    public Equipament(
            String name,
            String description,
            Long topo,
            String categoria,
            LocalDateTime dateHour,
            EquipmentUsage usageType,
            Proprietary proprietary
    ) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.topo = topo;
        this.categoria = categoria;
        this.dateHour = dateHour != null ? dateHour : LocalDateTime.now();
        this.usageType = usageType;
        this.proprietary = proprietary;
        this.active = true;
    }

    public void addImageUrl(String url) {
        if (this.imageUrls == null) {
            this.imageUrls = new HashSet<>();
        }
        this.imageUrls.add(url);
    }

    public void clearImageUrls() {
        if (this.imageUrls != null) {
            this.imageUrls.clear();
        }
    }

    public void setInitialStatus(Status status) {
        this.status = status;
    }

    public void deactivate() {
        this.active = false;
    }

    public void update(
            String name,
            String description,
            Long topo,
            String categoria,
            EquipmentUsage usageType,
            boolean active,
            Proprietary proprietary
    ) {
        this.name = name;
        this.description = description;
        this.topo = topo;
        this.categoria = categoria;
        this.usageType = usageType;
        this.active = active;
        this.proprietary = proprietary;
    }

    public void addPerPart(String name, String serialNumber) {
        if (this.perParts == null) {
            this.perParts = new HashSet<>();
        }

        PerPart newPart = new PerPart(name, serialNumber, this);
        this.perParts.add(newPart);
    }

    public void clearPerParts() {
        if (this.perParts != null) {
            this.perParts.clear();
        }
    }

    public void atualizarStatus(Status novoStatus) {
        this.status = novoStatus;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getDateHour() { return dateHour; }
    public EquipmentUsage getUsageType() { return usageType; }
    public boolean isActive() { return active; }
    public Proprietary getProprietary() { return proprietary; }
    public Status getStatus() { return status; }
    public Long getTopo() { return topo; }
    public Set<String> getImageUrls() { return imageUrls; }
    public Set<PerPart> getPerParts() { return perParts; }
    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria;}

    // GETTER E SETTER DO CÓDIGO
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}