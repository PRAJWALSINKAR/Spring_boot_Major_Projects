package prajwal.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityResultDTO {

    private String caseNumber;
    private String planName;

    // "Y" or "N"
    private String eligible;

    // Explicit plan status for reports: "Approved", "Denied", "Not Applied"
    private String planStatus;

    // Reason for denial (including "Not Applied")
    private String denialReason;

    // Benefit info
    private Double benefitAmount;

    private LocalDate startDate;
    private LocalDate endDate;

}
