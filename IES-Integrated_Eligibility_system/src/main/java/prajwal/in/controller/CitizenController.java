package prajwal.in.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import prajwal.in.entity.Admin;
import prajwal.in.entity.CaseWorker;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.repo.AdminRepo;
import prajwal.in.repo.CaseWorkerRepo;
import prajwal.in.repo.CitizenRepo;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/application")
public class CitizenController {

    @Autowired
    private CitizenRepo citizenRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    // Show create application form
    @GetMapping("/create")
    public String createCitizen(Model model) {
        model.addAttribute("citizen", new CitizenEntity());
        return "create_application";
    }

    // Save application: support both CaseWorker and Admin
    @PostMapping("/save")
    public String saveCitizen(@ModelAttribute CitizenEntity citizen, HttpSession session, Model model) {
        String ssn = citizen.getSsn();

        // Validate SSN
        if (ssn == null || !ssn.matches("123\\d{5}")) {
            model.addAttribute("error", "Application not acceptable. You are not from Oidiland.");
            model.addAttribute("citizen", citizen);
            return "create_application";
        }

        // Set creator (Admin OR CaseWorker, determined by session)
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("email");

        if ("ADMIN".equals(role)) {
            Optional<Admin> adminOpt = adminRepo.findByEmail(email);
            if (adminOpt.isPresent()) {
                citizen.setCreatedByAdmin(adminOpt.get());
                citizen.setCreatedBy(null); // Explicitly set other to null
            }
        } else if ("CASEWORKER".equals(role)) {
            Optional<CaseWorker> cwOpt = caseWorkerRepo.findByEmail(email);
            if (cwOpt.isPresent()) {
                citizen.setCreatedBy(cwOpt.get());
                citizen.setCreatedByAdmin(null); // Explicitly set other to null
            }
        }

        // Generate 3-digit random case number
        String randomCaseNum = String.valueOf((int) (Math.random() * 900 + 100));
        citizen.setCaseNumber(randomCaseNum);

        citizenRepo.save(citizen);

        return "redirect:/application/view";
    }

    // View applications: only current user's own applications (Admin or CaseWorker)
    @GetMapping("/view")
    public String viewCitizens(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("email");

        if ("ADMIN".equals(role)) {
            Optional<Admin> adminOpt = adminRepo.findByEmail(email);
            if (adminOpt.isPresent()) {
                model.addAttribute("citizens", citizenRepo.findByCreatedByAdmin(adminOpt.get()));
            } else {
                model.addAttribute("citizens", List.of());
            }
        } else if ("CASEWORKER".equals(role)) {
            Optional<CaseWorker> cwOpt = caseWorkerRepo.findByEmail(email);
            if (cwOpt.isPresent()) {
                model.addAttribute("citizens", citizenRepo.findByCreatedBy(cwOpt.get()));
            } else {
                model.addAttribute("citizens", List.of());
            }
        } else {
            // Fallback: show nothing
            model.addAttribute("citizens", List.of());
        }

        return "view_applications";
    }
    @GetMapping("/delete/{id}")
    public String deleteCitizen(@PathVariable Long id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        // Extra safety check — delete only if it belongs to the current logged-in user
        String role = (String) session.getAttribute("role");
        String email = (String) session.getAttribute("email");

        citizenRepo.findById(id).ifPresent(citizen -> {
            boolean canDelete = false;

            if ("ADMIN".equals(role) && citizen.getCreatedByAdmin() != null &&
                    email.equalsIgnoreCase(citizen.getCreatedByAdmin().getEmail())) {
                canDelete = true;
            }
            if ("CASEWORKER".equals(role) && citizen.getCreatedBy() != null &&
                    email.equalsIgnoreCase(citizen.getCreatedBy().getEmail())) {
                canDelete = true;
            }

            if (canDelete) {
                citizenRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("succMsg", "Application deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "You are not authorized to delete this application.");
            }
        });

        return "redirect:/application/view";
    }
}
