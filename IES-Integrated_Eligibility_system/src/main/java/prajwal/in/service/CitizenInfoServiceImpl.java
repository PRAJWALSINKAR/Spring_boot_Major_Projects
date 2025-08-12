package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prajwal.in.entity.CitizenInfo;
import prajwal.in.repo.CitizenInfoRepo;

import java.util.List;
import java.util.Optional;

@Service
public class CitizenInfoServiceImpl implements CitizenInfoService {

    @Autowired
    private CitizenInfoRepo infoRepo;

    @Override
    public CitizenInfo getByCaseNumber(String caseNumber) {
        List<CitizenInfo> list = infoRepo.findAllByCaseNumber(caseNumber);
        if (list.isEmpty()) return null;

        // ✅ Prefer the record which has accountNumber
        for (CitizenInfo ci : list) {
            if (ci.getAccountNumber() != null && !ci.getAccountNumber().isBlank()) {
                return ci;
            }
        }
        return list.get(0);
    }

    @Override
    public CitizenInfo saveOrUpdate(CitizenInfo info) {

        if (info.getSelectedPlan() == null || info.getSelectedPlan().trim().isEmpty()) {
            throw new IllegalArgumentException("Selected plan cannot be null or empty.");
        }

        Optional<CitizenInfo> samePlan =
                infoRepo.findByCaseNumberAndSelectedPlan(info.getCaseNumber(), info.getSelectedPlan());

        if (samePlan.isPresent()) {
            CitizenInfo existing = samePlan.get();

            if (info.getMonthlySalary() != null) existing.setMonthlySalary(info.getMonthlySalary());
            if (info.getRentIncome() != null) existing.setRentIncome(info.getRentIncome());
            if (info.getPropertyIncome() != null) existing.setPropertyIncome(info.getPropertyIncome());

            if (info.getAccountNumber() != null && !info.getAccountNumber().isBlank()) {
                existing.setAccountNumber(info.getAccountNumber());
            } else {
                infoRepo.findAllByCaseNumber(info.getCaseNumber()).stream()
                    .filter(ci -> ci.getAccountNumber() != null && !ci.getAccountNumber().isBlank())
                    .findFirst()
                    .ifPresent(ci -> existing.setAccountNumber(ci.getAccountNumber()));
            }

            if (info.getHighestEducation() != null) existing.setHighestEducation(info.getHighestEducation());
            if (info.getGraduationYear() != null) existing.setGraduationYear(info.getGraduationYear());
            if (info.getUniversityOrSchool() != null && !info.getUniversityOrSchool().isBlank()) {
                existing.setUniversityOrSchool(info.getUniversityOrSchool());
            }
            return infoRepo.save(existing);
        }

        List<CitizenInfo> existingList = infoRepo.findAllByCaseNumber(info.getCaseNumber());
        if (!existingList.isEmpty()) {
            CitizenInfo source = existingList.stream()
                .filter(ci -> ci.getAccountNumber() != null && !ci.getAccountNumber().isBlank())
                .findFirst()
                .orElse(existingList.get(0));

            if (info.getMonthlySalary() == null) info.setMonthlySalary(source.getMonthlySalary());
            if (info.getRentIncome() == null) info.setRentIncome(source.getRentIncome());
            if (info.getPropertyIncome() == null) info.setPropertyIncome(source.getPropertyIncome());
            if (info.getAccountNumber() == null || info.getAccountNumber().isBlank()) {
                info.setAccountNumber(source.getAccountNumber());
            }
            if (info.getHighestEducation() == null) info.setHighestEducation(source.getHighestEducation());
            if (info.getGraduationYear() == null) info.setGraduationYear(source.getGraduationYear());
            if (info.getUniversityOrSchool() == null || info.getUniversityOrSchool().isBlank()) {
                info.setUniversityOrSchool(source.getUniversityOrSchool());
            }
        }

        return infoRepo.save(info);
    }

}
