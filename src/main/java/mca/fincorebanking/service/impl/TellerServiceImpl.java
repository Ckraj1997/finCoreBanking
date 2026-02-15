package mca.fincorebanking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.TellerTransaction;
import mca.fincorebanking.entity.Transaction;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.repository.AccountRepository;
import mca.fincorebanking.repository.TellerRepository;
import mca.fincorebanking.repository.TransactionRepository;
import mca.fincorebanking.repository.UserRepository;
import mca.fincorebanking.service.TellerService;

@Service
public class TellerServiceImpl implements TellerService {

    private final AccountRepository accountRepository;
    private final TellerRepository tellerRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TellerServiceImpl(AccountRepository accountRepository,
            TellerRepository tellerRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.tellerRepository = tellerRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TellerTransaction depositCash(Long tellerId, String accountNumber, Double amount) {
        // 1. Fetch Data
        User teller = userRepository.findById(tellerId).orElseThrow(() -> new RuntimeException("Teller not found"));
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        if (account == null)
            throw new RuntimeException("Invalid Account Number");

        // 2. Update Balance
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        // 3. Create Core Transaction Record (Visible to Customer)
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("CASH DEPOSIT (Branch)");
        tx.setTransactionTime(LocalDateTime.now());
        tx.setBalanceAfter(account.getBalance());
        transactionRepository.save(tx);

        // 4. Create Teller Log (Visible to Branch Manager)
        TellerTransaction tTx = new TellerTransaction();
        tTx.setTeller(teller);
        tTx.setTargetAccount(account);
        tTx.setAmount(amount);
        tTx.setType("CASH_DEPOSIT");
        tTx.setStatus("COMPLETED");
        return tellerRepository.save(tTx);
    }

    @Override
    @Transactional
    public TellerTransaction withdrawCash(Long tellerId, String accountNumber, Double amount) {
        User teller = userRepository.findById(tellerId).orElseThrow(() -> new RuntimeException("Teller not found"));
        // Account account = accountRepository.findByAccountNumber(accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        if (account == null)
            throw new RuntimeException("Invalid Account Number");

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient Funds");
        }

        // 2. Update Balance
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        // 3. Create Core Transaction Record
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("CASH WITHDRAWAL (Branch)");
        tx.setTransactionTime(LocalDateTime.now());
        tx.setBalanceAfter(account.getBalance());
        transactionRepository.save(tx);

        // 4. Create Teller Log
        TellerTransaction tTx = new TellerTransaction();
        tTx.setTeller(teller);
        tTx.setTargetAccount(account);
        tTx.setAmount(amount);
        tTx.setType("CASH_WITHDRAWAL");
        tTx.setStatus("COMPLETED");
        return tellerRepository.save(tTx);
    }

    @Override
    public List<TellerTransaction> getTellerHistory(Long tellerId) {
        User teller = userRepository.findById(tellerId).orElse(null);
        return tellerRepository.findByTeller(teller);
    }
}