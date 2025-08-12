package prajwal.in.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.CitizenInfo;

import java.util.List;
import java.util.Optional;

public interface CitizenInfoRepo extends JpaRepository<CitizenInfo, Long> {

    List<CitizenInfo> findAllByCaseNumber(String caseNumber);

    Optional<CitizenInfo> findByCaseNumberAndSelectedPlan(String caseNumber, String selectedPlan);
}
