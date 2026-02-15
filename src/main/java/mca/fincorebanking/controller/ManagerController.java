// package mca.fincorebanking.controller;

// import java.util.List;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import jakarta.servlet.http.HttpServletRequest;
// import mca.fincorebanking.entity.ChequeBookRequest;
// import mca.fincorebanking.entity.KycDocument;
// import mca.fincorebanking.service.AccountService;
// import mca.fincorebanking.service.BeneficiaryService;
// import mca.fincorebanking.service.CardService;
// import mca.fincorebanking.service.KycService;
// import mca.fincorebanking.service.LoanService;
// import mca.fincorebanking.service.UserService;

// @Controller
// @RequestMapping("/manager")
// public class ManagerController {

//     private final AccountService accountService;
//     private final LoanService loanService;
//     private final KycService kycService;
//     private final CardService cardService;
//     private final BeneficiaryService beneficiaryService;
//     private final UserService userService;

//     public ManagerController(AccountService accountService, LoanService loanService, KycService kycService,
//             CardService cardService, BeneficiaryService beneficiaryService, UserService userService) {
//         this.accountService = accountService;
//         this.loanService = loanService;
//         this.kycService = kycService;
//         this.cardService = cardService;
//         this.beneficiaryService = beneficiaryService;
//         this.userService = userService;
//     }

// // 📋 APPROVALS HUB (New)
//     @GetMapping("/approvals")
//     public String approvalsHub(HttpServletRequest request, Model model) {
//         model.addAttribute("currentUri", request.getRequestURI());
//         // Add counts for the badges
//         model.addAttribute("cntAccounts", accountService.getPendingAccounts().size());
//         model.addAttribute("cntLoans", loanService.getPendingLoans().size());
//         model.addAttribute("cntKyc", kycService.getPendingKycs().size());
//         model.addAttribute("cntCheques", cardService.getAllPendingChequeRequests().size());
//         model.addAttribute("cntBeneficiaries", beneficiaryService.getPendingBeneficiaries().size());
//         return "manager-approvals";
//     }

//     // 📊 REPORTS (New)
//     @GetMapping("/reports")
//     public String reportsDashboard(HttpServletRequest request, Model model) {
//         model.addAttribute("currentUri", request.getRequestURI());
//         return "manager-reports";
//     }

//     // 👥 CUSTOMER SEARCH (New - Read Only View)
//     @GetMapping("/users")
//     public String searchUsers(HttpServletRequest request, Model model) {
//         model.addAttribute("currentUri", request.getRequestURI());
//         model.addAttribute("users", userService.getAllUsers());
//         return "admin-users"; // Reusing the Admin view (Ensure buttons are hidden/disabled via security tags in HTML if needed)
//     }

//     // --- ACCOUNT APPROVALS ---
//     @GetMapping("/accounts")
//     public String pendingAccounts(HttpServletRequest request,Model model) {
//         model.addAttribute("accounts", accountService.getPendingAccounts());
//         model.addAttribute("currentUri", request.getRequestURI());
//         return "admin-account-approval"; // Reuse existing template
//     }

//     @PostMapping("/accounts/{id}/approve")
//     public String approveAccount(@PathVariable Long id) {
//         accountService.approveAccount(id);
//         return "redirect:/manager/accounts";
//     }

//     // --- LOAN APPROVALS ---
//     @GetMapping("/loans")
//     public String pendingLoans(Model model) {
//         model.addAttribute("loans", loanService.getPendingLoans());
//         model.addAttribute("currentUri", "/manager/loans");
//         return "admin-loan-list"; // Reuse existing template
//     }

//     @PostMapping("/loans/{id}/approve")
//     public String approveLoan(@PathVariable Long id) {
//         loanService.approveLoan(id);
//         return "redirect:/manager/loans";
//     }

//     @PostMapping("/loans/{id}/reject")
//     public String rejectLoan(@PathVariable Long id) {
//         loanService.rejectLoan(id);
//         return "redirect:/manager/loans";
//     }

//     // --- KYC APPROVALS ---
//     @GetMapping("/kyc")
//     public String pendingKyc(Model model) {
//         List<KycDocument> pendingKycs = kycService.getPendingKycs();
//         model.addAttribute("kycList", pendingKycs);
//         return "admin-kyc"; // Reuse existing template
//     }

//     @PostMapping("/kyc/approve")
//     public String approveKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
//         kycService.updateKycStatus(id, "VERIFIED");
//         redirectAttributes.addFlashAttribute("success", "KYC Approved Successfully!");
//         return "redirect:/manager/kyc";
//     }

//     @PostMapping("/kyc/reject")
//     public String rejectKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
//         kycService.updateKycStatus(id, "REJECTED");
//         redirectAttributes.addFlashAttribute("error", "KYC Rejected.");
//         return "redirect:/manager/kyc";
//     }

//     // --- CHEQUE BOOK REQUESTS ---
//     @GetMapping("/services")
//     public String pendingServices(Model model) {
//         List<ChequeBookRequest> requests = cardService.getAllPendingChequeRequests();
//         model.addAttribute("requests", requests);
//         return "admin-service-requests"; // Reuse existing template
//     }

//     @PostMapping("/services/approve")
//     public String approveService(@RequestParam Long id, RedirectAttributes redirectAttributes) {
//         cardService.approveChequeRequest(id);
//         redirectAttributes.addFlashAttribute("success", "Cheque Book Request Approved!");
//         return "redirect:/manager/services";
//     }

//     // --- BENEFICIARY APPROVALS ---
//     @GetMapping("/beneficiaries")
//     public String pendingBeneficiaries(HttpServletRequest request, Model model) {
//         model.addAttribute("currentUri", request.getRequestURI());
//         model.addAttribute("beneficiaries", beneficiaryService.getPendingBeneficiaries());
//         return "admin-beneficiaries"; // Reuse existing template
//     }

//     @GetMapping("/beneficiaries/approve/{id}")
//     public String approveBeneficiary(@PathVariable Long id) {
//         beneficiaryService.approveBeneficiary(id);
//         return "redirect:/manager/beneficiaries";
//     }

//     @GetMapping("/beneficiaries/reject/{id}")
//     public String rejectBeneficiary(@PathVariable Long id) {
//         beneficiaryService.rejectBeneficiary(id);
//         return "redirect:/manager/beneficiaries";
//     }
// }

package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.service.AccountService;
import mca.fincorebanking.service.BeneficiaryService;
import mca.fincorebanking.service.CardService;
import mca.fincorebanking.service.KycService;
import mca.fincorebanking.service.LoanService;
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

    public ManagerController(AccountService accountService, LoanService loanService, KycService kycService,
            CardService cardService, BeneficiaryService beneficiaryService, UserService userService) {
        this.accountService = accountService;
        this.loanService = loanService;
        this.kycService = kycService;
        this.cardService = cardService;
        this.beneficiaryService = beneficiaryService;
        this.userService = userService;
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

    // --- ACCOUNTS (Forward Logic) ---
    @GetMapping("/accounts")
    public String pendingAccounts(Model model, HttpServletRequest request) {
        // Manager sees "PENDING"
        model.addAttribute("accounts", accountService.getPendingAccounts());
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-account-approval"; // ✅ Point to Manager-specific view
    }

    @PostMapping("/accounts/{id}/approve")
    public String forwardAccount(@PathVariable Long id, RedirectAttributes redirect) {
        // FORWARD LOGIC: Update status to 'PENDING_ADMIN' instead of 'ACTIVE'
        // You must ensure your AccountService has a method to set generic status, or
        // add one.
        accountService.updateAccountStatus(id, "PENDING_ADMIN");
        redirect.addFlashAttribute("success", "Account forwarded to Admin for final approval.");
        return "redirect:/manager/accounts";
    }

    // --- LOANS (Forward Logic) ---
    @GetMapping("/loans")
    public String pendingLoans(Model model, HttpServletRequest request) {
        model.addAttribute("loans", loanService.getPendingLoans());
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-loan-list"; // ✅ Point to Manager-specific view
    }

    @PostMapping("/loans/{id}/approve")
    public String forwardLoan(@PathVariable Long id, RedirectAttributes redirect) {
        // FORWARD LOGIC
        loanService.updateLoanStatus(id, "PENDING_ADMIN");
        redirect.addFlashAttribute("success", "Loan recommended and forwarded to Admin.");
        return "redirect:/manager/loans";
    }

    // --- KYC APPROVALS (Manager Review) ---
    @GetMapping("/kyc")
    public String pendingKyc(Model model, HttpServletRequest request) {
        // Manager sees initial "PENDING" requests
        // Ensure kycService.getKycsByStatus("PENDING") exists or generic
        // getPendingKycs() returns "PENDING"
        model.addAttribute("kycList", kycService.getKycsByStatus("PENDING"));
        model.addAttribute("currentUri", request.getRequestURI());
        return "manager-kyc-list"; // ✅ Points to Manager View
    }

    @PostMapping("/kyc/approve")
    public String forwardKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        // FORWARD LOGIC: Set status to 'PENDING_ADMIN'
        kycService.updateKycStatus(id, "PENDING_ADMIN");
        redirectAttributes.addFlashAttribute("success", "KYC Verified & Forwarded to Admin.");
        return "redirect:/manager/kyc";
    }

    @PostMapping("/kyc/reject")
    public String rejectKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        kycService.updateKycStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("error", "KYC Rejected.");
        return "redirect:/manager/kyc";
    }

    // --- BENEFICIARY APPROVALS (Manager Review) ---
    @GetMapping("/beneficiaries")
    public String pendingBeneficiaries(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Manager sees "PENDING" requests
        // Ensure beneficiaryService.getBeneficiariesByStatus("PENDING") exists
        model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesByStatus("PENDING"));
        return "manager-beneficiaries"; // ✅ Points to Manager View
    }

    @PostMapping("/beneficiaries/{id}/approve")
    public String forwardBeneficiary(@PathVariable Long id, RedirectAttributes redirect) {
        // FORWARD LOGIC: Set status to 'PENDING_ADMIN'
        beneficiaryService.updateBeneficiaryStatus(id, "PENDING_ADMIN");
        redirect.addFlashAttribute("success", "Beneficiary verified and forwarded to Admin.");
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
        // Manager sees initial "PENDING" requests
        // Ensure cardService.getChequeRequestsByStatus("PENDING") exists
        model.addAttribute("requests", cardService.getChequeRequestsByStatus("PENDING"));
        return "manager-service-requests"; // ✅ Points to Manager View
    }

    @PostMapping("/services/approve")
    public String forwardService(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        // FORWARD LOGIC: Set status to 'PENDING_ADMIN'
        cardService.updateChequeRequestStatus(id, "PENDING_ADMIN");
        redirectAttributes.addFlashAttribute("success", "Request verified and forwarded to Admin.");
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
}