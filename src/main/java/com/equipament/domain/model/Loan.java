package com.equipament.domain.model;

import com.equipament.domain.enums.LoanStatus;
import com.identity.domain.UserEntity;
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
    private Equipament equipament;

    @ManyToOne
    @JoinColumn(name = "collaborator_id", nullable = false)
    private UserEntity collaborator;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = true)
    private UserEntity tecnico;

    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private LocalDateTime expectedReturnDate;

    private String helpdeskTicket;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    private String observation;

    protected Loan() {
    }

    public static Loan createPreparation(Equipament equipament, UserEntity tecnico, UserEntity collaborator,
                                         LocalDateTime loanDate, String helpdeskTicket, String observation) {
        Loan loan = new Loan();
        loan.equipament = equipament;
        loan.tecnico = tecnico;
        loan.collaborator = collaborator;
        loan.loanDate = loanDate;
        loan.helpdeskTicket = helpdeskTicket;
        loan.observation = observation;
        loan.status = LoanStatus.PREPARACAO;
        return loan;
    }

    public UUID getId() { return id; }
    public Equipament getEquipament() { return equipament; }
    public UserEntity getCollaborator() { return collaborator; }
    public UserEntity getTecnico() { return tecnico; }
    public LocalDateTime getLoanDate() { return loanDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public LocalDateTime getExpectedReturnDate() { return expectedReturnDate; }
    public String getHelpdeskTicket() { return helpdeskTicket; }
    public LoanStatus getStatus() { return status; }
    public String getObservation() { return observation; }

}