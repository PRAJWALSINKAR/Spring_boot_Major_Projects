package prajwal.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import prajwal.in.entity.CaseWorker;
import prajwal.in.repo.CaseWorkerRepo;

import java.time.LocalDateTime;

@Controller
public class ResetController {

    @Autowired
    private CaseWorkerRepo repo;

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        CaseWorker worker = repo.findByResetToken(token).orElse(null);

        if (worker == null || worker.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processReset(@RequestParam String token,
                               @RequestParam String tempPassword,
                               @RequestParam String newPassword,
                               Model model) {
        CaseWorker worker = repo.findByResetToken(token).orElse(null);

        if (worker == null || worker.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }

        if (!worker.getTempPassword().equals(tempPassword)) {
            model.addAttribute("error", "Temporary password does not match.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        // Update actual password and clear temp data
        worker.setPassword(newPassword);
        worker.setPasswordResetRequired(false);
        worker.setTempPassword(null);
        worker.setResetToken(null);
        worker.setResetTokenExpiry(null);
        repo.save(worker);

        model.addAttribute("message", "Password reset successful! You can now log in.");
        return "redirect:/login";
    }
}
