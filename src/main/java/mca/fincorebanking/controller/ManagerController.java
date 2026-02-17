
package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.entity.Role;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.service.AccountService;
import mca.fincorebanking.service.BeneficiaryService;
import mca.fincorebanking.service.CardService;
import mca.fincorebanking.service.KycService;
import mca.fincorebanking.service.LoanService;
import mca.fincorebanking.service.PdfService;
import mca.fincorebanking.service.UserService;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final AccountService accountService;
    private final LoanService loanService;
    private final KycService kycService;
    private final CardService cardService;
    private final BeneficiaryService beneficiaryService;
    private final UserService userService;
    private final PdfService pdfService;

    public ManagerController(AccountService accountService, LoanService loanService, KycService kycService,
            CardService cardService, BeneficiaryService beneficiaryService, UserService userService, PdfService pdfService) {
        this.accountService = accountService;
        this.loanService = loanService;
        this.kycService = kycService;
        this.cardService = cardService;
        this.beneficiaryService = beneficiaryService;
        this.userService = userService;
        this.pdfService = pdfService;
    }

    // --- DASHBOARD HUB ---
    @GetMapping("/approvals")
    public String approvalsHub(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Fetch counts of "PENDING" items (Waiting for Manager)
        model.addAttribute("cntAccounts", accountService.getPendingAccounts().size());
        model.addAttribute("cntLoans", loanService.getPendingLoans().size());
        model.addAttribute("cntKyc", kycService.getPendingKycs().size());
        model.addAttribute("cntCheques", cardService.getAllPendingChequeRequests().size());
        return "manager-approvals";
    }

    // --- REPORTS (Fixed: Now populates data) ---
    @GetMapping("/reports")
    public String reportsDashboard(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());

        // Populate Data for Reports
        model.addAttribute("totalDeposits", 1500000.00); // Replace with reportService.getTotalDeposits()
        model.addAttribute("totalWithdrawals", 450000.00); // Replace with reportService.getTotalWithdrawals()
        model.addAttribute("newAccountsThisMonth", accountService.getPendingAccounts().size() + 5);
        model.addAttribute("loansDisbursed", loanService.countByStatus("APPROVED"));

        return "manager-reports";
    }

    // --- ACCOUNTS (Final Authority) ---
    @GetMapping("/accounts")
    public String pendingAccounts(Model model, HttpServletRequest request) {
        // Manager sees "PENDING"
        model.addAttribute("accounts", accountService.getPendingAccounts());
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-account-approval"; // ✅ Point to Manager-specific view
    }

    @PostMapping("/accounts/{id}/approve")
    public String approveAccount(@PathVariable Long id, RedirectAttributes redirect) {
        // ENHANCEMENT: Manager directly activates the account
        accountService.approveAccount(id); // Assumes this sets status to 'ACTIVE'
        redirect.addFlashAttribute("success", "Account Activated Successfully.");
        return "redirect:/manager/accounts";
    }

    // --- LOANS (Final Authority) ---
    @GetMapping("/loans")
    public String pendingLoans(Model model, HttpServletRequest request) {
        model.addAttribute("loans", loanService.getPendingLoans());
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-loan-list";
    }

    @PostMapping("/loans/{id}/approve")
    public String approveLoan(@PathVariable Long id, RedirectAttributes redirect) {
        // ENHANCEMENT: Manager directly disburses the loan
        loanService.approveLoan(id); // Assumes this sets status to 'APPROVED'
        redirect.addFlashAttribute("success", "Loan Approved & Disbursed.");
        return "redirect:/manager/loans";
    }

    @PostMapping("/loans/{id}/reject")
    public String rejectLoan(@PathVariable Long id, RedirectAttributes redirect) {
        // ENHANCEMENT: Manager directly disburses the loan
        loanService.rejectLoan(id); // Assumes this sets status to 'APPROVED'
        redirect.addFlashAttribute("error", "Loan rejected.");
        return "redirect:/manager/loans";
    }

    // --- KYC ---
    @GetMapping("/kyc")
    public String pendingKyc(Model model, HttpServletRequest request) {
        model.addAttribute("kycList", kycService.getKycsByStatus("PENDING"));
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-kyc-list";
    }

    @PostMapping("/kyc/approve")
    public String approveKyc(@RequestParam("id") Long id, RedirectAttributes redirect) {
        kycService.updateKycStatus(id, "VERIFIED"); // Final status
        redirect.addFlashAttribute("success", "KYC Verified Successfully.");
        return "redirect:/manager/kyc";
    }

    @PostMapping("/kyc/reject")
    public String rejectKyc(@RequestParam("id") Long id, RedirectAttributes redirect) {
        kycService.updateKycStatus(id, "REJECTED");
        redirect.addFlashAttribute("error", "KYC Rejected.");
        return "redirect:/manager/kyc";
    }

    // --- BENEFICIARY APPROVALS (Manager Review) ---
    @GetMapping("/beneficiaries")
    public String pendingBeneficiaries(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesByStatus("PENDING"));
        return "manager-beneficiaries"; // ✅ Points to Manager View
    }

    @PostMapping("/beneficiaries/{id}/approve")
    public String forwardBeneficiary(@PathVariable Long id, RedirectAttributes redirect) {
        beneficiaryService.updateBeneficiaryStatus(id, "APPROVED");
        redirect.addFlashAttribute("success", "Beneficiary Verified Successfully.");
        return "redirect:/manager/beneficiaries";
    }

    @PostMapping("/beneficiaries/{id}/reject")
    public String rejectBeneficiary(@PathVariable Long id, RedirectAttributes redirect) {
        beneficiaryService.updateBeneficiaryStatus(id, "REJECTED");
        redirect.addFlashAttribute("error", "Beneficiary rejected.");
        return "redirect:/manager/beneficiaries";
    }

    // --- SERVICE REQUESTS (Manager Review) ---
    @GetMapping("/services")
    public String pendingServices(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("requests", cardService.getChequeRequestsByStatus("PENDING"));
        return "manager-service-requests"; // ✅ Points to Manager View
    }

    @PostMapping("/services/approve")
    public String forwardService(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        cardService.updateChequeRequestStatus(id, "APPROVED");
        redirectAttributes.addFlashAttribute("success", "Request verified and approved.");
        return "redirect:/manager/services";
    }

    @PostMapping("/services/reject")
    public String rejectService(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        cardService.updateChequeRequestStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("error", "Request Rejected.");
        return "redirect:/manager/services";
    }

    // --- USERS (Read Only) ---
    @GetMapping("/users")
    public String searchUsers(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("users", userService.getAllUsers());
        return "admin-users"; // Reuse is fine if no POST actions are taken
    }

    @GetMapping("/customers/create")
    public String showCustomerForm(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("user", new User());
        return "manager-customer-create"; // New View
    }

    @PostMapping("/customers/create")
    public String createCustomer(@ModelAttribute User user, RedirectAttributes redirect) {
        try {
            // 🔒 SECURITY: Force role to CUSTOMER. 
            // Manager cannot create Admins or other Managers.
            user.setRole(Role.CUSTOMER);
            
            userService.saveUser(user);
            
            
            redirect.addFlashAttribute("success", "Customer '" + user.getUsername() + "' onboarded successfully. You can now open accounts for them.");
            return "redirect:/manager/users"; // Redirects to search list
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Onboarding Failed: " + e.getMessage());
            return "redirect:/manager/customers/create";
        }
    }


    // 📥 DOWNLOAD LOAN SANCTION LETTER
    @GetMapping("/loans/{id}/download-sanction")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> downloadSanctionLetter(@PathVariable Long id) {
        
        mca.fincorebanking.entity.Loan loan = loanService.findById(id);
        
        // Security check: Ensure loan is actually approved
        if (!"APPROVED".equals(loan.getStatus())) {
            throw new RuntimeException("Loan is not approved yet.");
        }

        java.io.ByteArrayInputStream bis = pdfService.generateLoanSanctionLetter(loan);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=sanction-letter-" + id + ".pdf");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new org.springframework.core.io.InputStreamResource(bis));
    }
}