package prajwal.in.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import prajwal.in.entity.CitizenEntity;
import prajwal.in.entity.CaseWorker;


public interface CitizenRepo extends JpaRepository<CitizenEntity, Long> {

    List<CitizenEntity> findByCreatedBy(CaseWorker caseWorker);

    Optional<CitizenEntity> findByCaseNumber(String caseNumber);
    
    Optional<CitizenEntity> findBySsn(String ssn);

   
}

