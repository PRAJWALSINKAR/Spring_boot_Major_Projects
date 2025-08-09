package prajwal.in.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import prajwal.in.dto.CitizenDTO;
import prajwal.in.entity.CaseWorker;
import prajwal.in.entity.CitizenEntity;
import prajwal.in.repo.CaseWorkerRepo;
import prajwal.in.repo.CitizenRepo;

@Service
public class CitizenServiceImpl implements CitizenService {

    @Autowired
    private CitizenRepo citizenRepo;

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    private String generateCaseNumber() {
        Random random = new Random();
        int num = 100 + random.nextInt(900); // 3-digit number between 100–999
        return String.valueOf(num);
    }

    @Override
    public String createCitizen(CitizenDTO dto, String caseWorkerEmail) {

        // ❌ Reject if SSN does not start with 123
        if (!dto.getSsn().startsWith("123")) {
            return "Not a citizen of Rhode Island. Application not applicable.";
        }

        // ❌ Reject if SSN already exists
        if (citizenRepo.findBySsn(dto.getSsn()).isPresent()) {
            return "Application rejected: SSN already exists. Citizen already registered.";
        }

        // ✅ Fetch CaseWorker
        CaseWorker caseWorker = caseWorkerRepo.findByEmail(caseWorkerEmail).orElse(null);
        if (caseWorker == null) {
            return "CaseWorker not found.";
        }

        // ✅ Create Entity and Save
        CitizenEntity entity = new CitizenEntity();
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setMobileNumber(dto.getMobileNumber());
        entity.setGender(dto.getGender());
        entity.setDob(dto.getDob());
        entity.setSsn(dto.getSsn());
        entity.setCreatedBy(caseWorker);
        entity.setCaseNumber(generateCaseNumber());

        citizenRepo.save(entity);

        return "Application submitted successfully. Case Number: " + entity.getCaseNumber();
    }


    @Override
    public List<CitizenDTO> getCitizensByCaseWorker(String email) {
    	 Optional<CaseWorker> option = caseWorkerRepo.findByEmail(email);
    	    if (option.isEmpty()) return List.of();

    	    CaseWorker caseWorker = option.get();
    	    List<CitizenEntity> entities = citizenRepo.findByCreatedBy(caseWorker);

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
