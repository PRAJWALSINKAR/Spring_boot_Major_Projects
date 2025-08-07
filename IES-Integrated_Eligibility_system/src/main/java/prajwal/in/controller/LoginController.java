package prajwal.in.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import prajwal.in.entity.Admin;
import prajwal.in.entity.CaseWorker;
import prajwal.in.repo.AdminRepo;
import prajwal.in.repo.CaseWorkerRepo;
import prajwal.in.util.EmailUtils;

import java.util.Optional;

@Controller
public class LoginController {

    private final AdminRepo adminRepo;
    private final CaseWorkerRepo caseWorkerRepo;
    private final EmailUtils emailUtils;

    @Autowired
    public LoginController(AdminRepo adminRepo, CaseWorkerRepo caseWorkerRepo, EmailUtils emailUtils) {
        this.adminRepo = adminRepo;
        this.caseWorkerRepo = caseWorkerRepo;
        this.emailUtils = emailUtils;
    }

    // Show Login Page
    @GetMapping({"/", "/login"})
    public String showLoginPage() {
        return "login"; // Thymeleaf template: login.html
    }

    // Process Login Submission
    @PostMapping("/login")
    public String loginCheck(@RequestParam String email,
                             @RequestParam String password,
                             Model model,
                             HttpSession session) {

        // Check if user is an Admin
        Optional<Admin> adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (password.equals(admin.getPassword())) {
                session.setAttribute("email", email);
                session.setAttribute("userName", admin.getName());
                session.setAttribute("role", "ADMIN");
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
        }

        // Check if user is a CaseWorker
        Optional<CaseWorker> workerOpt = caseWorkerRepo.findByEmail(email);
        if (workerOpt.isPresent()) {
            CaseWorker worker = workerOpt.get();

            if (worker.getPassword() == null) {
                model.addAttribute("error", "Your password is not set. Please check your email to reset it.");
                return "login";
            }

            if (!"ACTIVE".equalsIgnoreCase(worker.getAccStatus())) {
                model.addAttribute("error", "Your account is locked. Please reset your password to activate it or contact admin.");
                return "login";
            }

            if (password.equals(worker.getPassword())) {
                session.setAttribute("email", email);
                session.setAttribute("userName", worker.getName());
                session.setAttribute("userId", worker.getId());
                session.setAttribute("role", "CASEWORKER");
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
        }

        // User not found
        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    // Logout and invalidate session
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Show Forgot Password Page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; // Thymeleaf template: forgot-password.html
    }

    // Handle Forgot Password Post
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email, Model model) {
        // Check Admin
        Optional<Admin> adminOpt = adminRepo.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            sendPasswordEmail(admin.getEmail(), admin.getName(), admin.getPassword(), "Admin Account Password");
            model.addAttribute("msg", "Password sent to your registered email.");
            return "forgot-password";
        }

        // Check CaseWorker
        Optional<CaseWorker> workerOpt = caseWorkerRepo.findByEmail(email);
        if (workerOpt.isPresent()) {
            CaseWorker worker = workerOpt.get();
            sendPasswordEmail(worker.getEmail(), worker.getName(), worker.getPassword(), "CaseWorker Account Password");
            model.addAttribute("msg", "Password sent to your registered email.");
            return "forgot-password";
        }

        // Email not found
        model.addAttribute("error", "Invalid Email ID");
        return "forgot-password";
    }

    // Helper method to send password emails
    private void sendPasswordEmail(String toEmail, String userName, String password, String subjectSuffix) {
        String body = "<h3>Hi " + userName + ",</h3>"
                + "<p>Your current password is: <strong>" + password + "</strong></p>"
                + "<p><a href='http://localhost:8080/login'>Click here to Login</a></p>";
        emailUtils.sendEmail(toEmail, "Your " + subjectSuffix, body);
    }
}
