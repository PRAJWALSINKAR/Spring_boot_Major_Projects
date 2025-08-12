package prajwal.in.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prajwal.in.service.BenefitTransactionService;
import prajwal.in.dto.BenefitTransactionDTO;

import java.util.List;

@Controller
@RequestMapping("/bi")
public class BenefitTransactionController {

    @Autowired
    private BenefitTransactionService transactionService;

    @GetMapping("/manage")
    public String managePage(@RequestParam(required = false) String caseNumber, Model model) {
        List<BenefitTransactionDTO> transactions = null;
        if (caseNumber != null && !caseNumber.isBlank()) {
            transactions = transactionService.getTransactionsByCaseNumber(caseNumber.trim());
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("caseNumber", caseNumber);
        return "bi_manage";
    }

    @GetMapping("/send-money")
    public String sendMoney() {
        transactionService.sendMoneyToAllApproved(); // ✅ Transactional method called here
        return "redirect:/dashboard?success=moneySent";
    }
}
