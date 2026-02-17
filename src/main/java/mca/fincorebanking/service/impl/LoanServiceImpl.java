package mca.fincorebanking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.Loan;
import mca.fincorebanking.entity.LoanInterestRate;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.repository.LoanInterestRateRepository;
import mca.fincorebanking.repository.LoanRepository;
import mca.fincorebanking.repository.UserRepository;
import mca.fincorebanking.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final LoanInterestRateRepository loanInterestRateRepository;

    public LoanServiceImpl(LoanRepository loanRepository, UserRepository userRepository,LoanInterestRateRepository loanInterestRateRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.loanInterestRateRepository = loanInterestRateRepository;
    }

    @Override
    public void applyLoan(String username,
            String loanType,
            Double amount,
            Integer tenureMonths) {

        LoanInterestRate cfg = loanInterestRateRepository.findByLoanType(loanType);

        if (!cfg.isActive()) {
            throw new RuntimeException("Loan type currently unavailable");
        }

        Loan loan = new Loan();
        loan.setLoanType(loanType);
        loan.setAmount(amount);
        loan.setTenureMonths(tenureMonths);

        User user = userRepository.findByUsername(username).orElseThrow();

        // 🔐 VERY IMPORTANT
        loan.setInterestRate(cfg.getInterestRate());

        loan.setUser(user);
        loanRepository.save(loan);
    }

    // @Override
    // public void applyLoan(String username,
    // String loanType,
    // Double amount,
    // Integer tenureMonths) {

    // if (amount <= 0 || tenureMonths <= 0) {
    // throw new RuntimeException("Invalid loan details");
    // }

    // User user = userRepository.findByUsername(username)
    // .orElseThrow(() -> new RuntimeException("User not found"));

    // Loan loan = new Loan();
    // loan.setLoanType(loanType);
    // loan.setAmount(amount);
    // loan.setTenureMonths(tenureMonths);
    // loan.setInterestRate(8.5); // default demo rate
    // loan.setUser(user);

    // loanRepository.save(loan);
    // }

    @Override
    public List<Loan> getLoansByUser(String username) {
        return loanRepository.findByUserUsername(username);
    }

    @Override
    public List<Loan> getPendingLoans() {
        // return loanRepository.findByStatus("PENDING");
        return loanRepository.findAll();
    }

    @Override
    public void approveLoan(Long loanId) {
        Loan loan = findById(loanId);
        loan.setStatus("APPROVED");
        loan.setDecisionDate(LocalDateTime.now());
        loanRepository.save(loan);
    }

    @Override
    public void rejectLoan(Long loanId) {
        Loan loan = findById(loanId);
        loan.setStatus("REJECTED");
        loan.setDecisionDate(LocalDateTime.now());
        loanRepository.save(loan);
    }

    @Override
    public Loan findById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }

    @Override
    public double calculateEmi(double amount, double annualRate, int tenureMonths) {

        double monthlyRate = annualRate / 12 / 100;
        double emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths))
                / (Math.pow(1 + monthlyRate, tenureMonths) - 1);

        return Math.round(emi * 100.0) / 100.0; // 2 decimal places
    }

    @Override
    public long countActiveLoans(String username) {
        return loanRepository.countByUserUsernameAndStatus(username, "APPROVED");
    }

    @Override
    public long countByStatus(String status) {
        return loanRepository.countByStatus(status);
    }

    @Override
    public void updateLoanStatus(Long id, String pending_admin) {
        int updated = loanRepository.updateLoanStatusById(id, pending_admin);
        if (updated == 0) {
            throw new RuntimeException("Loan not found with id: " + id);
        }
    }

    @Override
    public List<Loan> getLoansByStatus(String pending_admin) {
        return loanRepository.findByStatus(pending_admin);
    }


}
