package prajwal.in.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.entity.CaseWorker;
import prajwal.in.service.CaseWorkerService;

@Controller
@RequestMapping("/caseworker")
public class CaseWorkerController {

    @Autowired
    private CaseWorkerService caseWorkerService;

    // ADMIN: show registration form to register a new caseworker
    @GetMapping("/register-form")
    public String showRegistrationForm(Model model) {
        model.addAttribute("caseworker", new CaseWorkerRequest());
        return "register-caseworker";
    }

    // ADMIN: saves new caseworker from registration form
    @PostMapping("/save")
    public String saveCaseworker(@ModelAttribute("caseworker") CaseWorkerRequest form, Model model) {
        boolean isSaved = caseWorkerService.registerCaseWorker(form);

        if (isSaved) {
            model.addAttribute("succMsg", "Caseworker account created. Check email for temp password.");
        } else {
            model.addAttribute("errMsg", "Failed to create caseworker. Email may already exist.");
        }

        return "register-caseworker";
    }

    // ADMIN: view/search all caseworkers
    @GetMapping("/view")
    public String viewCaseworkers(
        @RequestParam(value = "email", required = false) String email,
        Model model
    ) {
        List<CaseWorker> list = (email != null && !email.isEmpty())
            ? caseWorkerService.searchByEmail(email)
            : caseWorkerService.findAll();

        model.addAttribute("caseworkers", list);
        return "view-caseworkers";
    }

    // ADMIN: delete a caseworker account
    @GetMapping("/delete/{id}")
    public String deleteCaseworker(@PathVariable Long id) {
        caseWorkerService.deleteCaseWorker(id);
        return "redirect:/caseworker/view";
    }

    // ADMIN: toggle status (active/locked) for a caseworker
    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id) {
        caseWorkerService.toggleCaseWorkerStatus(id);
        return "redirect:/caseworker/view";
    }

    // ADMIN: explicit edit (for admin editing ANY caseworker)
    @GetMapping("/edit/{id}")
    public String editCaseworker(@PathVariable Long id, Model model) {
        Optional<CaseWorker> cw = caseWorkerService.findById(id);
        if (cw.isPresent()) {
            model.addAttribute("caseworker", cw.get());
            return "edit-caseworker";
        } else {
            return "redirect:/caseworker/view";
        }
    }

    // ADMIN: update caseworker info (via edit form)
    @PostMapping("/update")
    public String updateCaseworker(@ModelAttribute("caseworker") CaseWorker worker, HttpSession session) {
        caseWorkerService.updateCaseWorker(worker);

        // If this was SELF-edit by a caseworker, redirect to dashboard
        if ("CASEWORKER".equals(session.getAttribute("role"))) {
            return "redirect:/dashboard";
        }
        // Else, redirect admin to the list view
        return "redirect:/caseworker/view";
    }

    // CASEWORKER: edit own profile ("/caseworker/edit" via Profile menu)
    // Shows edit page pre-filled with current user's data
    @GetMapping("/edit")
    public String editProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<CaseWorker> optionalWorker = caseWorkerService.findById(userId);
        if (optionalWorker.isPresent()) {
            model.addAttribute("caseworker", optionalWorker.get());
            return "edit-caseworker";
        } else {
            // If somehow user id not found--redirect to dashboard with a message
            model.addAttribute("error", "Profile not found.");
            return "redirect:/dashboard";
        }
    }
}
