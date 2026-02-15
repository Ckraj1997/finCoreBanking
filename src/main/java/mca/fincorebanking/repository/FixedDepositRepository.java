package mca.fincorebanking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mca.fincorebanking.entity.FixedDeposit;
import mca.fincorebanking.entity.User;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    
    // Find all FDs for a specific user (for dashboard)
    List<FixedDeposit> findByUser(User user);

    // Find active FDs to check for maturity
    List<FixedDeposit> findByStatus(String status);
}