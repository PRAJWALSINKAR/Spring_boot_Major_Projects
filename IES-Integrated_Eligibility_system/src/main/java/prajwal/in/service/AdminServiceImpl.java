package prajwal.in.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.Admin;
import prajwal.in.entity.CaseWorker;
import prajwal.in.repo.AdminRepo;
import prajwal.in.repo.CaseWorkerRepo;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    @Autowired
    private JavaMailSender mailSender;

    private final AdminRepo adminRepo;

    public AdminServiceImpl(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public Admin createAdmin(Admin admin) {
        return adminRepo.save(admin); // Save plain for now
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepo.findByEmail(email);
    }

    @Override
    public ResponseEntity<Map<String, String>> login(LoginRequest request) {
        Optional<Admin> optional = adminRepo.findByEmail(request.getEmail());
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials: email not found"));
        }

        Admin admin = optional.get();

        if (!request.getPassword().equals(admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials: wrong password"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role", "ADMIN"
        ));
    }

    @Override
    public List<Admin> findAll() {
        return adminRepo.findAll();
    }

    @Override
    public ResponseEntity<String> createCaseWorker(CaseWorkerRequest request) {
        if (caseWorkerRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Case Worker already exists with this email");
        }

        // Generate temp password and token (just to embed in email)
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        String token = UUID.randomUUID().toString(); // Reset token to map in DB

        // Create and save worker
        CaseWorker worker = new CaseWorker();
        worker.setName(request.getName());
        worker.setEmail(request.getEmail());
        worker.setPhoneNumber(request.getPhoneNumber());
        worker.setGender(request.getGender());
        worker.setDob(request.getDob());
        worker.setSsn(request.getSsn());
        worker.setPassword(tempPassword);           // Store as plain
        worker.setTempPassword(tempPassword);       // For checking during reset
        worker.setResetToken(token);                // Used in URL
        worker.setAccStatus("LOCKED");              // For future logic if needed

        caseWorkerRepo.save(worker);

        // Send email with reset link
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        String body = String.format(
                "Hi %s,\n\nYour case worker account has been created.\n" +
                        "Temporary Password: %s\n" +
                        "Reset your password here: %s\n\nThanks!",
                request.getName(), tempPassword, resetLink
        );

        sendEmail(request.getEmail(), "Case Worker Account Created", body);
        return ResponseEntity.ok("Case Worker created and email sent.");
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
