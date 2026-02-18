package mca.fincorebanking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import mca.fincorebanking.entity.FraudLog;
import mca.fincorebanking.repository.FraudLogRepository;
import mca.fincorebanking.service.FraudService;

@Service
public class FraudServiceImpl implements FraudService {

    private final FraudLogRepository fraudLogRepository;

    public FraudServiceImpl(FraudLogRepository fraudLogRepository) {
        this.fraudLogRepository = fraudLogRepository;
    }

    @Override
    public void logFraud(String username, String reason) {
        FraudLog log = new FraudLog();
        log.setUsername(username);
        log.setReason(reason);
        log.setDetectedAt(LocalDateTime.now());
        // log.setSeverity("HIGH"); // If you have this field
        fraudLogRepository.save(log);
    }

    @Override
    public long countFrauds() {
        return fraudLogRepository.count();
    }

    @Override
    public List<FraudLog> getAllFraudLogs() {
        return fraudLogRepository.findAll();
    }

}
