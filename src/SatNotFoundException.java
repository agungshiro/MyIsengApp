

/**
 * @author David A. B. Johnson, badgersoft
 */
public class SatNotFoundException extends PredictionException {

    private static final long serialVersionUID = 3389434245667560642L;

    /**
     *
     */
    public SatNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public SatNotFoundException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public SatNotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SatNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
