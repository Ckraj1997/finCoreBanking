package mca.fincorebanking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.LoanInterestRate;
import mca.fincorebanking.repository.LoanInterestRateRepository;
import mca.fincorebanking.service.LoanInterestRateService;

@Service
public class LoanInterestRateServiceImpl
        implements LoanInterestRateService {

    private final LoanInterestRateRepository repo;

    public LoanInterestRateServiceImpl(LoanInterestRateRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<LoanInterestRate> getActiveLoanTypes() {
        return repo.findByActiveTrue();
    }

    @Override
    public double getInterestRate(String loanType) {
        return repo.findByLoanType(loanType).getInterestRate();
    }

    @Override
    public void updateInterestRate(String loanType, double rate) {
        LoanInterestRate r = repo.findByLoanType(loanType);
        r.setInterestRate(rate);
        repo.save(r);
    }

    @Override
    public List<LoanInterestRate> findAll() {
        return repo.findAll();
    }

     @Override
    public void addLoanType(String loanType, double rate) {
        if (repo.findByLoanType(loanType) != null) {
            throw new RuntimeException("Loan type already exists");
        }
        LoanInterestRate type = new LoanInterestRate();
        type.setLoanType(loanType);
        type.setInterestRate(rate);
        repo.save(type);
    }

     @Override
    public void updateLoanType(Long id, double rate, boolean active) {
        LoanInterestRate type = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan type not found"));
        type.setInterestRate(rate);
        type.setActive(active);
        repo.save(type);
    }

    @Override
    public void deleteLoanType(Long id) {
        repo.deleteById(id); // optional: replace with soft delete
    }

    @Override
    public List<LoanInterestRate> getAllLoanTypes() {
        return repo.findAll();
    }

    @Override
    public void updateStatus(String loanType, boolean active) {
        LoanInterestRate rate = repo.findByLoanType(loanType);
        rate.setActive(active);
        repo.save(rate);
    }
}

