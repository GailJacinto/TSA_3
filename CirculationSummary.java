package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Map;

public record CirculationSummary(
        int totalBooks,
        int availableBooks,
        int checkedOutBooks,
        int totalMembers,
        int activeLoans,
        int pendingReservations,
        Map<MembershipType, Long> activeLoansPerType,
        double estimatedFines
) {}