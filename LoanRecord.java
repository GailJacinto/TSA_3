package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.time.LocalDate;
import java.util.Objects;

public class LoanRecord implements Comparable<LoanRecord> {
    private String loanId;
    private String isbn;
    private String memberId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;

    public LoanRecord(String loanId, String isbn, String memberId, LocalDate dueDate) {
        this.loanId = loanId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = LocalDate.now();
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
    }

    public String getLoanId() { return loanId; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public LoanStatus getStatus() { return status; }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = LoanStatus.RETURNED;
    }

    public void setStatus(LoanStatus status) { this.status = status; }

    @Override
    public int compareTo(LoanRecord other) {
        boolean thisActive = this.status != LoanStatus.RETURNED;
        boolean otherActive = other.status != LoanStatus.RETURNED;
        if (thisActive != otherActive) {
            return thisActive ? -1 : 1;
        }
        int dateComp = this.dueDate.compareTo(other.dueDate);
        if (dateComp != 0) {
            return dateComp;
        }
        return this.loanId.compareTo(other.loanId);
    }

    @Override
    public String toString() {
        return String.format("Loan[ID=%s, ISBN=%s, Member=%s, Due=%s, Status=%s]",
                loanId, isbn, memberId, dueDate, status);
    }
}