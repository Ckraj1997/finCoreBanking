package mca.fincorebanking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "cheque_book_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChequeBookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String requestType; // 20_LEAVES, 50_LEAVES

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    // 🔗 Many requests can come from one account
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @PrePersist
    public void onCreate() {
        this.requestDate = LocalDateTime.now();
        this.status = "PENDING";
    }
}