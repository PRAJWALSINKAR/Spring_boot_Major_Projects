package prajwal.in.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.Admin;

public interface AdminService {
    Admin createAdmin(Admin admin);
    ResponseEntity<Map<String, String>> login(LoginRequest request);
    List<Admin> findAll();
    Optional<Admin> findByEmail(String email);
    ResponseEntity<String> createCaseWorker(CaseWorkerRequest request);

   

}
