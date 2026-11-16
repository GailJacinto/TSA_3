package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final LibraryService service = new LibraryService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;

        System.out.println("==========================================");
        System.out.println("   BOOKBRIDGE LIBRARY SERVICE      ");
        System.out.println("==========================================");

        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> handleAddBook();
                    case "2" -> handleRegisterMember();
                    case "3" -> handleIssueLoan();
                    case "4" -> handleReturnBook();
                    case "5" -> handleReserveBook();
                    case "6" -> handleProcessNextReservation();
                    case "7" -> handleSearchBooks();
                    case "8" -> handleViewSortedLoans();
                    case "9" -> handleGenerateReport();
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting BookBridge Library Service. Goodbye!");
                    }
                    default -> System.out.println("Invalid option. Please select 0-9.");
                }
            } catch (LibraryException e) {
                System.out.println("\n[Operation Blocked] " + e.getMessage());
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("\n[Input Error] Invalid number or date format (Use YYYY-MM-DD).");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add Book");
        System.out.println("2. Register Member");
        System.out.println("3. Issue Loan");
        System.out.println("4. Return Book");
        System.out.println("5. Reserve Book");
        System.out.println("6. Process Next Reservation");
        System.out.println("7. Search Catalog");
        System.out.println("8. View Sorted Loans");
        System.out.println("9. Generate Summary Report");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private static void handleAddBook() {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter Total Copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());

        service.addBook(new LibraryBook(isbn, title, author, copies));
        System.out.println("Success: Book '" + title + "' added to catalog.");
    }

    private static void handleRegisterMember() {
        System.out.print("Enter Member ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Membership Type (STUDENT, FACULTY, GUEST, RESEARCHER): ");
        MembershipType type = MembershipType.valueOf(scanner.nextLine().trim().toUpperCase());

        service.registerMember(new Member(id, name, email, type));
        System.out.println("Success: Member '" + name + "' registered.");
    }

    private static void handleIssueLoan() {
        System.out.print("Enter Loan ID: ");
        String loanId = scanner.nextLine().trim();
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Due Date (YYYY-MM-DD): ");
        LocalDate dueDate = LocalDate.parse(scanner.nextLine().trim());

        service.issueLoan(loanId, isbn, memberId, dueDate);
        System.out.println("Success: Loan " + loanId + " issued.");
    }

    private static void handleReturnBook() {
        System.out.print("Enter Loan ID to return: ");
        String loanId = scanner.nextLine().trim();
        service.returnBook(loanId, LocalDate.now());
        System.out.println("Success: Loan " + loanId + " marked as RETURNED.");
    }

    private static void handleReserveBook() {
        System.out.print("Enter Book ISBN to reserve: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();

        service.reserveBook(isbn, memberId);
        System.out.println("Success: Reservation added to queue.");
    }

    private static void handleProcessNextReservation() {
        String next = service.processNextReservation();
        System.out.println("Processed Reservation (ISBN:MemberID) -> " + next);
    }

    private static void handleSearchBooks() {
        System.out.print("Enter search keyword (Title, Author, or ISBN): ");
        String keyword = scanner.nextLine().trim();
        List<LibraryBook> results = service.searchBooks(keyword);

        if (results.isEmpty()) {
            System.out.println("No books matched your search.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void handleViewSortedLoans() {
        List<LoanRecord> loans = service.getSortedLoanView();
        if (loans.isEmpty()) {
            System.out.println("No loan records found.");
        } else {
            loans.forEach(System.out::println);
        }
    }

    private static void handleGenerateReport() {
        CirculationSummary summary = service.generateSummaryReport();
        System.out.println("\n=== BOOKBRIDGE SYSTEM REPORT ===");
        System.out.println("Total Copies in Catalog: " + summary.totalBooks());
        System.out.println("Available Copies:        " + summary.availableBooks());
        System.out.println("Checked Out Copies:      " + summary.checkedOutBooks());
        System.out.println("Registered Members:      " + summary.totalMembers());
        System.out.println("Active Loans:            " + summary.activeLoans());
        System.out.println("Pending Reservations:    " + summary.pendingReservations());
        System.out.println("Active Loans per Type:   " + summary.activeLoansPerType());
        System.out.println("Estimated Overdue Fines: P" + String.format("%.2f", summary.estimatedFines()));
    }
}