package prajwal.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import prajwal.in.entity.CitizenInfo;
import prajwal.in.repo.CitizenInfoRepo;
import prajwal.in.repo.PlanRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/citizen-plan")
public class CitizenPlanController {

    @Autowired
    private CitizenInfoRepo citizenInfoRepo;

    @Autowired
    private PlanRepo planRepo;

    @GetMapping("/select/{caseNumber}")
    public String showPlanSelection(@PathVariable String caseNumber, Model model) {
        model.addAttribute("caseNumber", caseNumber);
        model.addAttribute("plans", planRepo.findAll());

        List<CitizenInfo> appliedPlans = citizenInfoRepo.findAllByCaseNumber(caseNumber);
        if (appliedPlans == null) {
            appliedPlans = new ArrayList<>();
        }
        model.addAttribute("appliedPlans", appliedPlans);

        return "dc_plan_selection";
    }

    @PostMapping("/apply")
    public String applyForPlan(@RequestParam String caseNumber,
                               @RequestParam String planName,
                               RedirectAttributes redirectAttributes) {

        // Validate
        if (planName == null || planName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errMsg", "Please select a plan before continuing.");
            return "redirect:/citizen-plan/select/" + caseNumber;
        }

        // Check duplicate
        Optional<CitizenInfo> existingApplication =
                citizenInfoRepo.findByCaseNumberAndSelectedPlan(caseNumber.trim(), planName.trim());
        if (existingApplication.isPresent()) {
            redirectAttributes.addFlashAttribute("errMsg", "This plan is already applied for this case.");
            return "redirect:/citizen-plan/select/" + caseNumber;
        }

        // Existing plans for citizen
        List<CitizenInfo> existingPlans = citizenInfoRepo.findAllByCaseNumber(caseNumber.trim());

        CitizenInfo newApplication = new CitizenInfo();
        newApplication.setCaseNumber(caseNumber.trim());
        newApplication.setSelectedPlan(planName.trim());

        if (!existingPlans.isEmpty()) {
            CitizenInfo copySource = existingPlans.get(0);
            newApplication.setMonthlySalary(copySource.getMonthlySalary());
            newApplication.setRentIncome(copySource.getRentIncome());
            newApplication.setPropertyIncome(copySource.getPropertyIncome());
            newApplication.setHighestEducation(copySource.getHighestEducation());
            newApplication.setGraduationYear(copySource.getGraduationYear());
            newApplication.setUniversityOrSchool(copySource.getUniversityOrSchool());
        }

        citizenInfoRepo.save(newApplication);

        redirectAttributes.addFlashAttribute("succMsg", "Plan applied successfully: " + planName);

        if (existingPlans.isEmpty()) {
            return "redirect:/dc/income-manual?caseNumber=" + caseNumber;
        } else {
            return "redirect:/application/view";
        }
    }

    @GetMapping("/delete/{id}/{caseNumber}")
    public String deleteAppliedPlan(@PathVariable Long id,
                                    @PathVariable String caseNumber,
                                    RedirectAttributes redirectAttributes) {
        citizenInfoRepo.deleteById(id);
        redirectAttributes.addFlashAttribute("succMsg", "Applied plan deleted successfully.");
        return "redirect:/citizen-plan/select/" + caseNumber;
    }
}
