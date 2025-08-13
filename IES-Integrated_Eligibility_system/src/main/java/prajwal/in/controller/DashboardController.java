package prajwal.in.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import prajwal.in.entity.CaseWorker;
import prajwal.in.service.CaseWorkerService;
import prajwal.in.service.DashboardService;
import prajwal.in.service.ApplicationService;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private CaseWorkerService caseWorkerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        // Redirect to login if no session
        if (email == null || role == null) {
            return "redirect:/login";
        }

        boolean isAdmin = "ADMIN".equals(role);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("session", session); // For Thymeleaf conditional role rendering

        // === Fetch Dashboard Stats ===
        model.addAttribute("planCount", dashboardService.getPlanCount());
        model.addAttribute("citizensApproved", dashboardService.getCitizensApproved());
        model.addAttribute("citizensDenied", dashboardService.getCitizensDenied());
        model.addAttribute("benefitsGiven", dashboardService.getBenefitsGiven());

        return "dashboard"; // Thymeleaf dashboard.html
    }

    @GetMapping("/api/applications")
    @ResponseBody
    public List<Object> getApplications(HttpSession session) {
        String email = (String) session.getAttribute("email");
        String role = (String) session.getAttribute("role");

        if (email == null || role == null) {
            return List.of();  // empty list if not logged in
        }

        if ("ADMIN".equals(role)) {
            return applicationService.findAllApplications();
        } 
        else if ("CASEWORKER".equals(role)) {
            CaseWorker worker = caseWorkerService.findByEmail(email).orElse(null);
            if (worker != null) {
                return applicationService.findApplicationsByCaseWorkerId(worker.getId());
            }
        }

        return List.of();
    }
}
