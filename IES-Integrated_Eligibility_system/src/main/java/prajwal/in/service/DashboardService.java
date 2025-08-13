// DashboardService.java
package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import prajwal.in.repo.PlanRepo;
import prajwal.in.repo.EligibilityResultRepo;

@Service
public class DashboardService {

    @Autowired
    private PlanRepo planRepo;

    @Autowired
    private EligibilityResultRepo eligibilityRepo;

    public long getPlanCount() {
        return planRepo.count();
    }

    public long getCitizensApproved() {
        return eligibilityRepo.countByEligible("Y");
    }

    public long getCitizensDenied() {
        return eligibilityRepo.countByEligibleNot("Y");
    }

    public Double getBenefitsGiven() {
        return eligibilityRepo.sumTotalBenefitsGiven();
    }
}
