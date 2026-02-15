package mca.fincorebanking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.service.BeneficiaryService;

@Controller
@RequestMapping("/admin/beneficiaries")
public class AdminBeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public AdminBeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    // 📋 List 'PENDING_ADMIN' Beneficiaries
    @GetMapping
    public String viewPendingBeneficiaries(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Admin sees items forwarded by Manager
        model.addAttribute("beneficiaries", beneficiaryService.getBeneficiariesByStatus("PENDING_ADMIN"));
        return "admin-beneficiaries";
    }

    // ✅ Final Approve
    @PostMapping("/{id}/approve")
    public String approveBeneficiary(@PathVariable Long id, RedirectAttributes redirect) {
        beneficiaryService.updateBeneficiaryStatus(id, "ACTIVE");
        redirect.addFlashAttribute("success", "Beneficiary Activated Successfully!");
        return "redirect:/admin/beneficiaries";
    }

    // ❌ Reject
    @PostMapping("/{id}/reject")
    public String rejectBeneficiary(@PathVariable Long id, RedirectAttributes redirect) {
        beneficiaryService.updateBeneficiaryStatus(id, "REJECTED");
        redirect.addFlashAttribute("error", "Beneficiary Rejected.");
        return "redirect:/admin/beneficiaries";
    }
}