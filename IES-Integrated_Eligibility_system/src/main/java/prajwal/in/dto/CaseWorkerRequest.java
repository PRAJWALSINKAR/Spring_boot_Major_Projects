package prajwal.in.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CaseWorkerRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate dob;
    private String ssn;
}
