package mca.fincorebanking.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String documentType; // AADHAR, PAN, VOTER_ID

    @Column(nullable = false)
    private String documentNumber; // The ID number (e.g. ABCD1234F)

    @Column(nullable = false)
    private String fileName;      // The saved filename on disk

    @Column(nullable = false)
    private String filePath;      // Full path to the file

    @Column(nullable = false)
    private String status;        // PENDING, VERIFIED, REJECTED

    private String rejectionReason;

    private LocalDateTime uploadDate;
    private LocalDateTime verifiedDate;

    // 🔗 One User has only one active KYC Document
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}