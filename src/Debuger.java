import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.lang.Math;
import java.text.SimpleDateFormat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;


public class Debuger {

	public static void main(String[] args) throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		// TODO Auto-generated method stub
		
		String[] tleString = {
				"LAPAN-A2",
				"1 40931U 15052B   21129.30274365  .00000735  00000-0  13172-4 0  9998",
				"2 40931   5.9964 171.5481 0013196 213.2885 146.6459 14.76674337303502"
				};
		TLE theTLE = new TLE(tleString);
		
		GroundStationPosition gs = new GroundStationPosition(
				-6.5356560,
				106.701,
				50,
				"Rancabungur"
				);
		
		PassPredictor pp = new PassPredictor(theTLE, gs);
		
		Satellite sat = SatelliteFactory.createSatellite(theTLE);
		
		Date dt = new Date();
		
		SatPos pos = sat.getPosition(dt);
		SimpleDateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
		
		List<SatPassTime> passes = pp.getPasses(dt, 24, false);
		
		System.out.println(passes.get(0).getStartTime());
		
		//ArrayList<String> colSeed = new ArrayList<String>( Arrays.asList("Sat","AOS","LOS","Max El"));
		
		//Object[] cols = colSeed.toArray();
		//System.out.println(colSeed);
		
		AlarmSound alarm = new AlarmSound();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				alarm.playAlarm("src/TF010.WAV");
			}
		
		}).start();
	}

}
