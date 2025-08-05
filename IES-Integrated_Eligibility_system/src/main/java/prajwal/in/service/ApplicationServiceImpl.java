package prajwal.in.service;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Override
    public Map<String, Object> getDashboardStats() {
        // TODO: Replace with actual DB queries and calculations
        Map<String, Object> stats = new HashMap<>();
        stats.put("planCount", 23);
        stats.put("citizensApproved", 8577641);
        stats.put("citizensDenied", 4565);
        stats.put("benefitsGiven", 12566);
        return stats;
    }

    @Override
    public List<Object> findAllApplications() {
        // TODO: Replace with DB call to fetch all applications
        return List.of(
            Map.of("caseNum", "1234", "name", "John Doe", "mobileNumber", "+1-8457475723", "gender", "Male", "ssn", "897-6543-45"),
            Map.of("caseNum", "1235", "name", "Smith", "mobileNumber", "+1-4946769567", "gender", "Male", "ssn", "678-3543-68"),
            Map.of("caseNum", "1236", "name", "Orlen", "mobileNumber", "+1-5673676667", "gender", "Female", "ssn", "489-2934-92")
        );
    }

    @Override
    public List<Object> findApplicationsByCaseWorkerId(Long caseWorkerId) {
        // TODO: Replace with DB query to filter by caseWorkerId
        // For now return only first record as dummy filtered data
        if (caseWorkerId != null) {
            return List.of(
                Map.of("caseNum", "1234", "name", "John Doe", "mobileNumber", "+1-8457475723", "gender", "Male", "ssn", "897-6543-45")
            );
        }
        return List.of();
    }
}
