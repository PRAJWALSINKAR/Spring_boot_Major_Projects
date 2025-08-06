package prajwal.in.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.CaseWorker;

public interface CaseWorkerService {
    CaseWorker createCaseWorker(CaseWorker worker);
    ResponseEntity<Map<String, String>> login(LoginRequest request);

    List<CaseWorker> findAll();
    Optional<CaseWorker> findByEmail(String email);
    CaseWorker findByResetToken(String token);
    ResponseEntity<String> resetPassword(String token, String newPassword);
    void updateCaseWorker(CaseWorker worker);
    boolean registerCaseWorker(CaseWorkerRequest form);
    
    
    void deleteCaseWorker(Long id); // permanent delete
    void toggleCaseWorkerStatus(Long id); // activate/deactivate
    List<CaseWorker> searchByEmail(String email); // for search bar
    Optional<CaseWorker> findById(Long id); // for edit form



}
