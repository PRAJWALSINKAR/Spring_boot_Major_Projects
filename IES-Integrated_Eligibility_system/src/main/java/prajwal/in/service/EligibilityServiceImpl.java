package prajwal.in.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prajwal.in.dto.EligibilityResultDTO;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.entity.CitizenInfo;
import prajwal.in.entity.EligibilityResult;
import prajwal.in.entity.Kid;
import prajwal.in.entity.Plan;
import prajwal.in.repo.CitizenInfoRepo;
import prajwal.in.repo.CitizenRepo;
import prajwal.in.repo.EligibilityResultRepo;
import prajwal.in.repo.KidRepo;
import prajwal.in.repo.PlanRepo;
import prajwal.in.util.EligibilityRulesEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {

    private final CitizenRepo citizenRepo;
    private final CitizenInfoRepo citizenInfoRepo;
    private final KidRepo kidRepo;
    private final PlanRepo planRepo;
    private final EligibilityResultRepo eligibilityResultRepo;
    private final EligibilityRulesEngine rulesEngine;

    @Override
    public List<EligibilityResultDTO> determineEligibility(String caseNumber) {
        List<EligibilityResultDTO> resultList = new ArrayList<>();

        // 1. Get citizen basic info
        CitizenEntity citizenEntity = citizenRepo.findByCaseNumber(caseNumber).orElse(null);
        if (citizenEntity == null)
            return resultList;

        // 2. Fetch all applied plan records for this case
        List<CitizenInfo> citizenInfoList = citizenInfoRepo.findAllByCaseNumber(caseNumber);

        // Build planName -> CitizenInfo map for quick lookup
        Map<String, CitizenInfo> appliedPlanInfoMap = new HashMap<>();
        for (CitizenInfo ci : citizenInfoList) {
            if (ci.getSelectedPlan() != null && !ci.getSelectedPlan().isBlank()) {
                appliedPlanInfoMap.put(ci.getSelectedPlan().trim(), ci);
            }
        }

        // 3. Kids data
        List<Kid> kids = kidRepo.findByCaseNumber(caseNumber);

        // 4. All active plans
        List<Plan> plans = planRepo.findByActiveSw("Y");

        for (Plan plan : plans) {

            EligibilityResultDTO dto = new EligibilityResultDTO();
            dto.setCaseNumber(caseNumber);
            dto.setPlanName(plan.getPlanName());

            // Get the CitizenInfo for this specific plan
            CitizenInfo thisPlanInfo = appliedPlanInfoMap.get(plan.getPlanName().trim());

            if (thisPlanInfo == null) {
                // Not applied for this plan — show in UI, skip DB save
                dto.setEligible("NA");
                dto.setPlanStatus("Not Applicable");
                dto.setDenialReason("Not Applied");
                dto.setBenefitAmount(0.0);
                dto.setStartDate(null);
                dto.setEndDate(null);
            } else {
                // Applied → evaluate using plan-specific info
                EligibilityResultDTO ruleResult =
                        rulesEngine.evaluatePlan(citizenEntity, thisPlanInfo, kids, plan);

                dto.setEligible(ruleResult.getEligible());
                dto.setDenialReason(ruleResult.getDenialReason());
                dto.setBenefitAmount(ruleResult.getBenefitAmount());
                dto.setStartDate(ruleResult.getStartDate());
                dto.setEndDate(ruleResult.getEndDate());
                dto.setPlanStatus("Y".equalsIgnoreCase(ruleResult.getEligible()) ? "Approved" : "Denied");

                // Save/update DB record (only Approved/Denied)
                Optional<EligibilityResult> existingOpt =
                        eligibilityResultRepo.findByCaseNumberAndPlanName(caseNumber, plan.getPlanName());

                EligibilityResult entity = existingOpt.orElse(new EligibilityResult());
                entity.setCaseNumber(dto.getCaseNumber());
                entity.setPlanName(dto.getPlanName());
                entity.setEligible(dto.getEligible());
                entity.setPlanStatus(dto.getPlanStatus());
                entity.setDenialReason(dto.getDenialReason());
                entity.setBenefitAmount(dto.getBenefitAmount());
                entity.setStartDate(dto.getStartDate());
                entity.setEndDate(dto.getEndDate());
                entity.setDeterminedAt(LocalDateTime.now());

                eligibilityResultRepo.save(entity);
            }

            resultList.add(dto);
        }

        return resultList;
    }

    @Override
    public List<EligibilityResultDTO> getEligibilityResults(String caseNumber) {
        List<EligibilityResult> records = eligibilityResultRepo.findByCaseNumber(caseNumber);
        List<EligibilityResultDTO> dtos = new ArrayList<>();
        for (EligibilityResult rec : records) {
            EligibilityResultDTO dto = new EligibilityResultDTO();
            dto.setCaseNumber(rec.getCaseNumber());
            dto.setPlanName(rec.getPlanName());
            dto.setEligible(rec.getEligible());
            dto.setPlanStatus(rec.getPlanStatus());
            dto.setDenialReason(rec.getDenialReason());
            dto.setBenefitAmount(rec.getBenefitAmount());
            dto.setStartDate(rec.getStartDate());
            dto.setEndDate(rec.getEndDate());
            dtos.add(dto);
        }
        return dtos;
    }
}
