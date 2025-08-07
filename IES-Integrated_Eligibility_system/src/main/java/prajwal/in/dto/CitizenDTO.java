package prajwal.in.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CitizenDTO {

    private String fullName;
    private String email;
    private String mobileNumber;
    private String gender;
    private LocalDate dob;
    private String ssn;

    // Getters and Setters
}