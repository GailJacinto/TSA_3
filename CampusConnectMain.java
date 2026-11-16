package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final RequestService service = new RequestService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("==========================================");
        System.out.println("  CAMPUSCONNECT SERVICE DESK      ");
        System.out.println("==========================================");

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> handleAddRequest();
                    case "2" -> handleFindTicket();
                    case "3" -> handleProcessNext();
                    case "4" -> handleUpdateStatus();
                    case "5" -> handleRemoveTicket();
                    case "6" -> handleFilterRequests();
                    case "7" -> handleListAllSorted();
                    case "8" -> handleGenerateReport();
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting CampusConnect Service Desk. Goodbye!");
                    }
                    default -> System.out.println("Invalid option. Please select 0-8.");
                }
            } catch (CampusConnectException e) {
                System.out.println("\n[Operation Blocked] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\n[Input Error] Invalid number or enum value entered.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Submit Service Request");
        System.out.println("2. Search Ticket (by Ticket ID or Student ID)");
        System.out.println("3. Process Next Pending Request");
        System.out.println("4. Update Request Status");
        System.out.println("5. Remove Service Request");
        System.out.println("6. Filter by Category & Status");
        System.out.println("7. View All Requests (Sorted)");
        System.out.println("8. Generate Summary Report");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private static void handleAddRequest() {
        System.out.print("Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim();
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter Category (IT_SUPPORT, REGISTRAR, FACILITIES, FINANCIAL_AID, LIBRARY): ");
        ServiceCategory category = ServiceCategory.valueOf(scanner.nextLine().trim().toUpperCase());
        System.out.print("Enter Priority (1-5): ");
        int priority = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter Estimated Days: ");
        int days = Integer.parseInt(scanner.nextLine().trim());

        service.add(new ServiceRequest(ticketId, studentId, desc, category, priority, days));
        System.out.println("Success: Ticket " + ticketId + " submitted to queue.");
    }

    private static void handleFindTicket() {
        System.out.print("Search by (1) Ticket ID or (2) Student ID: ");
        String mode = scanner.nextLine().trim();

        if ("1".equals(mode)) {
            System.out.print("Enter Ticket ID: ");
            Result<ServiceRequest> result = service.findById(scanner.nextLine().trim());
            result.getValue().ifPresentOrElse(
                    System.out::println,
                    () -> System.out.println(result.getMessage())
            );
        } else if ("2".equals(mode)) {
            System.out.print("Enter Student ID: ");
            List<ServiceRequest> list = service.findByStudentId(scanner.nextLine().trim());
            if (list.isEmpty()) {
                System.out.println("No tickets found for that Student ID.");
            } else {
                list.forEach(System.out::println);
            }
        } else {
            System.out.println("Invalid search choice.");
        }
    }

    private static void handleProcessNext() {
        ServiceRequest next = service.processNext();
        System.out.println("Now Processing -> " + next);
    }

    private static void handleUpdateStatus() {
        System.out.print("Enter Ticket ID: ");
        String ticketId = scanner.nextLine().trim();
        System.out.print("Enter New Status (PENDING, IN_PROGRESS, RESOLVED, CANCELLED): ");
        RequestStatus status = RequestStatus.valueOf(scanner.nextLine().trim().toUpperCase());

        service.updateStatus(ticketId, status);
        System.out.println("Success: Ticket " + ticketId + " updated to " + status + ".");
    }

    private static void handleRemoveTicket() {
        System.out.print("Enter Ticket ID to remove: ");
        String ticketId = scanner.nextLine().trim();
        service.remove(ticketId);
        System.out.println("Success: Ticket " + ticketId + " removed.");
    }

    private static void handleFilterRequests() {
        System.out.print("Enter Category (or press Enter for ALL): ");
        String catInput = scanner.nextLine().trim();
        ServiceCategory category = catInput.isEmpty() ? null : ServiceCategory.valueOf(catInput.toUpperCase());

        System.out.print("Enter Status (or press Enter for ALL): ");
        String statInput = scanner.nextLine().trim();
        RequestStatus status = statInput.isEmpty() ? null : RequestStatus.valueOf(statInput.toUpperCase());

        List<ServiceRequest> filtered = service.filterByCategoryAndStatus(category, status);
        if (filtered.isEmpty()) {
            System.out.println("No service requests match the selected filter.");
        } else {
            filtered.forEach(System.out::println);
        }
    }

    private static void handleListAllSorted() {
        List<ServiceRequest> sorted = service.listAllSorted();
        if (sorted.isEmpty()) {
            System.out.println("No service requests recorded.");
        } else {
            sorted.forEach(System.out::println);
        }
    }

    private static void handleGenerateReport() {
        RequestSummary summary = service.generateSummaryReport();
        System.out.println("\n=== CAMPUSCONNECT SYSTEM REPORT ===");
        System.out.println("Total Requests Recorded:  " + summary.totalRequests());
        System.out.println("Pending Queue Size:       " + summary.pendingQueueSize());
        System.out.println("Active Unique Students:   " + summary.uniqueStudentsCount());
        System.out.println("Requests by Category:     " + summary.countByCategory());
        System.out.println("Requests by Status:       " + summary.countByStatus());
        System.out.println("Average Estimated Days:   " + String.format("%.2f", summary.averageEstimatedDays()));
    }
}