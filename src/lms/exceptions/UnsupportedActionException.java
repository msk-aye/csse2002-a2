package lms.exceptions;

/**
 * Special Runtime exception -- Note that this is a Runtime (unchecked exception) which is unlike
 * our normal `extends Exception` as we want to be able to add this to a method without needing
 * to have it in a method signature.
 */
public class UnsupportedActionException extends RuntimeException {
    /**
     * Constructs a new UnsupportedActionException with no message.
     */
    public UnsupportedActionException() {
        super();
    }

    /**
     * Constructs a new UnsupportedActionException with the specified error message.
     * @param message a String containing the error message to be associated with this exception.
     */
    public UnsupportedActionException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnsupportedActionException with the specified error message and cause.
     * @param message a String containing the error message to be associated with this exception.
     * @param cause a Throwable object representing the cause of this exception.
     */
    public UnsupportedActionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UnsupportedActionException with the specified cause.
     * @param cause a Throwable object representing the cause of this exception.
     */
    public UnsupportedActionException(Throwable cause) {
        super(cause);
    }
}
