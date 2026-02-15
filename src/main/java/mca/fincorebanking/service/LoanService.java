package mca.fincorebanking.service;

import java.util.List;

import mca.fincorebanking.entity.Loan;

public interface LoanService {

    // void applyLoan(Loan loan, User user);

    void applyLoan(
        String username,
        String loanType,
        Double amount,
        Integer tenureMonths
);

   double calculateEmi(double amount, double annualRate, int tenureMonths);

    List<Loan> getLoansByUser(String username);

    List<Loan> getPendingLoans();

    void approveLoan(Long loanId);

    void rejectLoan(Long loanId);

    long countActiveLoans(String username);

    long countByStatus(String status);

    Loan findById(Long id);

    void updateLoanStatus(Long id, String pending_admin);

    List<Loan> getLoansByStatus(String pending_admin);


}
