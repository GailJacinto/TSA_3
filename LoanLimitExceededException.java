package com.mycompany.jacinto_bookbridge_tsa3;

/**
 *
 * @author Gail Jacinto
 */
public class LoanLimitExceededException extends LibraryException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}