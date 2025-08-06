package prajwal.in.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.CaseWorker;
import java.util.Optional;

public interface CaseWorkerRepo extends JpaRepository<CaseWorker, Long> {
    Optional<CaseWorker> findByEmail(String email);

	Optional<CaseWorker> findByResetToken(String token);
}
