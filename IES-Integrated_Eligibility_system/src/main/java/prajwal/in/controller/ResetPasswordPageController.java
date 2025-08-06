package prajwal.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import prajwal.in.entity.CaseWorker;
import prajwal.in.service.CaseWorkerService;

@Controller
public class ResetPasswordPageController {

    @Autowired
    private CaseWorkerService caseWorkerService;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        CaseWorker worker = caseWorkerService.findByResetToken(token);
        if (worker == null) {
            model.addAttribute("error", "Invalid token");
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @RequestParam("tempPassword") String tempPwd,
            @RequestParam("newPassword") String newPwd,
            Model model) {

        CaseWorker worker = caseWorkerService.findByResetToken(token);
        if (worker == null) {
            model.addAttribute("error", "Invalid token");
            return "reset-password";
        }

        if (!tempPwd.equals(worker.getTempPassword())) {
            model.addAttribute("error", "Incorrect temporary password");
            model.addAttribute("token", token);
            return "reset-password";
        }

        worker.setPassword(newPwd);
        worker.setTempPassword(null);
        worker.setResetToken(null);
        worker.setAccStatus("ACTIVE");

        caseWorkerService.updateCaseWorker(worker);

        model.addAttribute("message", "Password reset successful");
        return "reset-password";
    }
}
