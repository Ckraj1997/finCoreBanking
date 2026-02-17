package mca.fincorebanking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mca.fincorebanking.dto.TransactionReceiptDTO;
import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.Beneficiary;
import mca.fincorebanking.entity.Transaction;
import mca.fincorebanking.repository.AccountRepository;
import mca.fincorebanking.repository.BeneficiaryRepository;
import mca.fincorebanking.repository.TransactionRepository;
import mca.fincorebanking.service.FraudService;
import mca.fincorebanking.service.NotificationService;
import mca.fincorebanking.service.TransactionService;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final FraudService fraudService;
    private static final double LARGE_TRANSFER_LIMIT = 100000;
    // private final TransactionReceiptDTO transactionReceiptDTO;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
            BeneficiaryRepository beneficiaryRepository,
            AccountRepository accountRepository,
            NotificationService notificationService,
            FraudService fraudService

    // , TransactionReceiptDTO transactionReceiptDTO
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
        this.fraudService = fraudService;
        this.beneficiaryRepository = beneficiaryRepository;
    }

    @Override
    public void credit(Account account, Double amount) {

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("CREDIT");
        tx.setBalanceAfter(account.getBalance());
        tx.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(tx);

    }

    @Override
    public void debit(Account account, Double amount) {

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("DEBIT");
        tx.setBalanceAfter(account.getBalance());
        tx.setTransactionTime(LocalDateTime.now());

        transactionRepository.save(tx);
    }

    @Override
    public List<Transaction> getTransactions(Account account) {
        return transactionRepository.findByAccount(account);
    }

    @Transactional
    @Override
    public TransactionReceiptDTO transfer(
            String username,
            Long accountId,
            Long beneficiaryId,
            Double amount) {

        Account fromAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 🔐 Ownership validation
        if (!fromAccount.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized account access");
        }

        if (amount >= LARGE_TRANSFER_LIMIT) {
            fraudService.recordFraud(
                    fromAccount.getUser().getUsername(),
                    "Large fund transfer: ₹" + amount);
        }

        // 💰 Balance validation
        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));

        // ✅ Approval validation
        if (!"APPROVED".equals(beneficiary.getStatus())) {
            throw new RuntimeException("Beneficiary not approved by admin");
        }

        Account toAccount = accountRepository
                .findByAccountNumber(beneficiary.getBeneficiaryAccountNumber())
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        // 💸 Debit
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        accountRepository.save(fromAccount);

        Transaction debitTx = transactionRepository.save(
                new Transaction(null, "DEBIT", amount, fromAccount.getBalance(), LocalDateTime.now(),
                        fromAccount));
        // 💰 Credit
        toAccount.setBalance(toAccount.getBalance() + amount);
        accountRepository.save(toAccount);

        Transaction creditTx = transactionRepository
                .save(new Transaction(null, "CREDIT", amount, toAccount.getBalance(), LocalDateTime.now(),
                        toAccount));

        notificationService.notify(
                fromAccount.getUser(),
                "₹" + amount + " transferred successfully");

        

        return new TransactionReceiptDTO(debitTx, creditTx);
    }

    @Override
    public List<Transaction> findRecentByUser(String username, int limit) {

        PageRequest pageable = PageRequest.of(0, limit);
        return transactionRepository
                .findTopNByUserOrderByTransactionTimeDesc(username, pageable);
    }

    @Override
    public Page<Transaction> getTransactions(
            Long accountId,
            // String username,
            String type,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size) {
        return transactionRepository.findTransactions(
                // username
                accountId,
                type,
                fromDate,
                toDate,
                PageRequest.of(page, size, Sort.by("transactionTime").descending()));
    }

    @Override
    public List<Transaction> getAllTransactions(
            Long accountId,
            String type,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        return transactionRepository.findAllForExport(
                accountId, type, fromDate, toDate);
    }

    @Override
    public long countByUsername(String username) {
        return transactionRepository.countByUserUsername(username);
    }

    @Override
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Transactional
    @Override
    @SuppressWarnings("ConvertToStringSwitch")
    public void processSelfTransaction(
            String username,
            Long accountId,
            String type,
            Double amount) {

        if (amount <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 🔐 ownership check
        if (!account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized account access");
        }

        if ("DEBIT".equals(type)) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Insufficient balance");
            }
            account.setBalance(account.getBalance() - amount);
        } else if ("CREDIT".equals(type)) {
            account.setBalance(account.getBalance() + amount);
        } else {
            throw new RuntimeException("Invalid transaction type");
        }

        accountRepository.save(account);

        transactionRepository.save(
                new Transaction(null,
                        type,
                        amount,
                        account.getBalance(),
                        LocalDateTime.now(),
                        account));

    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

}
