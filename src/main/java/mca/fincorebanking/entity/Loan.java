package mca.fincorebanking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanType; // HOME, PERSONAL, EDUCATION

    private Double amount;

    private Double interestRate;

    private Integer tenureMonths;

    private String status; // PENDING, APPROVED, REJECTED

    private Double emi;

    private LocalDateTime appliedDate;

    private LocalDateTime decisionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void onCreate() {
        this.appliedDate = LocalDateTime.now();
        this.status = "PENDING";
    }
}
