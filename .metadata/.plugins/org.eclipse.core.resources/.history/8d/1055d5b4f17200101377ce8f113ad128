package prajwal.in.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import prajwal.in.entity.Plan;

public interface PlanRepo extends JpaRepository<Plan, Long> {
    List<Plan> findByActiveSw(String activeSw);
}
