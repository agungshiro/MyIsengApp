

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Class which provides Pass Prediction.
 *
 * @author David A. B. Johnson, badgersoft
 *
 */
public class PassPredictor {

    private static final String UTC = "UTC";
    private static final String SOUTH = "south";
    private static final String NORTH = "north";
    private static final double SPEED_OF_LIGHT = 2.99792458E8;
    private static final double TWOPI = Math.PI * 2.0;

    private static final String DEADSPOT_NONE = "none";

    /** The time at which we do all the calculations. */
    static final TimeZone TZ = TimeZone.getTimeZone(UTC);

    private static Log log = LogFactory.getLog(PassPredictor.class);

    private boolean newTLE = true;

    private final TLE tle;
    private final GroundStationPosition qth;
    private Satellite sat;
    private boolean windBackTime;
    private final double meanMotion;
    private int iterationCount;
    private Date tca;

    /**
     * Constructor.
     *
     * @throws IllegalArgumentException bad argument passed in
     * @throws SatNotFoundException
     * @throws InvalidTleException
     */
    public PassPredictor(final TLE theTLE, final GroundStationPosition theQTH)
            throws IllegalArgumentException, InvalidTleException, SatNotFoundException {

        if (null == theTLE) {
            throw new IllegalArgumentException("TLE has not been set");
        }

        if (null == theQTH) {
            throw new IllegalArgumentException("QTH has not been set");
        }

        this.tle = theTLE;
        this.qth = theQTH;

        newTLE = true;
        validateData();

        meanMotion = theTLE.getMeanmo();
    }

    /**
     * Gets the downlink frequency corrected for doppler.
     *
     * @param freq the original frequency in Hz
     * @return the doppler corrected frequency in Hz
     * @throws InvalidTleException bad TLE passed in
     * @throws SatNotFoundException
     */
    public Long getDownlinkFreq(final Long freq, final Date date) throws InvalidTleException,
            SatNotFoundException {
        validateData();
        // get the current position
        final Calendar cal = Calendar.getInstance(TZ);
        cal.clear();
        cal.setTimeInMillis(date.getTime());
        final SatPos satPos = getSatPos(cal.getTime());
        final double rangeRate = satPos.getRangeRate();
        return (long)((double)freq * (SPEED_OF_LIGHT - rangeRate * 1000.0) / SPEED_OF_LIGHT);
    }

    private SatPos getSatPos(final Date time) throws InvalidTleException,
            SatNotFoundException {
        this.iterationCount++;
        return sat.getPosition(qth, time);
    }

    public Long getUplinkFreq(final Long freq, final Date date) throws InvalidTleException,
            SatNotFoundException {
        validateData();
        final Calendar cal = Calendar.getInstance(TZ);
        cal.clear();
        cal.setTimeInMillis(date.getTime());
        final SatPos satPos = getSatPos(cal.getTime());
        final double rangeRate = satPos.getRangeRate();
        return (long)((double)freq * (SPEED_OF_LIGHT + rangeRate * 1000.0) / SPEED_OF_LIGHT);
    }

    public SatPassTime nextSatPass(final Date date) throws InvalidTleException, SatNotFoundException {
        return nextSatPass(date, false);
    }

    /**
     *
     * Find the next satellite pass for a specific date
     *
     * @param date The date fo find the next pass for
     * @param windBack Whether to wind back 1/4 of an orbit
     * @return The satellite pass time
     * @throws InvalidTleException
     * @throws SatNotFoundException
     */
    public SatPassTime nextSatPass(final Date date, final boolean windBack)
            throws InvalidTleException, SatNotFoundException {

        int aosAzimuth;
        int losAzimuth;
        double maxElevation = 0;
        double elevation;

        validateData();

        String polePassed = DEADSPOT_NONE;

        // get the current position
        final Calendar cal = Calendar.getInstance(TZ);
        cal.clear();
        cal.setTimeInMillis(date.getTime());

        // wind back time 1/4 of an orbit
        if (windBack) {
            cal.add(Calendar.MINUTE, (int)(-24.0 * 60.0 / meanMotion / 4.0));
        }

        SatPos satPos = getSatPos(cal.getTime());
        SatPos prevPos = satPos;

        // test for the elevation being above the horizon
        if (satPos.getElevation() > 0.0) {

            // move time forward in 30 second intervals until the sat goes below
            // the horizon
            do {
                satPos = getPosition(cal, 60);
            }
            while (satPos.getElevation() > 0.0);

            // move time forward 3/4 orbit
            cal.add(Calendar.MINUTE, threeQuarterOrbitMinutes());
        }

        // now find the next time it comes above the horizon
        do {
            satPos = getPosition(cal, 60);
            final Date now = cal.getTime();
            elevation = satPos.getElevation();
            if (elevation > maxElevation) {
                maxElevation = elevation;
                tca = now;
            }
        }
        while (satPos.getElevation() < 0.0);

        // refine it to 5 seconds
        cal.add(Calendar.SECOND, -60);
        do {
            satPos = getPosition(cal, 5);
            final Date now = cal.getTime();
            elevation = satPos.getElevation();
            if (elevation > maxElevation) {
                maxElevation = elevation;
                tca = now;
            }
            prevPos = satPos;
        }
        while (satPos.getElevation() < 0.0);

        final Date startDate = satPos.getTime();

        aosAzimuth = (int)((satPos.getAzimuth() / (2.0 * Math.PI)) * 360.0);

        // now find when it goes below
        do {
            satPos = getPosition(cal, 30);
            final Date now = cal.getTime();
            final String currPolePassed = getPolePassed(prevPos, satPos);
            if (!currPolePassed.equals(DEADSPOT_NONE)) {
                polePassed = currPolePassed;
            }
            log.debug("Current pole passed: " + polePassed);
            elevation = satPos.getElevation();
            if (elevation > maxElevation) {
                maxElevation = elevation;
                tca = now;
            }
            prevPos = satPos;
        }
        while (satPos.getElevation() > 0.0);

        newTLE = true;
        validateData();

        // refine it to 5 seconds
        cal.add(Calendar.SECOND, -30);
        do {
            satPos = getPosition(cal, 5);
            final Date now = cal.getTime();
            elevation = satPos.getElevation();
            if (elevation > maxElevation) {
                maxElevation = elevation;
                tca = now;
            }
        }
        while (satPos.getElevation() > 0.0);

        final Date endDate = satPos.getTime();
        losAzimuth = (int)((satPos.getAzimuth() / (2.0 * Math.PI)) * 360.0);

        return new SatPassTime(startDate, endDate, tca, polePassed,
                aosAzimuth, losAzimuth, (maxElevation / (2.0 * Math.PI)) * 360.0);

    }

    /**
     * @param cal
     * @param offSet
     * @return
     * @throws InvalidTleException
     * @throws SatNotFoundException
     */
    private SatPos getPosition(final Calendar cal, final int offSet)
            throws InvalidTleException, SatNotFoundException {
        SatPos satPos;
        cal.add(Calendar.SECOND, offSet);
        satPos = getSatPos(cal.getTime());
        return satPos;
    }

    /**
     * Gets a list of SatPassTime
     *
     * @param start Date
     *
     *            newTLE = true; validateData();
     * @return List&lt;SatPassTime&gt;
     * @throws SatNotFoundException
     * @throws InvalidTleException
     */
    public List<SatPassTime> getPasses(final Date start, final int hoursAhead, final boolean windBack)
            throws InvalidTleException, SatNotFoundException {

        this.iterationCount = 0;

        this.windBackTime = windBack;

        final List<SatPassTime> passes = new ArrayList<SatPassTime>();

        Date trackStartDate = start;
        final Date trackEndDate = new Date(start.getTime() + (hoursAhead * 60L * 60L * 1000L));

        Date lastAOS;

        int count = 0;

        do {
            if (count > 0) {
                this.windBackTime = false;
            }
            final SatPassTime pass = nextSatPass(trackStartDate, this.windBackTime);
            lastAOS = pass.getStartTime();
            passes.add(pass);
            trackStartDate = new Date(pass.getEndTime().getTime() + (threeQuarterOrbitMinutes() * 60L * 1000L));
            count++;
        }
        while (lastAOS.compareTo(trackEndDate) < 0);

        return passes;
    }

    /**
     * @return the iterationCount
     */
    public final int getIterationCount() {
        return iterationCount;
    }

    private void validateData() throws InvalidTleException,
            SatNotFoundException {

        if (newTLE) {
            sat = SatelliteFactory.createSatellite(tle);

            if (null == sat) {
                throw new SatNotFoundException("Satellite has not been created");
            }
            else if (!sat.willBeSeen(qth)) {
                throw new SatNotFoundException(
                        "Satellite will never appear above the horizon");
            }
            newTLE = false;
        }
    }

    /**
     * @return time in mS for 3/4 of an orbit
     */
    private int threeQuarterOrbitMinutes() {
        return (int)(24.0 * 60.0 / tle.getMeanmo() * 0.75);
    }

    private String getPolePassed(final SatPos prevPos, final SatPos satPos) {
        String polePassed = DEADSPOT_NONE;

        final double az1 = prevPos.getAzimuth() / TWOPI * 360.0;
        final double az2 = satPos.getAzimuth() / TWOPI * 360.0;

        if (az1 > az2) {
            // we may be moving from 350 or greateer thru north
            if (az1 > 350 && az2 < 10) {
                polePassed = NORTH;
            }
            else {
                // we may be moving from 190 or greateer thru south
                if (az1 > 180 && az2 < 180) {
                    polePassed = SOUTH;
                }
            }
        }
        else {
            // we may be moving from 10 or less through north
            if (az1 < 10 && az2 > 350) {
                polePassed = NORTH;
            }
            else {
                // we may be moving from 170 or more through south
                if (az1 < 180 && az2 > 180) {
                    polePassed = SOUTH;
                }
            }
        }

        return polePassed;
    }

    /**
     * Calculates positions of satellite for a given point in time, time range and step incremen.
     *
     * @param referenceDate
     * @param incrementSeconds
     * @param minutesBefore
     * @param minutesAfter
     * @return list of SatPos
     * @throws SatNotFoundException
     * @throws InvalidTleException
     */
    public List<SatPos> getPositions(
            final Date referenceDate,
            final int incrementSeconds,
            final int minutesBefore,
            final int minutesAfter)
            throws InvalidTleException, SatNotFoundException {

        Date trackDate = new Date(referenceDate.getTime() - (minutesBefore * 60L * 1000L));
        final Date endDateDate = new Date(referenceDate.getTime() + (minutesAfter * 60L * 1000L));

        final List<SatPos> positions = new ArrayList<SatPos>();

        while (trackDate.before(endDateDate)) {

            positions.add(getSatPos(trackDate));

            trackDate = new Date(trackDate.getTime() + (incrementSeconds * 1000));
        }

        return positions;
    }
}
