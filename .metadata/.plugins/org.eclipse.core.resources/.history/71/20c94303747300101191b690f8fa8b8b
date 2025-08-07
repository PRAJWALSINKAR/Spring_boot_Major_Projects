package prajwal.in.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prajwal.in.entity.CaseWorker;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.repo.CitizenRepo;

import java.util.List;

@Controller
@RequestMapping("/application")
public class CitizenController {

    @Autowired
    private CitizenRepo citizenRepo;

    // Show create application form
    @GetMapping("/create")
    public String createCitizen(Model model) {
        model.addAttribute("citizen", new CitizenEntity());
        return "create_application";
    }

    // Save application with SSN validation and CaseWorker binding
    @PostMapping("/save")
    public String saveCitizen(@ModelAttribute CitizenEntity citizen, HttpSession session, Model model) {
        String ssn = citizen.getSsn();

        // ✅ SSN must start with "123" and be 8 digits total
        if (ssn == null || !ssn.matches("123\\d{5}")) {
            model.addAttribute("error", "Application not acceptable. You are not from Oidiland.");
            model.addAttribute("citizen", citizen);
            return "create_application";
        }

        // ✅ Set CaseWorker (creator) from session
        Long caseWorkerId = (Long) session.getAttribute("userId");
        if (caseWorkerId != null) {
            CaseWorker cw = new CaseWorker();
            cw.setId(caseWorkerId);
            citizen.setCreatedBy(cw);
        }

        // ✅ Generate 3-digit random case number
        String randomCaseNum = String.valueOf((int) (Math.random() * 900 + 100));
        citizen.setCaseNumber(randomCaseNum);

        // ✅ Save the citizen application
        citizenRepo.save(citizen);

        return "redirect:/application/view";
    }

    // View only those applications created by logged-in CaseWorker
    @GetMapping("/view")
    public String viewCitizens(Model model, HttpSession session) {
        Long caseWorkerId = (Long) session.getAttribute("userId");

        if (caseWorkerId != null) {
            CaseWorker cw = new CaseWorker();
            cw.setId(caseWorkerId);

            List<CitizenEntity> list = citizenRepo.findByCreatedBy(cw);
            model.addAttribute("citizens", list);
        } else {
            model.addAttribute("citizens", List.of());
        }

        return "view_applications";
    }
}
