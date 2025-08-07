package prajwal.in.service;

import java.util.List;

import prajwal.in.dto.CitizenDTO;

public interface CitizenService {

    String createCitizen(CitizenDTO dto, String caseWorkerEmail);  // returns case number or error

    List<CitizenDTO> getCitizensByCaseWorker(String email);

    CitizenDTO getCitizenByCaseNumber(String caseNumber);
}
