package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Map;

public record RequestSummary(
        int totalRequests,
        int pendingQueueSize,
        int uniqueStudentsCount,
        Map<ServiceCategory, Long> countByCategory,
        Map<RequestStatus, Long> countByStatus,
        double averageEstimatedDays
) {}