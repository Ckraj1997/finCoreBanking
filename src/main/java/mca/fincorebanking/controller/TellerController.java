package mca.fincorebanking.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.entity.Account;
import mca.fincorebanking.entity.TellerTransaction;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.service.AccountService;
import mca.fincorebanking.service.TellerService;
import mca.fincorebanking.service.UserService;

@Controller
@RequestMapping("/teller")
public class TellerController {

    private final TellerService tellerService;
    private final AccountService accountService;
    private final UserService userService;

    public TellerController(TellerService tellerService, AccountService accountService, UserService userService) {
        this.tellerService = tellerService;
        this.accountService = accountService;
        this.userService = userService;
    }

    // 🏦 Dashboard: Search Customer & View History
    @GetMapping
    public String showTellerDashboard(Model model, Principal principal,
            HttpServletRequest request,
            @RequestParam(required = false) String searchAccountNo) {

        String username = principal.getName();
        User teller = userService.findByUsername(username);

        // 1. Setup Standard Dashboard Context (Required for dashboard.html wrapper)
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("userRole", "ROLE_TELLER");
        model.addAttribute("username", username);

        // 2. Handle Search (If performed)
        if (searchAccountNo != null && !searchAccountNo.isEmpty()) {
            Account customerAccount = accountService.findByAccountNumber(searchAccountNo);
            if (customerAccount != null) {
                model.addAttribute("customerAccount", customerAccount);
            } else {
                model.addAttribute("error", "Account not found!");
            }
            // Keep the search term in the input box
            model.addAttribute("searchAccountNo", searchAccountNo);
        }

        // 3. Load Teller Data (Matches names in dashboard-views.html fragment)
        List<TellerTransaction> history = tellerService.getTellerHistory(teller.getId());
        model.addAttribute("todayTransactions", history); // Fragment expects 'todayTransactions'

        // Mock Data for Dashboard Cards (You can replace these with real DB queries
        // later)
        model.addAttribute("cashInDrawer", 450000.00);
        model.addAttribute("customersServed", history.size());

        // ✅ Return the global dashboard template
        return "dashboard";
    }

    // 📜 2. History Page (Fixes the 404 Error)
    @GetMapping("/history")
    public String showTellerHistory(Model model, Principal principal, HttpServletRequest request) {
        String username = principal.getName();
        User teller = userService.findByUsername(username);

        // Context
        model.addAttribute("currentUri", request.getRequestURI()); // This will be "/teller/history"
        model.addAttribute("userRole", "ROLE_TELLER");
        model.addAttribute("username", username);

        // Load Full History
        List<TellerTransaction> history = tellerService.getTellerHistory(teller.getId());
        model.addAttribute("fullHistory", history);

        return "teller-history";
    }

    // 💰 Perform Deposit
    @PostMapping("/deposit")
    public String performDeposit(@RequestParam String accountNumber,
            @RequestParam Double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User teller = userService.findByUsername(principal.getName());
            tellerService.depositCash(teller.getId(), accountNumber, amount);
            redirectAttributes.addFlashAttribute("success", "Cash Deposited Successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        // Redirect keeps search parameter active so user stays on same customer
        return "redirect:/teller?searchAccountNo=" + accountNumber;
    }

    // 💸 Perform Withdrawal
    @PostMapping("/withdraw")
    public String performWithdrawal(@RequestParam String accountNumber,
            @RequestParam Double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            User teller = userService.findByUsername(principal.getName());
            tellerService.withdrawCash(teller.getId(), accountNumber, amount);
            redirectAttributes.addFlashAttribute("success", "Cash Withdrawn Successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/teller?searchAccountNo=" + accountNumber;
    }
}