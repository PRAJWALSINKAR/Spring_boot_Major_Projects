package prajwal.in.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import prajwal.in.entity.CitizenInfo;

public interface CitizenInfoRepo extends JpaRepository<CitizenInfo, Long> {
    Optional<CitizenInfo> findByCaseNumber(String caseNumber);
}

