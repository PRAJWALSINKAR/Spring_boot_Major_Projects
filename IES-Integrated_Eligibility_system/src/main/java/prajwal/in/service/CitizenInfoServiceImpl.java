package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import prajwal.in.entity.CitizenInfo;
import prajwal.in.repo.CitizenInfoRepo;

@Service
public class CitizenInfoServiceImpl implements CitizenInfoService {

    @Autowired
    private CitizenInfoRepo infoRepo;

    @Override
    public CitizenInfo getByCaseNumber(String caseNumber) {
        return infoRepo.findByCaseNumber(caseNumber).orElse(null);
    }

    @Override
    public CitizenInfo saveOrUpdate(CitizenInfo info) {
        Optional<CitizenInfo> existingOpt = infoRepo.findByCaseNumber(info.getCaseNumber());

        if (existingOpt.isPresent()) {
            CitizenInfo existing = existingOpt.get();
            info.setId(existing.getId());

            // Merge fields so we don't overwrite existing data with nulls
            if (info.getSelectedPlan() == null) info.setSelectedPlan(existing.getSelectedPlan());
            if (info.getMonthlySalary() == null) info.setMonthlySalary(existing.getMonthlySalary());
            if (info.getRentIncome() == null) info.setRentIncome(existing.getRentIncome());
            if (info.getPropertyIncome() == null) info.setPropertyIncome(existing.getPropertyIncome());
            if (info.getHighestEducation() == null) info.setHighestEducation(existing.getHighestEducation());
            if (info.getGraduationYear() == null) info.setGraduationYear(existing.getGraduationYear());
            if (info.getUniversityOrSchool() == null) info.setUniversityOrSchool(existing.getUniversityOrSchool());
        }

        return infoRepo.save(info);
    }
}

