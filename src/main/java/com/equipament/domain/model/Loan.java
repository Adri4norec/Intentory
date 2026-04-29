package com.equipament.domain.model;

import com.equipament.domain.enums.LoanStatus;
import com.identity.domain.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    @ElementCollection
    @CollectionTable(name = "tb_loan_documents", joinColumns = @JoinColumn(name = "loan_id"))
    @Column(name = "document_url")
    private Set<String> documentUrls = new HashSet<>();

    protected Loan() {
    }

    public static Loan createPreparation(Equipament equipament, UserEntity tecnico, UserEntity collaborator,
                                         LocalDateTime loanDate, LocalDateTime returnDate, String helpdeskTicket, String observation) {
        Loan loan = new Loan();
        loan.equipament = equipament;
        loan.tecnico = tecnico;
        loan.collaborator = collaborator;
        loan.loanDate = loanDate;
        loan.returnDate = returnDate;
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
    public Set<String> getDocumentUrls() { return documentUrls; }

    public void addDocumentUrl(String url) {
        if (this.documentUrls == null) {
            this.documentUrls = new HashSet<>();
        }
        this.documentUrls.add(url);
    }

    public void changeStatus(LoanStatus newStatus) {
        if (this.status == LoanStatus.EMPRESTIMO_FINALIZADO || this.status == LoanStatus.DEVOLVIDO) {
            throw new IllegalStateException("Não é possível alterar o status de um empréstimo já finalizado.");
        }

        if (newStatus == LoanStatus.DEVOLVIDO) {
            this.returnDate = LocalDateTime.now();
        }

        if (newStatus == LoanStatus.EM_USO) {
            this.loanDate = LocalDateTime.now();
        }

        this.status = newStatus;
    }
}