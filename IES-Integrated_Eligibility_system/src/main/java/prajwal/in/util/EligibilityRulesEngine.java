package prajwal.in.util;

import org.springframework.stereotype.Component;
import prajwal.in.dto.EligibilityResultDTO;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.entity.CitizenInfo;
import prajwal.in.entity.Kid;
import prajwal.in.entity.Plan;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class EligibilityRulesEngine {

    public EligibilityResultDTO evaluatePlan(CitizenEntity citizenEntity,
                                             CitizenInfo citizenInfo,
                                             List<Kid> kids,
                                             Plan plan) {

        double totalIncome =
                (citizenInfo.getMonthlySalary() != null ? citizenInfo.getMonthlySalary() : 0) +
                (citizenInfo.getRentIncome() != null ? citizenInfo.getRentIncome() : 0) +
                (citizenInfo.getPropertyIncome() != null ? citizenInfo.getPropertyIncome() : 0);

        EligibilityResultDTO dto = new EligibilityResultDTO();
        dto.setCaseNumber(citizenInfo.getCaseNumber());
        dto.setPlanName(plan.getPlanName());

        LocalDate startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        dto.setStartDate(startDate);
        dto.setEndDate(startDate.plusMonths(12));

        String planName = plan.getPlanName().trim().toLowerCase();

        switch (planName) {
            case "snap":
                if (totalIncome > 1500)
                    deny(dto, "Income exceeds SNAP threshold");
                else
                    approve(dto, 250.0 * (kids.size() + 1)); // applicant + kids
                break;

            case "ccap":
                long eligibleKids = kids.stream()
                        .filter(k -> k.getKidAge() != null && k.getKidAge() <= 14)
                        .count();
                if (eligibleKids == 0)
                    deny(dto, "No child under 14");
                else if (totalIncome > 2500)
                    deny(dto, "Income exceeds CCAP threshold");
                else
                    approve(dto, 300.0 * eligibleKids);
                break;

            case "medicaid":
                if (totalIncome > 1800)
                    deny(dto, "Income exceeds Medicaid limit");
                else
                    approve(dto, 0.0); // Coverage only
                break;

            case "medicare":
                if (citizenEntity == null || citizenEntity.getDob() == null) {
                    deny(dto, "DOB not available for Medicare check");
                } else {
                    int age = Period.between(citizenEntity.getDob(), LocalDate.now()).getYears();
                    if (age < 60) {
                        deny(dto, "Applicant under age 60");
                    } else {
                        approve(dto, 0.0); // Coverage only
                    }
                }
                break;

            case "qhp":
                if (totalIncome <= 1800)
                    deny(dto, "Income too low - qualifies for Medicaid");
                else if (totalIncome > 4000)
                    deny(dto, "Income exceeds QHP limit");
                else {
                    double subsidy = 400.0 - ((totalIncome - 1800) / 100);
                    if (subsidy < 0) subsidy = 0;
                    approve(dto, subsidy);
                }
                break;

            case "riw":
                boolean hasMinor = kids.stream()
                        .anyMatch(k -> k.getKidAge() != null && k.getKidAge() < 18);
                if (!hasMinor)
                    deny(dto, "No dependent child under 18");
                else if (totalIncome > 1800)
                    deny(dto, "Income exceeds RIW threshold");
                else
                    approve(dto, 350.0);
                break;

            default:
                deny(dto, "Unknown plan");
        }

        return dto;
    }

    private void approve(EligibilityResultDTO dto, double amount) {
        dto.setEligible("Y");
        dto.setBenefitAmount(amount);
        dto.setDenialReason(null);
    }

    private void deny(EligibilityResultDTO dto, String reason) {
        dto.setEligible("N");
        dto.setBenefitAmount(0.0);
        dto.setDenialReason(reason);
    }
}
