package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "benefit_transaction")
public class BenefitTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Serial Number in table

    @Column(nullable = false)
    private String caseNumber;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String planStatus; // Approved / Denied

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private Double benefitAmount; // Amount for this payment cycle

    @Column(nullable = false)
    private String accountNumber; // Beneficiary Account Number

    private LocalDate transactionDate; // Payment date

    @Column(nullable = false)
    private String transactionStatus; // Success / Waiting

    private String edgTraceId; // Unique reference ID

    private Double totalAmountReceived; // Cumulative
}
