package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prajwal.in.dto.BenefitTransactionDTO;
import prajwal.in.entity.BenefitTransaction;
import prajwal.in.entity.CitizenInfo;
import prajwal.in.entity.EligibilityResult;
import prajwal.in.repo.BenefitTransactionRepo;
import prajwal.in.repo.CitizenInfoRepo;
import prajwal.in.repo.EligibilityResultRepo;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class BenefitTransactionServiceImpl implements BenefitTransactionService {

    @Autowired
    private BenefitTransactionRepo transactionRepo;

    @Autowired
    private CitizenInfoRepo citizenInfoRepo;

    @Autowired
    private EligibilityResultRepo eligibilityResultRepo;

    @Override
    public List<BenefitTransactionDTO> getTransactionsByCaseNumber(String caseNumber) {
        List<BenefitTransaction> transactions = transactionRepo.findByCaseNumber(caseNumber);
        return transactions.stream()
                .map(tx -> new BenefitTransactionDTO(
                        tx.getId(),
                        tx.getEdgTraceId(),
                        tx.getCaseNumber(),
                        tx.getPlanName(),
                        tx.getPlanStatus(),
                        tx.getBenefitAmount(),
                        tx.getAccountNumber(),
                        tx.getStartDate(),
                        tx.getEndDate(),
                        tx.getTransactionDate(),
                        tx.getTransactionStatus(),
                        tx.getTotalAmountReceived()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendMoneyToAllApproved() {
        // Fetch approved plans (case-insensitive) and drop null/blank case numbers
        List<EligibilityResult> approvedPlans = eligibilityResultRepo.findAll().stream()
                .filter(er -> er.getPlanStatus() != null && er.getPlanStatus().trim().equalsIgnoreCase("Approved"))
                .filter(er -> er.getCaseNumber() != null && !er.getCaseNumber().isBlank())
                .collect(Collectors.toList());

        if (approvedPlans.isEmpty()) {
            System.out.println("[SendMoney] No approved plans found.");
            return;
        }

        // Group by trimmed case number
        Map<String, List<EligibilityResult>> groupedByCase =
                approvedPlans.stream()
                             .collect(Collectors.groupingBy(er -> er.getCaseNumber().trim()));

        for (Map.Entry<String, List<EligibilityResult>> entry : groupedByCase.entrySet()) {
            String caseNumber = entry.getKey();
            List<EligibilityResult> plans = entry.getValue();

            // --- FIXED: pick a CitizenInfo that actually has an accountNumber
            Optional<CitizenInfo> optInfo = citizenInfoRepo.findAllByCaseNumber(caseNumber).stream()
                    .filter(ci -> ci.getAccountNumber() != null && !ci.getAccountNumber().isBlank())
                    // prefer the latest record (highest id) if multiple exist
                    .sorted(Comparator.comparing(CitizenInfo::getId).reversed())
                    .findFirst();

            if (optInfo.isEmpty()) {
                System.out.printf("[SendMoney] Skipping case %s: no account number.%n", caseNumber);
                continue;
            }
            String accountNumber = optInfo.get().getAccountNumber().trim();

            for (EligibilityResult plan : plans) {
                double planAmount = plan.getBenefitAmount() != null ? plan.getBenefitAmount() : 0.0;

                // calculate cumulative total received so far **for this plan**
                double previousTotal = transactionRepo.findByCaseNumber(caseNumber).stream()
                        .filter(tx -> tx.getPlanName() != null
                                && tx.getPlanName().equalsIgnoreCase(plan.getPlanName()))
                        .mapToDouble(tx -> tx.getBenefitAmount() != null ? tx.getBenefitAmount() : 0.0)
                        .sum();

                double newTotal = previousTotal + planAmount;

                BenefitTransaction tx = new BenefitTransaction();
                tx.setCaseNumber(caseNumber);
                tx.setPlanName(plan.getPlanName());
                tx.setPlanStatus(plan.getPlanStatus());
                tx.setBenefitAmount(planAmount); // this payment's amount
                tx.setAccountNumber(accountNumber);
                tx.setStartDate(plan.getStartDate());
                tx.setEndDate(plan.getEndDate());
                tx.setTransactionDate(LocalDate.now());
                tx.setTransactionStatus("Success");
                tx.setEdgTraceId(UUID.randomUUID().toString());
                tx.setTotalAmountReceived(newTotal); // cumulative for this plan

                transactionRepo.save(tx);

                System.out.printf("[SendMoney] Paid case %s, plan %s, amount %.2f, total now %.2f (Account: %s)%n",
                        caseNumber, plan.getPlanName(), planAmount, newTotal, accountNumber);
            }
        }
    }
}
