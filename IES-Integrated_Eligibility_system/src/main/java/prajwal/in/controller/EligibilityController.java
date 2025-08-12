package prajwal.in.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prajwal.in.dto.EligibilityResultDTO;
import prajwal.in.service.EligibilityService;
import prajwal.in.model.CaseNumberForm;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    // From Process button
    @GetMapping("/determine/{caseNumber}")
    public String determineEligibility(@PathVariable String caseNumber, Model model) {
        List<EligibilityResultDTO> results = eligibilityService.determineEligibility(caseNumber);
        model.addAttribute("results", results);
        return "eligibility-result"; 
    }

    // Manual dashboard check
    @GetMapping("/check")
    public String showCheckForm(@RequestParam(required = false) String caseNumber, Model model) {
        CaseNumberForm form = new CaseNumberForm();
        if (caseNumber != null && !caseNumber.isEmpty()) {
            form.setCaseNumber(caseNumber); // Pre-fill if provided
        }
        model.addAttribute("caseForm", form);
        return "eligibility-check-form"; 
    }

    @PostMapping("/check")
    public String checkEligibility(@ModelAttribute CaseNumberForm caseForm, Model model) {
        List<EligibilityResultDTO> results = eligibilityService.determineEligibility(caseForm.getCaseNumber());
        model.addAttribute("results", results);
        return "eligibility-result"; 
    }

    // View previous results without recalculation
    @GetMapping("/view/{caseNumber}")
    public String viewEligibility(@PathVariable String caseNumber, Model model) {
        List<EligibilityResultDTO> results = eligibilityService.getEligibilityResults(caseNumber);
        model.addAttribute("results", results);
        return "eligibility-result";
    }
}
