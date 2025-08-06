package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
public class CaseWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String tempPassword;
    private String phoneNumber;
    private String gender;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String ssn;

    private String accStatus; // e.g., LOCKED or ACTIVE
    private String resetToken; // Simple token (no expiry tracking)
}
