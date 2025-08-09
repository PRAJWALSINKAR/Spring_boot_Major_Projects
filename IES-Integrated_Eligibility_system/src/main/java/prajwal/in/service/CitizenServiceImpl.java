package prajwal.in.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prajwal.in.dto.CitizenDTO;
import prajwal.in.entity.Admin;
import prajwal.in.entity.CaseWorker;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.repo.AdminRepo;
import prajwal.in.repo.CaseWorkerRepo;
import prajwal.in.repo.CitizenRepo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CitizenServiceImpl implements CitizenService {

    @Autowired
    private CitizenRepo citizenRepo;

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    @Autowired
    private AdminRepo adminRepo;

    private String generateCaseNumber() {
        Random random = new Random();
        int num = 100 + random.nextInt(900); // 100–999
        return String.valueOf(num);
    }

    @Override
    public String createCitizen(CitizenDTO dto, String email, String role) {
        // Validate SSN logic
        if (dto.getSsn() == null || !dto.getSsn().startsWith("123") || dto.getSsn().length() != 8) {
            return "Not a citizen of Rhode Island. Application not applicable.";
        }

        // Check SSN presence
        if (citizenRepo.findBySsn(dto.getSsn()).isPresent()) {
            return "Application rejected: SSN already exists. Citizen already registered.";
        }

        CitizenEntity entity = new CitizenEntity();
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setMobileNumber(dto.getMobileNumber());
        entity.setGender(dto.getGender());
        entity.setDob(dto.getDob());
        entity.setSsn(dto.getSsn());
        entity.setCaseNumber(generateCaseNumber());

        // Set owner: ADMIN or CASEWORKER
        if ("ADMIN".equals(role)) {
            Optional<Admin> adminOpt = adminRepo.findByEmail(email);
            if (adminOpt.isPresent()) {
                entity.setCreatedByAdmin(adminOpt.get());
                entity.setCreatedBy(null);
            } else {
                return "Admin not found.";
            }
        } else if ("CASEWORKER".equals(role)) {
            Optional<CaseWorker> workerOpt = caseWorkerRepo.findByEmail(email);
            if (workerOpt.isPresent()) {
                entity.setCreatedBy(workerOpt.get());
                entity.setCreatedByAdmin(null);
            } else {
                return "CaseWorker not found.";
            }
        } else {
            return "Invalid user role.";
        }

        citizenRepo.save(entity);
        return "Application submitted successfully. Case Number: " + entity.getCaseNumber();
    }

    @Override
    public List<CitizenDTO> getCitizensByUser(String email, String role) {
        List<CitizenEntity> entities = List.of();

        if ("ADMIN".equals(role)) {
            Optional<Admin> aOpt = adminRepo.findByEmail(email);
            if (aOpt.isPresent()) {
                entities = citizenRepo.findByCreatedByAdmin(aOpt.get());
            }
        } else if ("CASEWORKER".equals(role)) {
            Optional<CaseWorker> cwOpt = caseWorkerRepo.findByEmail(email);
            if (cwOpt.isPresent()) {
                entities = citizenRepo.findByCreatedBy(cwOpt.get());
            }
        }
        return entities.stream().map(entity -> {
            CitizenDTO dto = new CitizenDTO();
            dto.setFullName(entity.getFullName());
            dto.setEmail(entity.getEmail());
            dto.setMobileNumber(entity.getMobileNumber());
            dto.setGender(entity.getGender());
            dto.setDob(entity.getDob());
            dto.setSsn(entity.getSsn());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public CitizenDTO getCitizenByCaseNumber(String caseNumber) {
        return citizenRepo.findByCaseNumber(caseNumber)
                .map(entity -> {
                    CitizenDTO dto = new CitizenDTO();
                    dto.setFullName(entity.getFullName());
                    dto.setEmail(entity.getEmail());
                    dto.setMobileNumber(entity.getMobileNumber());
                    dto.setGender(entity.getGender());
                    dto.setDob(entity.getDob());
                    dto.setSsn(entity.getSsn());
                    return dto;
                })
                .orElse(null);
    }
}
