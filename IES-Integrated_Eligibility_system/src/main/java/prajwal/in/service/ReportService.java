package prajwal.in.service;

import java.util.List;
import prajwal.in.dto.ReportRowDTO;
import prajwal.in.dto.SearchRequest;

public interface ReportService {

    List<ReportRowDTO> searchReport(SearchRequest req);

    List<String> getPlanNames();

    List<String> getPlanStatuses();
}
