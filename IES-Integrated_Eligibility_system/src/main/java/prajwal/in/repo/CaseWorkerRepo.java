package prajwal.in.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.CaseWorker;

import java.util.List;
import java.util.Optional;

public interface CaseWorkerRepo extends JpaRepository<CaseWorker, Long> {

	Optional<CaseWorker> findByResetToken(String token);
	 Optional<CaseWorker> findByEmail(String email);
	List<CaseWorker> findByEmailContainingIgnoreCase(String email);// for search functionalit
}
