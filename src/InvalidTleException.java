

/**
 * @author David A. B. Johnson, badgersoft
 */
public class InvalidTleException extends PredictionException {

    /**
     *
     */
    private static final long serialVersionUID = -1993764584699304542L;

    /**
     *
     */
    public InvalidTleException() {
    }

    /**
     * @param message The message
     */
    public InvalidTleException(final String message) {
        super(message);
    }

    /**
     * @param cause The cause
     */
    public InvalidTleException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message The message
     * @param cause The cause
     */
    public InvalidTleException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
