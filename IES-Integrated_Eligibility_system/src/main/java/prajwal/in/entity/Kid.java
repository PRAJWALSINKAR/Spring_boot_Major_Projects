package prajwal.in.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "KID")
public class Kid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kidName;
    private Integer kidAge;
    private String kidSsn;

    // Foreign key to CitizenInfo
    private String caseNumber;
}
