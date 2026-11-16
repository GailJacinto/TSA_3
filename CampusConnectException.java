package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
public class CampusConnectException extends RuntimeException {
    public CampusConnectException(String message) {
        super(message);
    }

    public CampusConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}