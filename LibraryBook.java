package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Objects;

public class LibraryBook {
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    private BookStatus status;

    public LibraryBook(String isbn, String title, String author, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.status = totalCopies > 0 ? BookStatus.AVAILABLE : BookStatus.MAINTENANCE;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public BookStatus getStatus() { return status; }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        this.status = this.availableCopies > 0 ? BookStatus.AVAILABLE : BookStatus.CHECKED_OUT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibraryBook)) return false;
        LibraryBook that = (LibraryBook) o;
        return isbn.equals(that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book[ISBN=%s, Title='%s', Author='%s', Available=%d/%d, Status=%s]",
                isbn, title, author, availableCopies, totalCopies, status);
    }
}