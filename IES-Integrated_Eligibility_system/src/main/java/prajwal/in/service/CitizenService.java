package prajwal.in.service;

import prajwal.in.dto.CitizenDTO;

import java.util.List;

public interface CitizenService {
    // For caseworker or admin, pass email and role
    String createCitizen(CitizenDTO dto, String email, String role);

    // Fetch apps for user by their email and role
    List<CitizenDTO> getCitizensByUser(String email, String role);

    // Fetch by case number (for details etc.)
    CitizenDTO getCitizenByCaseNumber(String caseNumber);
}
