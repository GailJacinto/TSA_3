
package com.mycompany.jacinto_campusconnect_tsa3;

/**
 *
 * @author Gail Jacinto
 */
import java.util.Optional;

public final class Result<T> {
    private final T value;
    private final String message;
    private final boolean success;

    private Result(T value, String message, boolean success) {
        this.value = value;
        this.message = message;
        this.success = success;
    }

    public static <T> Result<T> ok(T value, String message) {
        return new Result<>(value, message, true);
    }

    public static <T> Result<T> empty(String message) {
        return new Result<>(null, message, false);
    }

    public Optional<T> getValue() { return Optional.ofNullable(value); }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}