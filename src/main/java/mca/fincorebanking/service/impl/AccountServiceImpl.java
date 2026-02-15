package mca.fincorebanking.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.Notification;
import mca.fincorebanking.entity.Transaction;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.repository.AccountRepository;
import mca.fincorebanking.repository.NotificationRepository;
import mca.fincorebanking.repository.TransactionRepository;
import mca.fincorebanking.repository.UserRepository;
import mca.fincorebanking.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository,
            TransactionRepository transactionRepository,
            NotificationRepository notificationRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Account createAccount(Account account, User user) {

        account.setAccountNumber(generateAccountNumber());
        account.setStatus("ACTIVE");
        account.setUser(user);

        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        // Simple account number generation logic (for demonstration purposes)
        return "AC" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    @Override
    public List<Account> findByUsername(String username) {
        return accountRepository.findByUserUsername(username);
    }

    @Override
    public long countByUsername(String username) {
        return accountRepository.countByUserUsername(username);
    }

    @Override
    public Double totalBalanceByUsername(String username) {
        return accountRepository.sumBalanceByUsername(username);
    }

    @Override
    public long countActiveAccounts() {
        return accountRepository.countByStatus("ACTIVE");
    }

    @Override
    public void requestAccount(Account account, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        account.setUser(user);
        account.setStatus("PENDING");
        account.setAccountNumber(generateAccountNumber());

        // Default balance
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }

        accountRepository.save(account);
    }

    @Override
    public List<Account> getPendingAccounts() {
        return accountRepository.findByStatus("PENDING");
    }

    @Override
    public void approveAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        account.setStatus("ACTIVE");
        accountRepository.save(account);
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public List<Transaction> getRecentTransactions(Long accountId) {
        return transactionRepository
                .findTop5ByAccountIdOrderByTransactionTimeDesc(accountId);
    }

    @Override
    public List<Notification> getAccountNotifications(Long accountId) {
        return notificationRepository.findByAccount_Id(accountId);
    }

    @Override
    public Account findByAccountNumber(String searchAccountNo) {
        return accountRepository.findByAccountNumber(searchAccountNo).orElseThrow();
    }

    @Override
    public void updateAccountStatus(Long id, String status) {
        int updated = accountRepository.updateAccountStatusById(id, status);
        if (updated == 0) {
            throw new RuntimeException("Account not found with id: " + id);
        }
    }

    @Override
    public List<Account> getAccountsByStatus(String pending_admin) {
        return accountRepository.findByStatus(pending_admin);
    }

}
