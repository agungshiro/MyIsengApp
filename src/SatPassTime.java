

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SatPassTime implements Serializable {

    private static final long serialVersionUID = -6408342316986801301L;

    private Date startTime;
    private Date endTime;
    private Date tca;
    private String polePassed;
    private int aos;
    private int los;
    private double maxEl;

    private static final String NEW_LINE = "\n";
    private static final String DEG_NL = " deg.\n";

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm:ss a");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMMMM d, yyyy");

    public SatPassTime(final Date startTime, final Date endTime, final String polePassed,
            final int aos, final int los, final double maxEl) {
        this(startTime,
            endTime,
            new Date((startTime.getTime() + endTime.getTime()) / 2),
            polePassed,
            aos,
            los,
            maxEl);
    }

    public SatPassTime() {
    }

    public SatPassTime(final Date startTime, final Date endTime, final Date tca, final String polePassed,
            final int aosAzimuth, final int losAzimuth,
            final double maxEl) {
        // TODO Auto-generated constructor stub
        this.startTime = new Date(startTime.getTime());
        this.endTime = new Date(endTime.getTime());
        this.polePassed = polePassed;
        this.aos = aosAzimuth;
        this.los = losAzimuth;
        this.maxEl = maxEl;
        this.tca = new Date(tca.getTime());
    }

    public final Date getStartTime() {
        return new Date(startTime.getTime());
    }

    public final Date getEndTime() {
        return new Date(endTime.getTime());
    }

    public final Date getTCA() {
        return new Date(tca.getTime());
    }

    public final void setTCA(final Date theTCA) {
        this.tca = theTCA;
    }

    public final String getPolePassed() {
        return polePassed;
    }

    /**
     * @return the aos azimuth
     */
    public final int getAosAzimuth() {
        return aos;
    }

    /**
     * @return the los azimuth
     */
    public final int getLosAzimuth() {
        return los;
    }

    /**
     * @return the maxEl
     */
    public final double getMaxEl() {
        return maxEl;
    }

    /**
     * Returns a string representing the contents of the object.
     */
    @Override
    public String toString() {

        final double duration = (endTime.getTime() - startTime.getTime()) / 60000.0;

        return "Date: " + DATE_FORMAT.format(startTime)
                + NEW_LINE
                + "Start Time: "
                + TIME_FORMAT.format(startTime)
                + NEW_LINE
                + "End Time: "
                + TIME_FORMAT.format(endTime)
                + NEW_LINE
                + String.format("Duration: %4.1f min.\n", duration)
                + "AOS Azimuth: " + aos + DEG_NL
                + String.format("Max Elevation: %4.1f deg.\n", maxEl)
                + "LOS Azimuth: " + los + " deg.";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SatPassTime)) return false;
        SatPassTime that = (SatPassTime) o;
        return aos == that.aos &&
                los == that.los &&
                Double.compare(that.getMaxEl(), getMaxEl()) == 0 &&
                Objects.equals(getStartTime(), that.getStartTime()) &&
                Objects.equals(getEndTime(), that.getEndTime()) &&
                Objects.equals(tca, that.tca) &&
                Objects.equals(getPolePassed(), that.getPolePassed());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getStartTime(), getEndTime(), tca, getPolePassed(), aos, los, getMaxEl());
    }
}
