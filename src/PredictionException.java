

/**
 * @author David A. B. Johnson, badgersoft
 */
public class PredictionException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public PredictionException() {
        super();
    }

    /**
     * @param message the exception message
     */
    public PredictionException(final String message) {
        super(message);
    }

    /**
     * @param cause the original exception
     */
    public PredictionException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message the exception message
     * @param cause the original exception
     */
    public PredictionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
