package mca.fincorebanking.dto;


import mca.fincorebanking.entity.Transaction;

public class TransactionReceiptDTO {

    private final Transaction debitTransaction;
    private final Transaction creditTransaction;

    public TransactionReceiptDTO(Transaction debitTransaction,
                                 Transaction creditTransaction) {
        this.debitTransaction = debitTransaction;
        this.creditTransaction = creditTransaction;
    }

    public Transaction getDebitTransaction() {
        return debitTransaction;
    }

    public Transaction getCreditTransaction() {
        return creditTransaction;
    }
}
