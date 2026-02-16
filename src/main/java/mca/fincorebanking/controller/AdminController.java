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
import mca.fincorebanking.service.LoanInterestRateService;
import mca.fincorebanking.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final LoanInterestRateService loanInterestRateService;

    public AdminController(UserService userService,
            LoanInterestRateService loanInterestRateService) {
        this.userService = userService;
        this.loanInterestRateService = loanInterestRateService;
    }

    @GetMapping("/users")
    public String viewUsers(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("users", userService.getAllUsers());
        return "admin-users"; // ✅ Points to the fixed HTML below
    }

    @GetMapping("/users/block/{id}")
    public String blockUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.blockUser(id);
            redirect.addFlashAttribute("success", "User blocked successfully.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error blocking user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/unblock/{id}")
    public String unblockUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.unblockUser(id);
            redirect.addFlashAttribute("success", "User unblocked successfully.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error unblocking user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/register")
    public String registerForm(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("roles", Role.values()); // ✅ Passes Roles enum to view
        model.addAttribute("user", new User());
        return "admin-register"; // ✅ Points to the fixed HTML below
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirect) {
        try {
            userService.saveUser(user);
            redirect.addFlashAttribute("success", "User registered successfully!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/admin/register";
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/loan-types")
    public String loanConfigPage(HttpServletRequest request, Model model) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("loanTypes", loanInterestRateService.getAllLoanTypes());
        return "admin-loan-rate"; // ✅ Points to the fixed HTML below
    }

    @PostMapping("/loan-types/add")
    public String addLoanType(@RequestParam String loanType, @RequestParam Double rate, RedirectAttributes redirect) {
        try {
            loanInterestRateService.addLoanType(loanType, rate);
            redirect.addFlashAttribute("success", "Loan type added successfully.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/loan-types";
    }

    @PostMapping("/loan-types/update")
    public String updateLoanType(@RequestParam Long id, @RequestParam Double rate,
            @RequestParam(defaultValue = "false") boolean active,
            RedirectAttributes redirect) {
        try {
            loanInterestRateService.updateLoanType(id, rate, active);
            redirect.addFlashAttribute("success", "Loan type updated.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/admin/loan-types";
    }

    @PostMapping("/loan-types/delete")
    public String deleteLoanType(@RequestParam Long id, RedirectAttributes redirect) {
        try {
            loanInterestRateService.deleteLoanType(id);
            redirect.addFlashAttribute("success", "Loan type deleted.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return "redirect:/admin/loan-types";
    }
}