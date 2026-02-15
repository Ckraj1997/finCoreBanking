package mca.fincorebanking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fixed_deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixedDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double principalAmount; // The amount invested

    @Column(nullable = false)
    private Double interestRate;    // Locked rate at time of creation

    @Column(nullable = false)
    private Integer tenureMonths;   // Duration in months

    @Column(nullable = false)
    private Double maturityAmount;  // Calculated amount to be returned

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime maturityDate;

    @Column(nullable = false)
    private String status; // ACTIVE, CLOSED, LIQUIDATED

    // 🔗 Owner of the FD
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔗 The account where money comes from (and goes back to)
    @ManyToOne
    @JoinColumn(name = "linked_account_id", nullable = false)
    private Account linkedAccount;
}