package mca.fincorebanking.service;

import java.util.List;

import mca.fincorebanking.entity.LoanInterestRate;

public interface LoanInterestRateService {

    double getInterestRate(String loanType);

    void updateInterestRate(String loanType, double rate);

    List<LoanInterestRate> findAll();

    List<LoanInterestRate> getActiveLoanTypes();

    List<LoanInterestRate> getAllLoanTypes();

    void addLoanType(String loanType, double rate);

    void updateLoanType(Long id, double rate, boolean active);

    void deleteLoanType(Long id);

    void updateStatus(String loanType, boolean active);

    // double getInterestRate(String loanType);

}
