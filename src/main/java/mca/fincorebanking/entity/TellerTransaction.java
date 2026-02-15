package mca.fincorebanking.entity;

import java.time.LocalDateTime;

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
@Table(name = "teller_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TellerTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // CASH_DEPOSIT, CASH_WITHDRAWAL
    private Double amount;
    private String status; // PENDING_AUTH, COMPLETED, REJECTED

    // Who performed it? (The Teller)
    @ManyToOne
    @JoinColumn(name = "teller_id")
    private User teller;

    // Which customer was served?
    @ManyToOne
    @JoinColumn(name = "customer_account_id")
    private Account targetAccount;

    // If high value, who approved it? (The Manager)
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User approvedByManager;

    private LocalDateTime timestamp;
}