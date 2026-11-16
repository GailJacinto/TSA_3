package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Objects;

public class ServiceRequest implements Comparable<ServiceRequest> {
    private final String ticketId;
    private final String studentId;
    private final String description;
    private final ServiceCategory category;
    private final int priority; 
    private final int estimatedDays;
    private RequestStatus status;

    public ServiceRequest(String ticketId, String studentId, String description,
                          ServiceCategory category, int priority, int estimatedDays) {
        this.ticketId = ticketId;
        this.studentId = studentId;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.estimatedDays = estimatedDays;
        this.status = RequestStatus.PENDING;
    }

    public String getTicketId() { return ticketId; }
    public String getStudentId() { return studentId; }
    public String getDescription() { return description; }
    public ServiceCategory getCategory() { return category; }
    public int getPriority() { return priority; }
    public int getEstimatedDays() { return estimatedDays; }
    public RequestStatus getStatus() { return status; }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(ServiceRequest other) {
        int priorityComp = Integer.compare(other.priority, this.priority);
        if (priorityComp != 0) {
            return priorityComp;
        }
        int daysComp = Integer.compare(this.estimatedDays, other.estimatedDays);
        if (daysComp != 0) {
            return daysComp;
        }
        return this.ticketId.compareTo(other.ticketId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceRequest that)) return false;
        return ticketId.equals(that.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return String.format("Ticket[ID=%s, Student=%s, Category=%s, Priority=%d, EstDays=%d, Status=%s, Desc='%s']",
                ticketId, studentId, category, priority, estimatedDays, status, description);
    }
}