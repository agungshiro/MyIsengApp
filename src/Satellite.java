

import java.util.Date;

public interface Satellite {

    @Deprecated
    void getPosition(GroundStationPosition qth, SatPos satPos, Date time);

    boolean willBeSeen(GroundStationPosition qth);

    void calculateSatelliteVectors(Date time);

    SatPos calculateSatelliteGroundTrack();

    SatPos calculateSatPosForGroundStation(GroundStationPosition gsPos);

    TLE getTLE();

    SatPos getPosition(GroundStationPosition qth, Date time);
    
    SatPos getPosition(Date time);
}
