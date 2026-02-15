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
import mca.fincorebanking.entity.ChequeBookRequest;
import mca.fincorebanking.service.CardService;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final CardService cardService;

    public AdminServiceController(CardService cardService) {
        this.cardService = cardService;
    }

    // 📋 List 'PENDING_ADMIN' Requests
    @GetMapping
    public String viewPendingRequests(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Admin sees items forwarded by Manager
        List<ChequeBookRequest> requests = cardService.getChequeRequestsByStatus("PENDING_ADMIN");
        model.addAttribute("requests", requests);
        return "admin-service-requests";
    }

    // ✅ Final Approve
    @PostMapping("/approve")
    public String approveRequest(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            // Final Approval -> APPROVED (Dispatch process initiated)
            cardService.approveChequeRequest(id);
            redirectAttributes.addFlashAttribute("success", "Request Finalized & Approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    // ❌ Reject
    @PostMapping("/reject")
    public String rejectRequest(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        cardService.updateChequeRequestStatus(id, "REJECTED");
        redirectAttributes.addFlashAttribute("error", "Request Rejected.");
        return "redirect:/admin/services";
    }
}