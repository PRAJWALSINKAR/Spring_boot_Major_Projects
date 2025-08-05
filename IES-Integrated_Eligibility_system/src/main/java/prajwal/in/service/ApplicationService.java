package prajwal.in.service;

import java.util.List;
import java.util.Map;

public interface ApplicationService {
    Map<String, Object> getDashboardStats();
    List<Object> findAllApplications();
    List<Object> findApplicationsByCaseWorkerId(Long caseWorkerId);
}
