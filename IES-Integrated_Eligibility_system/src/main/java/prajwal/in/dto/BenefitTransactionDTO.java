package prajwal.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenefitTransactionDTO {

    private Long id; // S.NO in table
    private String edgTraceId;
    private String caseNumber;
    private String planName;
    private String planStatus;
    private Double benefitAmount;
    private String accountNumber; // From CitizenInfo
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate transactionDate;
    private String transactionStatus; // Success / Waiting
    private Double totalAmountReceived; // Cumulative
}
