package prajwal.in.service;

import java.util.List;
import java.util.Optional;

import prajwal.in.entity.Plan;

public interface PlanService {
	 boolean savePlan(Plan plan);
	    List<Plan> getAllPlans();
	    Optional<Plan> getPlanById(Long id);
	    void deletePlan(Long id);
	    void togglePlanStatus(Long id);
	    List<Plan> searchByName(String name);

	}
