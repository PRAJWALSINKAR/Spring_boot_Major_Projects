package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_workers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String phoneNumber;

    private String gender;

    private LocalDate dob;

    private String ssn;

    // Password reset support
    private String tempPassword;
    private Boolean passwordResetRequired;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
}
