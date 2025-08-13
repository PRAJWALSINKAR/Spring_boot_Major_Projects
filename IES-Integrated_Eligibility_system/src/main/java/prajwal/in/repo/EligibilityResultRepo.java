package prajwal.in.repo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import prajwal.in.entity.EligibilityResult;

@Repository
public interface EligibilityResultRepo extends JpaRepository<EligibilityResult, Long> {

    List<EligibilityResult> findByCaseNumber(String caseNumber);

    Optional<EligibilityResult> findByCaseNumberAndPlanName(String caseNumber, String planName);

    List<EligibilityResult> findByPlanStatus(String planStatus);
    
    long countByEligible(String eligible);

    long countByEligibleNot(String eligible);

    @Query("SELECT COALESCE(SUM(e.benefitAmount), 0) FROM EligibilityResult e WHERE e.eligible = 'Y'")
    Double sumTotalBenefitsGiven();
    }


