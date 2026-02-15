package mca.fincorebanking.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.entity.KycDocument;
import mca.fincorebanking.service.KycService;

@Controller
@RequestMapping("/admin/kyc")
public class AdminKycController {

    private final KycService kycService;

    public AdminKycController(KycService kycService) {
        this.kycService = kycService;
    }

    // 📋 List 'PENDING_ADMIN' KYC Requests (Forwarded by Manager)
    @GetMapping
    public String viewPendingKycs(Model model, HttpServletRequest request) {
        // Ensure you add this method to KycService
        List<KycDocument> pendingKycs = kycService.getKycsByStatus("PENDING_ADMIN");
        model.addAttribute("kycList", pendingKycs);
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin-kyc";
    }

    // ✅ Final Approval
    @PostMapping("/approve")
    public String approveKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        kycService.updateKycStatus(id, "VERIFIED");
        redirectAttributes.addFlashAttribute("success", "KYC Finalized & Verified!");
        return "redirect:/admin/kyc";
    }

    // ❌ Reject
    @PostMapping("/reject")
    public String rejectKyc(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        kycService.updateKycStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("error", "KYC Rejected.");
        return "redirect:/admin/kyc";
    }
}