package prajwal.in.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import prajwal.in.dto.LoginRequest;
import prajwal.in.entity.CaseWorker;
import prajwal.in.repo.CaseWorkerRepo;
import org.apache.commons.lang3.RandomStringUtils;

@Service
public class CaseWorkerServiceImpl implements CaseWorkerService {

    @Autowired
    private CaseWorkerRepo caseWorkerRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public CaseWorker createCaseWorker(CaseWorker worker) {
        String tempPassword = RandomStringUtils.randomAlphanumeric(12);
        String resetToken = UUID.randomUUID().toString();

        worker.setTempPassword(tempPassword);
        worker.setResetToken(resetToken);
        worker.setPasswordResetRequired(true);
        worker.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        worker.setPassword(tempPassword);  // For now store as plain text

        // Save worker
        CaseWorker saved = caseWorkerRepo.save(worker);

        // Send email with temp password and reset link
        sendResetEmail(saved.getEmail(), tempPassword, resetToken);

        return saved;
    }

    private void sendResetEmail(String email, String tempPassword, String resetToken) {
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        String body = "Welcome! Your temporary password is: " + tempPassword + "\n" +
                      "Please reset your password using the link below within 24 hours:\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset - IES Portal");
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public ResponseEntity<Map<String, String>> login(LoginRequest request) {
        Optional<CaseWorker> optional = caseWorkerRepo.findByEmail(request.getEmail());
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials: email not found"));
        }

        CaseWorker worker = optional.get();

        if (!request.getPassword().equals(worker.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials: wrong password"));
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
        if (worker == null || worker.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired reset token");
        }

        worker.setPassword(newPassword);
        worker.setTempPassword(null);
        worker.setPasswordResetRequired(false);
        worker.setResetToken(null);
        worker.setResetTokenExpiry(null);

        caseWorkerRepo.save(worker);

        return ResponseEntity.ok("Password reset successful");
    }

	public Optional<CaseWorker> findByEmail(String email) {
		// TODO Auto-generated method stub
	    return caseWorkerRepo.findByEmail(email);
	}
}
