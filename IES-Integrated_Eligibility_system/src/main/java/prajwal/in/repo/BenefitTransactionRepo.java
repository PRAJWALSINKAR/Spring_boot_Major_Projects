package prajwal.in.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import prajwal.in.entity.BenefitTransaction;
import java.util.List;

public interface BenefitTransactionRepo extends JpaRepository<BenefitTransaction, Long> {
    List<BenefitTransaction> findByCaseNumber(String caseNumber);
}
