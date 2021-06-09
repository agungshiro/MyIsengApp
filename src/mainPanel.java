import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JTable;

public class mainPanel extends JPanel {
	private JTextField timeField;
	private JButton startStopTimeBtn;
	private Timer globalTime;
	private boolean timeStop = false;
	
	private JTree objectTree;
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode satelliteNode;
	private DefaultMutableTreeNode observerNode;
	
	private LinkedHashMap<String,ArrayList<String>> satObj, obsObj;
	private JTable satTable;
	private JTable obsTable;
	
	private DefaultTableModel tModel;
	
	private ArrayList<Satellite> satList;
	private ArrayList<GroundStationPosition> obsList;
	
	private LinkedHashMap<String,HashMap<Date,String[]>> schedule;
	
	/**
	 * Create the panel.
	 */
	public mainPanel() {
		setLayout(null);
		
		
		satList = new ArrayList<>();
		obsList = new ArrayList<>();
		
		JLabel lblNewLabel = new JLabel("Time");
		lblNewLabel.setBounds(222, 11, 31, 16);
		add(lblNewLabel);
		
		// Time Text Field
		timeField = new JTextField();
		timeField.setBounds(265, 6, 130, 26);
		add(timeField);
		timeField.setColumns(10);
		
		// Start Stop Button
		startStopTimeBtn = new JButton("stop");
		startStopTimeBtn.setBounds(407, 6, 75, 29);
		add(startStopTimeBtn);
		
		// Object Tree
		objectTree = new JTree();
		this.buildTree();
		objectTree.setBounds(17, 42, 139, 387);
		add(objectTree);
		
		//satTable = new JTable();
		initSatTable();
		satTable.setBounds(168, 42, 534, 123);
		add(satTable);
		JScrollPane satTabScroll = new JScrollPane(satTable);
		satTabScroll.setBounds(168, 42, 534, 123);
		add(satTabScroll);
		
		//obsTable
		obsTable = new JTable();
		//this.initObsTable();
		obsTable.setBounds(168, 198, 534, 231);
		obsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		add(obsTable);
		JScrollPane obsTabScroll = new JScrollPane(obsTable);
		obsTabScroll.setBounds(168, 198, 534, 231);
		add(obsTabScroll);
		
		// Time Function
		globalTime = new Timer(1000,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timeString();
				if (satList.size() > 0) {
					paintSatDataToTable();
				}
			}
			
		});
		globalTime.start();
		
		// Start Stop time
		this.startStopTimeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(!timeStop) {
					timeStop = true;
					startStopTimeBtn.setText("Start");
					globalTime.stop();
				} else {
					timeStop = false;
					startStopTimeBtn.setText("Stop");
					globalTime.start();
				}
			}
		});

	}
	
	/**
	 * Build Tree
	 */
	private void buildTree() {
		root = new DefaultMutableTreeNode("Object");
		satelliteNode = new DefaultMutableTreeNode("Satellite");
		observerNode = new DefaultMutableTreeNode("Observer");
		root.add(satelliteNode);
		root.add(observerNode);
		objectTree = new JTree(root);
		
	}
	
	/**
	 * Set Collection for satellite tle data 
	 * @param map
	 */
	public void setSelectedSatObj(HashMap<String,ArrayList<String>> map) {
		/**
		satObj.putAll(map);
		for(String key:map.keySet()) {
			this.addSatToTree(key);
		}
		this.objectTree.repaint();
		**/
		TLE tle;
		Satellite sat;
		String key;
		ArrayList<String> val;
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			key = entry.getKey();
			val = entry.getValue();
			
			this.addSatToTree(key);
			String[] param = {key, val.get(0),val.get(1)};
			
			tle = new TLE(param);
			sat = SatelliteFactory.createSatellite(tle);
			satList.add(sat);
		}
		this.objectTree.repaint();
		this.paintSatDataToTable();
	}
	
	
	private void paintSatDataToTable() {
		tModel.setRowCount(0);
		Date dt = new Date();
		
		for(int x = 0; x < this.satList.size(); x++) {
			Satellite sat = satList.get(x);
			TLE tle = sat.getTLE();
			SatPos pos = sat.getPosition(dt);
			Object[] satData = {tle.getName(),Math.toDegrees(pos.getLatitude()),Math.toDegrees(pos.getLongitude())};
			this.tModel.addRow(satData);
		}
	}
	
	/**
	 * Set Collection for Observer data 
	 * @param map
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	public void setNewObsObj (HashMap<String, ArrayList<String>> map) throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		/**
		obsObj.putAll(map);
		for(String key:map.keySet()) {
			this.addObsToTree(key);
		}
		**/
		for (Map.Entry<String, ArrayList<String>> entry: map.entrySet()) {
			GroundStationPosition gsObs = new GroundStationPosition(
						Double.parseDouble(entry.getValue().get(0)),
						Double.parseDouble(entry.getValue().get(1)),
						Double.parseDouble(entry.getValue().get(2)),
						entry.getKey()
					);
			obsList.add(gsObs);
			this.addObsToTree(entry.getKey());
		}
		this.objectTree.repaint();
		this.initObsTable();
	}
	
	/**
	 * Add Satellite name to the tree
	 * @param sat
	 */
	private void addSatToTree(String sat) {
		this.satelliteNode.add(new DefaultMutableTreeNode(sat));
	}
	
	/**
	 * Add Observer Name to the tree
	 * @param sat
	 */
	private void addObsToTree(String sat) {
		this.observerNode.add(new DefaultMutableTreeNode(sat));
	}
	
	/**
	 * Generate time string
	 */
	public void timeString() {
		String timeNow = "";
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		timeNow = Integer.toString(h) + ":" + Integer.toString(m) + ":" + Integer.toString(s);
		
		this.timeField.setText(timeNow);
		
		//return timeNow;
	}
	
	/**
	 * Initiate Setellite table header
	 */
	private void initSatTable() {
		String[] col = {
				"Satellite", "Latitude", "Longitude"
		};
		int numRow = 0;
		tModel = new DefaultTableModel(numRow,col.length);
		tModel.setColumnIdentifiers(col);
		satTable = new JTable(tModel);
	}
	
	/**
	 * Initiate Observer table header
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	private void initObsTable() throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		DefaultTableModel obsTabModel = new DefaultTableModel();
		ArrayList<String> colSeed = new ArrayList<String>(Arrays.asList("Sats ","AOS","LOS","Max El"));
		ArrayList<String> col = new ArrayList<>();
		int obsNum = obsList.size();
		
		for(int x = 0; x < obsNum; x++) {
			col.addAll(colSeed);
		}
		
		Object[] arrCol = col.toArray();
		
		
		
		
		
		// Todo : Insert pass data to the column
		this.generateSchedule();
		String[][] arrSch = this.scheduleToTable();
		
		//ArrayUtils.addAll(arrCol, arrSch);
		
		obsTabModel.setColumnIdentifiers(arrCol);
		for(int x=0; x < arrSch.length; x++) {
			obsTabModel.addRow(arrSch[x]);
		}
		
		// Make sure the satellite object is set
		
		obsTable.setModel(obsTabModel);
		
	}
	
	/**
	 * <GS_Name , 	<Date, [Schedule_Items] >
	 * 				<Date, [Schedule_Items] > >
	 * <GS_Name , 	<Date, [Schedule_Items] >
	 * 				<Date, [Schedule_Items] > >
	 * 
	 * GS			   		  | GS				 	   |
	 * Name | AOS | LOS | MAX | Name | AOS | LOS | MAX |
	 * 
	 * 
	 * Max Row
	 * 
	 */
	private String[][] scheduleToTable() {
		// max schedule for each obs 
		int numbObs = this.schedule.size();
		int maxSch = 0;
		for(String key : this.schedule.keySet()) {
			maxSch = (maxSch < this.schedule.get(key).size()) ? this.schedule.get(key).size() : maxSch;
		}
		
		int numbCol = 4 * numbObs;
		String[][] rowCols = new String[maxSch][numbCol];
		
		// Direct access to col and row number
		int gsIdx = 0;
		for(Map.Entry<String, HashMap<Date, String[]>> element : schedule.entrySet()) {
			int r = 0;
			for (Map.Entry<Date, String[]> schItem : element.getValue().entrySet()) {
				rowCols[r][4*gsIdx+0] = schItem.getValue()[0];
				rowCols[r][4*gsIdx+1] = schItem.getKey().toString();
				rowCols[r][4*gsIdx+2] = schItem.getValue()[1];
				rowCols[r][4*gsIdx+3] = schItem.getValue()[2];
				r++;
			}
			gsIdx++;
		}
		//System.out.println(rowCols[0][2]);
		
		return rowCols;
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException
	 * @throws InvalidTleException
	 * @throws SatNotFoundException
	 * <GS_Name , 	<Date, [Schedule_Items] >
	 * 				<Date, [Schedule_Items] > >
	 * <GS_Name , 	<Date, [Schedule_Items] >
	 * 				<Date, [Schedule_Items] > >
	 * 
	 */
	private void generateSchedule() throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		// Iterate Observer
		GroundStationPosition gs;
		SatPos satPos;
		Date dt = new Date();
		schedule = new LinkedHashMap<>();
		
		// Create Data Structure for Schedule based on each observer
		// The Structure should be HashMap<Observer,HashMap<ComparableSatPass, String[]>>
		
		for (int x = 0; x < obsList.size(); x++) {
			gs = obsList.get(x);
			HashMap<Date,String[]> obsSch = new HashMap<>();
			
			// Iterate Satellite
			for (int y = 0; y < satList.size(); y++) {
				// Iterate Schedule of each satellite
				Satellite sat = satList.get(y);
				TLE tle = sat.getTLE();
				PassPredictor pp = new PassPredictor(tle,gs);
				List<SatPassTime> passes = pp.getPasses(dt, 24, false);
				for (int z = 0;z < passes.size(); z++) {
					SatPassTime spt = passes.get(z);
					SimpleDateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
					String strDate = f.format(spt.getEndTime());
					String[] val = {tle.getName(),strDate,Double.toString(spt.getMaxEl())};
					obsSch.put(spt.getStartTime(), val);
				}
				
			}
			
			schedule.put(gs.getName(), obsSch);
		}
		
		
		// Set Schedule Item
	}
	
	class ComparableSatPass extends SatPassTime implements Comparable<ComparableSatPass> {

		@Override
		public int compareTo(mainPanel.ComparableSatPass o) {
			// TODO Auto-generated method stub
			return this.getStartTime().compareTo(o.getStartTime());
		}
		
	}
	
}
