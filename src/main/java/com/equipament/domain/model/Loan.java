package com.equipament.domain.model;

import com.equipament.domain.enums.LoanStatus;
import com.user.domain.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipament equipment;

    @ManyToOne
    @JoinColumn(name = "collaborator_id", nullable = false)
    private User collaborator;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private User tecnico;

    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private LocalDateTime expectedReturnDate;

    private String helpdeskTicket;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private String observation;

    public UUID getId() { return id; }
    public Equipament getEquipment() { return equipment; }
    public User getCollaborator() { return collaborator; }
    public User getTecnico() { return tecnico; }
    public LocalDateTime getLoanDate() { return loanDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public LocalDateTime getExpectedReturnDate() { return expectedReturnDate; }
    public String getHelpdeskTicket() { return helpdeskTicket; }
    public LoanStatus getStatus() { return status; }
    public String getObservation() { return observation; }
}
