package mca.fincorebanking.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mca.fincorebanking.entity.KycDocument;
import mca.fincorebanking.entity.User;
import mca.fincorebanking.service.KycService;
import mca.fincorebanking.service.UserService;

@Controller
@RequestMapping("/kyc")
public class KycController {

    private final KycService kycService;
    private final UserService userService;

    // ✅ Constructor Injection (No Repository here!)
    public KycController(KycService kycService, UserService userService) {
        this.kycService = kycService;
        this.userService = userService;
    }

    // 📄 1. Show KYC Page
    @GetMapping
    public String showKycPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        // ✅ Call Service instead of Repository
        Optional<KycDocument> existingKyc = kycService.getKycByUser(user);

        if (existingKyc.isPresent()) {
            model.addAttribute("kyc", existingKyc.get());
            return "kyc-status"; // Page 1: Status View
        } else {
            return "kyc-form"; // Page 2: Upload Form
        }
    }

    // 📤 2. Handle Document Upload
    @PostMapping("/upload")
    public String uploadKyc(@RequestParam("docType") String docType,
            @RequestParam("docNumber") String docNumber,
            @RequestParam("file") MultipartFile file,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User user = userService.findByUsername(username);

            kycService.uploadKycDocument(user.getId(), docType, docNumber, file);

            redirectAttributes.addFlashAttribute("success",
                    "KYC Document uploaded successfully! Pending verification.");
            return "redirect:/kyc";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
            return "redirect:/kyc";
        }
    }
}