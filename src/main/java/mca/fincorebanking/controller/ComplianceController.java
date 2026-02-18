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
    // ⚡ ACTION: FREEZE ACCOUNT
    // @PostMapping("/actions/freeze-account")
    // public String freezeAccount(@RequestParam Long accountId, RedirectAttributes redirect) {
    //     try {
    //         accountService.updateAccountStatus(accountId, "FROZEN");
    //         // ❌ Removed manual auditService.log() -> Handled by GlobalAuditAspect
    //         redirect.addFlashAttribute("success", "Account ID " + accountId + " has been FROZEN.");
    //     } catch (Exception e) {
    //         redirect.addFlashAttribute("error", "Failed to freeze account: " + e.getMessage());
    //     }
    //     return "redirect:/compliance/reports/fraud";
    // }

    // 🚩 ACTION: MANUAL FRAUD FLAG
    @PostMapping("/actions/flag-user")
    public String flagUser(@RequestParam String username, @RequestParam String reason, RedirectAttributes redirect) {
        try {
            fraudService.logFraud(username, "MANUAL FLAG: " + reason);
            // ❌ Removed manual auditService.log() -> Handled by GlobalAuditAspect
            redirect.addFlashAttribute("success", "User " + username + " flagged as suspicious.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error flagging user: " + e.getMessage());
        }
        return "redirect:/compliance/reports/fraud";
    }

    @PostMapping("/actions/freeze-account")
    public String freezeUser(@RequestParam String username, RedirectAttributes redirect) {
        try {
            accountService.freezeAccountByUsername(username);
            // Auto-audit logging handled by Aspect
            redirect.addFlashAttribute("success", "All accounts for user '" + username + "' have been FROZEN.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Failed to freeze user: " + e.getMessage());
        }
        return "redirect:/compliance/reports/fraud";
    }
}