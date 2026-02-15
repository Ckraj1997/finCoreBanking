package mca.fincorebanking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mca.fincorebanking.entity.KycDocument;
import mca.fincorebanking.entity.User;

@Repository
public interface KycRepository extends JpaRepository<KycDocument, Long> {

    // Find KYC by User (To check if they already uploaded one)
    Optional<KycDocument> findByUser(User user);

    // Find all pending KYCs for Admin approval
    List<KycDocument> findByStatus(String status);
}