package mca.fincorebanking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.ChequeBookRequest;
import mca.fincorebanking.entity.DebitCard;
import mca.fincorebanking.repository.AccountRepository;
import mca.fincorebanking.repository.ChequeBookRepository;
import mca.fincorebanking.repository.DebitCardRepository;
import mca.fincorebanking.service.CardService;

@Service
public class CardServiceImpl implements CardService {

    private final DebitCardRepository cardRepository;
    private final ChequeBookRepository chequeRepository;
    private final AccountRepository accountRepository;

    public CardServiceImpl(DebitCardRepository cardRepository, 
                           ChequeBookRepository chequeRepository,
                           AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.chequeRepository = chequeRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public DebitCard getCardByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return null;
        return cardRepository.findByAccount(account);
    }

    @Override
    public DebitCard issueCard(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Check if card already exists
        if(cardRepository.findByAccount(account) != null) {
            throw new RuntimeException("Card already exists for this account");
        }

        DebitCard card = new DebitCard();
        card.setAccount(account);
        // Details generated in @PrePersist or manually here
        card.generateCardDetails(); 
        return cardRepository.save(card);
    }

    @Override
    public void toggleCardStatus(Long cardId) {
        DebitCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        
        if ("ACTIVE".equals(card.getStatus())) {
            card.setStatus("BLOCKED");
        } else {
            card.setStatus("ACTIVE");
        }
        cardRepository.save(card);
    }

    @Override
    public ChequeBookRequest requestChequeBook(Long accountId, String type) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        ChequeBookRequest req = new ChequeBookRequest();
        req.setAccount(account);
        req.setRequestType(type);
        return chequeRepository.save(req);
    }

    @Override
    public List<ChequeBookRequest> getChequeRequestsByAccount(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return chequeRepository.findByAccount(account);
    }

    @Override
    public List<ChequeBookRequest> getAllPendingChequeRequests() {
        return chequeRepository.findByStatus("PENDING");
    }

    @Override
    public void approveChequeRequest(Long requestId) {
        ChequeBookRequest req = chequeRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("APPROVED");
        chequeRepository.save(req);
    }

    @Override
    public List<ChequeBookRequest> getChequeRequestsByStatus(String pending) {
        return chequeRepository.findByStatus(pending);
    }

    @Override
    public void updateChequeRequestStatus(Long id, String pending_admin) {
        ChequeBookRequest req = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus(pending_admin);
        chequeRepository.save(req);
    }
}