package mca.fincorebanking.service.impl;

import org.springframework.stereotype.Service;

import mca.fincorebanking.config.InterestConfig;
import mca.fincorebanking.entity.Account;
import mca.fincorebanking.service.InterestService;

@Service
public class InterestServiceImpl implements InterestService {

    @Override
    public double calculateSavingsInterest(Account account, int months) {

        if (!"SAVINGS".equals(account.getAccountType())) {
            return 0;
        }

        double principal = account.getBalance();
        double rate = InterestConfig.SAVINGS_INTEREST_RATE;
        double timeInYears = months / 12.0;

        // Simple Interest Formula
        return (principal * rate * timeInYears) / 100;
    }
}
