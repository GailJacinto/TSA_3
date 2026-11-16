package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.*;
import java.util.stream.Collectors;

public class RequestService {
    private final Map<String, ServiceRequest> ticketMap = new HashMap<>();
    private final Queue<ServiceRequest> pendingQueue = new LinkedList<>();
    private final Set<String> activeStudentIds = new HashSet<>();

    public void add(ServiceRequest request) {
        if (request == null || request.getTicketId().isBlank() || request.getStudentId().isBlank()) {
            throw new InvalidRequestException("Ticket ID and Student ID cannot be blank.");
        }
        if (request.getPriority() < 1 || request.getPriority() > 5) {
            throw new InvalidRequestException("Priority must be between 1 and 5.");
        }
        if (request.getEstimatedDays() <= 0) {
            throw new InvalidRequestException("Estimated days must be greater than zero.");
        }
        if (ticketMap.containsKey(request.getTicketId())) {
            throw new DuplicateTicketException("Ticket ID " + request.getTicketId() + " already exists.");
        }

        ticketMap.put(request.getTicketId(), request);
        pendingQueue.offer(request);
        activeStudentIds.add(request.getStudentId());
    }

    public Result<ServiceRequest> findById(String ticketId) {
        var req = ticketMap.get(ticketId);
        return req != null
                ? Result.ok(req, "Ticket found.")
                : Result.empty("No ticket found with ID: " + ticketId);
    }

    public List<ServiceRequest> findByStudentId(String studentId) {
        return ticketMap.values().stream()
                .filter(r -> r.getStudentId().equalsIgnoreCase(studentId))
                .collect(Collectors.toList());
    }

    public List<ServiceRequest> filterByCategoryAndStatus(ServiceCategory category, RequestStatus status) {
        return ticketMap.values().stream()
                .filter(r -> (category == null || r.getCategory() == category))
                .filter(r -> (status == null || r.getStatus() == status))
                .collect(Collectors.toList());
    }

    public ServiceRequest processNext() {
        ServiceRequest next = pendingQueue.poll();
        if (next == null) {
            throw new InvalidRequestException("Pending queue is empty. No requests to process.");
        }
        next.setStatus(RequestStatus.IN_PROGRESS);
        return next;
    }

    public void updateStatus(String ticketId, RequestStatus newStatus) {
        ServiceRequest req = ticketMap.get(ticketId);
        if (req == null) {
            throw new InvalidRequestException("Cannot update status: Ticket ID " + ticketId + " not found.");
        }

        RequestStatus current = req.getStatus();
        if (current == RequestStatus.RESOLVED || current == RequestStatus.CANCELLED) {
            throw new InvalidRequestException("Invalid transition: Cannot change status of a " + current + " ticket.");
        }
        if (current == RequestStatus.PENDING && newStatus == RequestStatus.RESOLVED) {
            throw new InvalidRequestException("Invalid transition: Ticket must be IN_PROGRESS before it can be RESOLVED.");
        }

        req.setStatus(newStatus);

        if (newStatus != RequestStatus.PENDING) {
            pendingQueue.remove(req);
        }
        syncActiveStudents(req.getStudentId());
    }

    public void remove(String ticketId) {
        ServiceRequest removed = ticketMap.remove(ticketId);
        if (removed == null) {
            throw new InvalidRequestException("Cannot remove: Ticket ID " + ticketId + " does not exist.");
        }
        pendingQueue.remove(removed);
        syncActiveStudents(removed.getStudentId());
    }

    private void syncActiveStudents(String studentId) {
        boolean hasOtherActive = ticketMap.values().stream()
                .anyMatch(r -> r.getStudentId().equals(studentId)
                        && (r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.IN_PROGRESS));
        if (!hasOtherActive) {
            activeStudentIds.remove(studentId);
        }
    }

    public List<ServiceRequest> listAllSorted() {
        List<ServiceRequest> view = new ArrayList<>(ticketMap.values());
        Collections.sort(view);
        return Collections.unmodifiableList(view);
    }

    public RequestSummary generateSummaryReport() {
        Map<ServiceCategory, Long> byCategory = ticketMap.values().stream()
                .collect(Collectors.groupingBy(ServiceRequest::getCategory, Collectors.counting()));

        Map<RequestStatus, Long> byStatus = ticketMap.values().stream()
                .collect(Collectors.groupingBy(ServiceRequest::getStatus, Collectors.counting()));

        double avgDays = ticketMap.values().stream()
                .mapToInt(ServiceRequest::getEstimatedDays)
                .average()
                .orElse(0.0);

        return new RequestSummary(
                ticketMap.size(),
                pendingQueue.size(),
                activeStudentIds.size(),
                byCategory,
                byStatus,
                avgDays
        );
    }
}