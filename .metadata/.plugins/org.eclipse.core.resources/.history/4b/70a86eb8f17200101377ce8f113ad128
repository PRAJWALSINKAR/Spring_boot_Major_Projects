package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prajwal.in.entity.Plan;
import prajwal.in.repo.PlanRepo;

import java.util.List;
import java.util.Optional;

@Service
public class PlanServiceImpl implements PlanService {

    @Autowired
    private PlanRepo planRepo;

    @Override
    public boolean savePlan(Plan plan) {
        plan.setActiveSw("Y");
        return planRepo.save(plan) != null;
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepo.findAll();
    }

    @Override
    public Optional<Plan> getPlanById(Long id) {
        return planRepo.findById(id);
    }

    @Override
    public void deletePlan(Long id) {
        planRepo.deleteById(id);
    }

    @Override
    public void togglePlanStatus(Long id) {
        Optional<Plan> opt = planRepo.findById(id);
        if (opt.isPresent()) {
            Plan plan = opt.get();
            plan.setActiveSw(plan.getActiveSw().equalsIgnoreCase("Y") ? "N" : "Y");
            planRepo.save(plan);
        }
    }
}
