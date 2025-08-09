package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "CITIZEN_INFO")
public class CitizenInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String caseNumber;

    // Income details
    private Double monthlySalary;
    private Double rentIncome;
    private Double propertyIncome;

    // Education details
    private String highestEducation;
    private Integer graduationYear;
    private String universityOrSchool;

    // ✅ NEW: Plan name selected by user
    @Column(name = "selected_plan")
    private String selectedPlan;
}
