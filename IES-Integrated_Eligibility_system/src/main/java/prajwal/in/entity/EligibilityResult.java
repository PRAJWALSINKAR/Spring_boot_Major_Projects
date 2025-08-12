package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ELIGIBILITY_RESULT")
public class EligibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caseNumber;

    @Column(nullable = false)
    private String planName;

    // "Y" or "N" for quick boolean checks
    @Column(nullable = false, length = 1)
    private String eligible;

    // Explicit status for reports: "Approved", "Denied", "Not Applied"
    @Column(nullable = false)
    private String planStatus;

    // Reason for denial (including "Not Applied")
    private String denialReason;

    // Monetary benefit amount
    private Double benefitAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime determinedAt;
}
