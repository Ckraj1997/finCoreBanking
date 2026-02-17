package mca.fincorebanking.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import mca.fincorebanking.entity.Transaction;
import mca.fincorebanking.service.AccountService;
import mca.fincorebanking.service.PdfService;
import mca.fincorebanking.service.ReportService;

@Controller
@RequestMapping("/reports")
public class ReportController {

        private final ReportService reportService;
        private final PdfService pdfService;
        private final AccountService accountService; // Ensure this is there

        public ReportController(ReportService reportService, PdfService pdfService, AccountService accountService) {
                this.reportService = reportService;
                this.pdfService = pdfService;
                this.accountService = accountService;
        }

        @GetMapping("/statement")
        public String statementForm(HttpServletRequest request, Model model) {
                model.addAttribute("currentUri", request.getRequestURI());
                return "statement-form";
        }

        @PostMapping("/statement")
        public String generateStatement(
                        @RequestParam String accountNumber,
                        @RequestParam String fromDate,
                        @RequestParam String toDate,
                        Authentication authentication,
                        HttpServletRequest request,
                        Model model) {

                LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
                LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);

                List<Transaction> transactions = reportService.getAccountStatement(
                                authentication.getName(),
                                accountNumber,
                                from,
                                to);

                model.addAttribute("transactions", transactions);
                model.addAttribute("currentUri", request.getRequestURI());

                return "statement-view";
        }

        // ... existing code ...

        // 📄 Download Compliance/Audit Report
        @GetMapping("/audit")
        public String downloadAuditReport(HttpServletRequest request, Model model) {
                // In a real app, this would generate a PDF/CSV stream.
                // For the MVP, we will show a "Report Generated" confirmation page.
                model.addAttribute("currentUri", request.getRequestURI());
                model.addAttribute("reportType", "Compliance & Audit Trail");
                model.addAttribute("generatedDate", java.time.LocalDateTime.now());
                return "report-download-success"; // You'll need this simple view
        }

        @PostMapping("/statement/download")
        public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> downloadStatement(
                        @RequestParam String accountNumber) {

                mca.fincorebanking.entity.Account account = accountService.findByAccountNumber(accountNumber);
                java.util.List<mca.fincorebanking.entity.Transaction> transactions = accountService
                                .getRecentTransactions(account.getId());

                java.io.ByteArrayInputStream bis = pdfService.generateAccountStatement(account, transactions);

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=statement-" + accountNumber + ".pdf");

                return org.springframework.http.ResponseEntity
                                .ok()
                                .headers(headers)
                                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                                .body(new org.springframework.core.io.InputStreamResource(bis));
        }
}
