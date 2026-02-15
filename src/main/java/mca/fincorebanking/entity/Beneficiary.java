package mca.fincorebanking.entity;

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
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String beneficiaryName;

    @Column(nullable = false)
    private String beneficiaryAccountNumber;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
