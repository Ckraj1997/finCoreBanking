package mca.fincorebanking.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.Beneficiary;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.repository.BeneficiaryRepository;
import mca.fincorebanking.repository.UserRepository;
import mca.fincorebanking.service.BeneficiaryService;
import mca.fincorebanking.service.NotificationService;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public BeneficiaryServiceImpl(UserRepository userRepository, NotificationService notificationService, BeneficiaryRepository beneficiaryRepository) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.notificationService = notificationService;
         this.userRepository = userRepository;
    }

    @Override
    public long countPending() {
        return beneficiaryRepository.countByStatus("PENDING");
    }

    @Override
    public List<Beneficiary> findApprovedByUsername(String username) {
        return beneficiaryRepository
                .findByUserUsernameAndStatus(username, "APPROVED");
    }

    @Override
    public List<Beneficiary> getPendingBeneficiaries() {
        return beneficiaryRepository.findByStatus("PENDING");
    }

    @Override 
    public void approveBeneficiary(Long id) {
        Beneficiary b = beneficiaryRepository.findById(id).orElseThrow();
        b.setStatus("APPROVED");
        beneficiaryRepository.save(b);

        
        notificationService.notify(
                b.getUser(),
                "Your beneficiary " + b.getBeneficiaryName() + " has been approved");
    }

    @Override
    public void rejectBeneficiary(Long id) {
        Beneficiary b = beneficiaryRepository.findById(id).orElseThrow();
        b.setStatus("REJECTED");
        beneficiaryRepository.save(b);

        
        notificationService.notify(
                b.getUser(),
                "Your beneficiary " + b.getBeneficiaryName() + " has been rejected");
    }

   @Override
    public void addBeneficiary(Beneficiary beneficiary, String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        beneficiary.setUser(user);
        beneficiary.setStatus("PENDING");

        beneficiaryRepository.save(beneficiary);

      
    }
   @Override
    public List<Beneficiary> getBeneficiaries(String username) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return beneficiaryRepository.findByUser(user);
    }


    @Override
    public List<Beneficiary> getBeneficiariesByStatus(String status) {
        return beneficiaryRepository.findByStatus(status);
    }

    @Override 
    public void updateBeneficiaryStatus(Long id, String status) {
        Beneficiary b = beneficiaryRepository.findById(id).orElseThrow();
        b.setStatus(status);
        beneficiaryRepository.save(b);

      
    }

}
