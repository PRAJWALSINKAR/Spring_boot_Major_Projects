package prajwal.in.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.Kid;

public interface KidRepo extends JpaRepository<Kid, Long> {
    List<Kid> findByCaseNumber(String caseNumber);
}
