package mca.fincorebanking.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.entity.Loan;
import mca.fincorebanking.service.LoanInterestRateService;
import mca.fincorebanking.service.LoanService;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final LoanInterestRateService loanInterestRateService;

    public LoanController(LoanService loanService, LoanInterestRateService loanInterestRateService) {
        this.loanService = loanService;
        this.loanInterestRateService = loanInterestRateService;
    }

    @GetMapping
    public String loanList(Authentication auth, Model model, HttpServletRequest request) {
        List<Loan> loans = loanService.getLoansByUser(auth.getName());
        for (Loan loan : loans) {
            if ("APPROVED".equals(loan.getStatus())) {
                double emi = loanService.calculateEmi(
                        loan.getAmount(),
                        loan.getInterestRate(),
                        loan.getTenureMonths());
                loan.setEmi(emi); // transient / DTO usage
            }
        }

        model.addAttribute("loans",
                loans);
        model.addAttribute("currentUri", request.getRequestURI());
        return "loan-list";
    }

    @GetMapping("/apply")
    public String applyPage(Model model) {
        model.addAttribute("rates", loanInterestRateService.findAll());
        return "loan-apply";
    }

    @PostMapping("/apply")
    public String applyLoan(
            Authentication auth,
            @RequestParam String loanType,
            @RequestParam Double amount,
            @RequestParam Integer tenureMonths,
            RedirectAttributes redirect) {

        try {
            loanService.applyLoan(
                    auth.getName(),
                    loanType,
                    amount,
                    tenureMonths);
            redirect.addFlashAttribute("success",
                    "Loan application submitted");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/loans";
    }

    @GetMapping("/emi")
    public String emiCalculator(
            @RequestParam(required = false) String loanType,
            @RequestParam(required = false) Double amount,
            @RequestParam(required = false) Integer tenure,
            Model model) {

        // 1. Load only ACTIVE loan types for dropdown
        model.addAttribute(
                "loanTypes",
                loanInterestRateService.getActiveLoanTypes());

        // 2. If user submitted form → calculate EMI
        if (loanType != null && amount != null && tenure != null) {

            double rate = loanInterestRateService.getInterestRate(loanType);

            double emi = loanService.calculateEmi(
                    amount,
                    rate,
                    tenure);

            model.addAttribute("selectedLoanType", loanType);
            model.addAttribute("amount", amount);
            model.addAttribute("tenure", tenure);
            model.addAttribute("rate", rate);
            model.addAttribute("emi", emi);
        }

        model.addAttribute("currentUri", "/loans/emi");
        return "loan-emi";
    }

}
