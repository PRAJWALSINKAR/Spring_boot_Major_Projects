package prajwal.in.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import prajwal.in.dto.CaseWorkerRequest;
import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.CaseWorker;
import prajwal.in.repo.CaseWorkerRepo;
import prajwal.in.util.EmailUtils;
import prajwal.in.util.PwdUtils;

@Service
public class CaseWorkerServiceImpl implements CaseWorkerService {

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    @Autowired
    private EmailUtils emailUtils;

    @Override
    public CaseWorker createCaseWorker(CaseWorker worker) {
        String tempPassword = PwdUtils.generateRandomPwd();
        String token = UUID.randomUUID().toString();

        worker.setPassword(tempPassword);
        worker.setTempPassword(tempPassword);
        worker.setResetToken(token);
        worker.setAccStatus("LOCKED");

        caseWorkerRepo.save(worker);

        sendResetEmail(worker.getEmail(), tempPassword, token);
        return worker;
    }

    private void sendResetEmail(String email, String tempPassword, String resetToken) {
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;

        String body = "<h2>Welcome to IES Portal</h2>"
                    + "<p><strong>Temporary Password:</strong> " + tempPassword + "</p>"
                    + "<p>Click below to reset your password:</p>"
                    + "<a href='" + resetLink + "'>Reset Password</a>";

        emailUtils.sendEmail(email, "Reset Your IES Password", body);
    }

    @Override
    public ResponseEntity<Map<String, String>> login(LoginRequest request) {
        Optional<CaseWorker> optional = caseWorkerRepo.findByEmail(request.getEmail());

        if (optional.isEmpty() || !request.getPassword().equals(optional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "role", "CASEWORKER"
        ));
    }

    @Override
    public List<CaseWorker> findAll() {
        return caseWorkerRepo.findAll();
    }

    @Override
    public CaseWorker findByResetToken(String token) {
        return caseWorkerRepo.findByResetToken(token).orElse(null);
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        CaseWorker worker = findByResetToken(token);
        if (worker == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
        }

        worker.setPassword(newPassword);
        worker.setTempPassword(null);
        worker.setResetToken(null);
        worker.setAccStatus("ACTIVE");

        caseWorkerRepo.save(worker);
        return ResponseEntity.ok("Password reset successful");
    }

    @Override
    public Optional<CaseWorker> findByEmail(String email) {
        return caseWorkerRepo.findByEmail(email);
    }

    @Override
    public void updateCaseWorker(CaseWorker worker) {
        caseWorkerRepo.save(worker);
    }

    @Override
    public boolean registerCaseWorker(CaseWorkerRequest form) {
        if (caseWorkerRepo.findByEmail(form.getEmail()).isPresent()) {
            return false;
        }

        CaseWorker worker = new CaseWorker();
        worker.setName(form.getName());
        worker.setEmail(form.getEmail());
        worker.setPhoneNumber(form.getPhoneNumber());
        worker.setGender(form.getGender());
        worker.setDob(form.getDob());
        worker.setSsn(form.getSsn());

        String tempPwd = PwdUtils.generateRandomPwd();
        worker.setPassword(tempPwd);
        worker.setTempPassword(tempPwd);
        worker.setAccStatus("LOCKED");

        String token = UUID.randomUUID().toString();
        worker.setResetToken(token);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        String subject = "Reset Your IES Password";
        String body = "<h2>Welcome to IES Portal</h2>"
                    + "<p><strong>Temporary Password:</strong> " + tempPwd + "</p>"
                    + "<p>Click below to reset your password:</p>"
                    + "<a href='" + resetLink + "'>Reset Password</a>";

        boolean emailSent = emailUtils.sendEmail(form.getEmail(), subject, body);

        if (emailSent) {
            caseWorkerRepo.save(worker);
            return true;
        }

        return false;
    }
    @Override
    public void deleteCaseWorker(Long id) {
        caseWorkerRepo.deleteById(id);
    }

    @Override
    public void toggleCaseWorkerStatus(Long id) {
        Optional<CaseWorker> optional = caseWorkerRepo.findById(id);
        if (optional.isPresent()) {
            CaseWorker worker = optional.get();

            // Normalize to uppercase before comparing
            String status = worker.getAccStatus();
            if (status != null && status.equalsIgnoreCase("ACTIVE")) {
                worker.setAccStatus("LOCKED");
            } else {
                worker.setAccStatus("ACTIVE");
            }

            caseWorkerRepo.save(worker); // save updated status
        }
    }

    @Override
    public List<CaseWorker> searchByEmail(String email) {
        return caseWorkerRepo.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public Optional<CaseWorker> findById(Long id) {
        return caseWorkerRepo.findById(id);
    }

    
    
}
