package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.service.AuditService;
import mca.fincorebanking.service.FraudService;
import mca.fincorebanking.service.TransactionService;

@Controller
@RequestMapping("/compliance")
public class ComplianceController {

    private final AuditService auditService;
    private final FraudService fraudService;
    private final TransactionService transactionService;

    public ComplianceController(AuditService auditService, FraudService fraudService,
            TransactionService transactionService) {
        this.auditService = auditService;
        this.fraudService = fraudService;
        this.transactionService = transactionService;
    }

    // 🛡️ SYSTEM AUDIT LOGS
    @GetMapping("/audit")
    public String viewAuditLogs(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("logs", auditService.getAllLogs());
        return "compliance-audit"; // Dedicated read-only view
    }

    // ⚠️ FRAUD ALERTS
    @GetMapping("/fraud")
    public String viewFraudAlerts(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("frauds", fraudService.getAllFraudLogs());
        return "compliance-fraud"; // Dedicated read-only view
    }
}