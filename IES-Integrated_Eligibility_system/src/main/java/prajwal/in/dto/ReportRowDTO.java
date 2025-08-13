package prajwal.in.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportRowDTO {
    private Long serialNo;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String gender;
    private String ssn;
    private Double totalBenefitAmount;
}
