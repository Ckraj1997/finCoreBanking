package mca.fincorebanking.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.Loan;
import mca.fincorebanking.entity.Transaction;

public interface PdfService {
    
    // 1. For Managers (and Customers)
    ByteArrayInputStream generateLoanSanctionLetter(Loan loan);

    // 2. For Tellers
    ByteArrayInputStream generateTransactionReceipt(Transaction transaction);

    // 3. For Customers
    ByteArrayInputStream generateAccountStatement(Account account, List<Transaction> transactions);
}