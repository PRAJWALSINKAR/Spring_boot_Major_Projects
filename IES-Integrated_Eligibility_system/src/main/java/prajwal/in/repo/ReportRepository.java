package prajwal.in.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import prajwal.in.entity.CitizenEntity;

public interface ReportRepository extends JpaRepository<CitizenEntity, Long> {

    /**
     * Search query that:
     * - Joins citizen with eligibility_result (covers all statuses & plan names)
     * - Ignores filter if dropdown/date is empty or null
     * - Derives plan status from eligible flag
     */
    @Query(value = """
        SELECT ROW_NUMBER() OVER (ORDER BY c.full_name) AS serialNo,
               c.full_name AS fullName,
               c.email AS email,
               c.mobile_number AS mobileNumber,
               c.gender AS gender,
               c.ssn AS ssn,
               COALESCE(SUM(er.benefit_amount),0) AS totalBenefitAmount
        FROM citizen c
        JOIN eligibility_result er ON er.case_number = c.case_number
        WHERE (:gender IS NULL OR :gender = '' OR c.gender = :gender)
          AND (:planName IS NULL OR :planName = '' OR er.plan_name = :planName)
          AND (:planStatus IS NULL OR :planStatus = '' OR 
               (CASE WHEN er.eligible = 'Y' THEN 'Approved' ELSE 'Denied' END) = :planStatus)
          AND (:startDate IS NULL OR er.start_date >= :startDate)
          AND (:endDate IS NULL OR er.end_date <= :endDate)
        GROUP BY c.full_name, c.email, c.mobile_number, c.gender, c.ssn
        ORDER BY c.full_name
        """, nativeQuery = true)
    List<Object[]> searchReportRaw(
        @Param("gender") String gender,
        @Param("planName") String planName,
        @Param("planStatus") String planStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get distinct plan names from eligibility_result  
     * (Covers all approved and denied)
     */
    @Query(value = """
        SELECT DISTINCT plan_name 
        FROM eligibility_result 
        WHERE plan_name IS NOT NULL
    """, nativeQuery = true)
    List<String> getPlanNames();

    /**
     * Get distinct statuses derived from eligible flag  
     * Always returns only 'Approved' and 'Denied'
     */
    @Query(value = """
        SELECT DISTINCT 
               CASE 
                   WHEN eligible = 'Y' THEN 'Approved'
                   ELSE 'Denied'
               END AS status
        FROM eligibility_result
    """, nativeQuery = true)
    List<String> getPlanStatuses();
}
