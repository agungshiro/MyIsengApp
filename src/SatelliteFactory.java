

/**
 * The factory which creates a LEO or Deep Space Satellite.
 *
 * @author G4DPZ
 *
 */
public final class SatelliteFactory {

    /**
     * Default constructor.
     */
    private SatelliteFactory() {

    }

    /**
     * Creates a <code>Satellite</code> from a <code>TLE</code>.
     *
     * @param tle The 'Three Line Elements'
     * @return <code>Satellite</code>
     * @throws IllegalArgumentException when the given TLE is null or the data is incorrect
     */
    public static synchronized Satellite createSatellite(final TLE tle)
            throws IllegalArgumentException {

        if (null == tle) {
            throw new IllegalArgumentException("TLE was null");
        }

        Satellite satellite = null;

        if (tle.isDeepspace()) {
            satellite = new DeepSpaceSatellite(tle);
        }
        else {
            satellite = new LEOSatellite(tle);
        }
        return satellite;
    }
}
