package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private final Map<String, LibraryBook> bookMap = new HashMap<>();
    private final Map<String, Member> memberMap = new HashMap<>();
    private final Map<String, LoanRecord> activeLoanMap = new HashMap<>();
    private final Queue<String> reservationQueue = new LinkedList<>(); // Queue<Reservation/String>
    private final Set<String> checkedOutIsbns = new HashSet<>();
    private final List<LoanRecord> loanHistory = new ArrayList<>();

    public void addBook(LibraryBook book) {
        if (bookMap.containsKey(book.getIsbn())) {
            throw new DuplicateEntityException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
        bookMap.put(book.getIsbn(), book);
    }

    public void registerMember(Member member) {
        if (memberMap.containsKey(member.getMemberId())) {
            throw new DuplicateEntityException("Member ID " + member.getMemberId() + " is already registered.");
        }
        memberMap.put(member.getMemberId(), member);
    }

    public void issueLoan(String loanId, String isbn, String memberId, LocalDate dueDate) {
        LibraryBook book = bookMap.get(isbn);
        if (book == null) {
            throw new ItemUnavailableException("Book not found for ISBN: " + isbn);
        }
        if (!memberMap.containsKey(memberId)) {
            throw new ItemUnavailableException("Member not found: " + memberId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new ItemUnavailableException("No available copies for loan on ISBN: " + isbn);
        }

        boolean alreadyBorrowed = activeLoanMap.values().stream()
                .anyMatch(l -> l.getIsbn().equals(isbn) && l.getMemberId().equals(memberId) && l.getStatus() == LoanStatus.ACTIVE);
        if (alreadyBorrowed) {
            throw new LoanLimitExceededException("Member already has an active loan for this book.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        LoanRecord loan = new LoanRecord(loanId, isbn, memberId, dueDate);
        
        activeLoanMap.put(loanId, loan);
        loanHistory.add(loan);
        checkedOutIsbns.add(isbn);
    }

    public void returnBook(String loanId, LocalDate returnDate) {
        LoanRecord loan = activeLoanMap.get(loanId);
        if (loan == null || loan.getStatus() == LoanStatus.RETURNED) {
            throw new ItemUnavailableException("Active loan not found with ID: " + loanId);
        }

        LibraryBook book = bookMap.get(loan.getIsbn());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            if (book.getAvailableCopies() == book.getTotalCopies()) {
                checkedOutIsbns.remove(book.getIsbn());
            }
        }

        loan.setReturnDate(returnDate);
        activeLoanMap.remove(loanId);
    }

    public void reserveBook(String isbn, String memberId) {
        if (!bookMap.containsKey(isbn)) {
            throw new ItemUnavailableException("Cannot reserve non-existent book ISBN: " + isbn);
        }
        if (!memberMap.containsKey(memberId)) {
            throw new ItemUnavailableException("Member not found: " + memberId);
        }
        reservationQueue.offer(isbn + ":" + memberId);
    }

    public String processNextReservation() {
        if (reservationQueue.isEmpty()) {
            throw new ItemUnavailableException("Reservation queue is empty.");
        }
        return reservationQueue.poll();
    }

    public List<LibraryBook> searchBooks(String keyword) {
        String query = keyword.toLowerCase();
        return bookMap.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query) || b.getAuthor().toLowerCase().contains(query) || b.getIsbn().equals(query))
                .collect(Collectors.toList());
    }

    public List<LoanRecord> getSortedLoanView() {
        List<LoanRecord> view = new ArrayList<>(loanHistory);
        Collections.sort(view);
        return view;
    }

    public CirculationSummary generateSummaryReport() {
        int totalBooks = bookMap.values().stream().mapToInt(LibraryBook::getTotalCopies).sum();
        int availableBooks = bookMap.values().stream().mapToInt(LibraryBook::getAvailableCopies).sum();
        int checkedOut = totalBooks - availableBooks;

        Map<MembershipType, Long> activeByType = activeLoanMap.values().stream()
                .map(l -> memberMap.get(l.getMemberId()))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Member::getMembershipType, Collectors.counting()));

        double fines = activeLoanMap.values().stream()
                .filter(l -> l.getDueDate().isBefore(LocalDate.now()))
                .mapToDouble(l -> 50.0) 
                .sum();

        return new CirculationSummary(
                totalBooks,
                availableBooks,
                checkedOut,
                memberMap.size(),
                activeLoanMap.size(),
                reservationQueue.size(),
                activeByType,
                fines
        );
    }

    public Queue<String> getReservationQueue() { return reservationQueue; }
    public Map<String, LibraryBook> getBookMap() { return bookMap; }
    public Map<String, Member> getMemberMap() { return memberMap; }
    public Map<String, LoanRecord> getActiveLoanMap() { return activeLoanMap; }
}