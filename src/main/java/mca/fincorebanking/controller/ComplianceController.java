package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.service.AccountService;
import mca.fincorebanking.service.AuditService;
import mca.fincorebanking.service.FraudService;

@Controller
@RequestMapping("/compliance")
public class ComplianceController {

    private final AuditService auditService;
    private final FraudService fraudService;
    private final AccountService accountService;

    public ComplianceController(AuditService auditService, FraudService fraudService, AccountService accountService) {
        this.auditService = auditService;
        this.fraudService = fraudService;
        this.accountService = accountService;
    }
@GetMapping("/audit/logs")
    public String viewAuditLogs(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("logs", auditService.getAllLogs());
        return "compliance-audit";
    }

    // ⚠️ READ-ONLY: FRAUD VIEW
    @GetMapping("/reports/fraud")
    public String viewFraudAlerts(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("frauds", fraudService.getAllFraudLogs());
        return "compliance-fraud";
    }

    // ⚡ ACTION: ENFORCEMENT (COMPLIANCE Role Only)
    // This allows Compliance to FREEZE an account suspect of fraud
    @PostMapping("/actions/freeze-account")
    public String freezeAccount(@RequestParam Long accountId, RedirectAttributes redirect) {
        // You will need to implement this method in AccountService
        accountService.updateAccountStatus(accountId, "FROZEN"); 
        auditService.log("COMPLIANCE", "Froze Account ID: " + accountId);
        redirect.addFlashAttribute("success", "Account Frozen Successfully.");
        return "redirect:/compliance/reports/fraud";
    }
}