import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;
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
import java.awt.Color;
import java.awt.Font;

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
	
	private LinkedHashMap<String,TreeMap<Date,String[]>> schedule;
	private TreeMap<Date,String[]> scheduleStack;
	
	private JLabel infoSatName;
	private JLabel infoObsName;
	private JLabel infoPassTime;
	private JLabel latterPass1;
	private JLabel latterPass2;
	private JLabel latterPass3;
	private JLabel countDown;
	private SimpleDateFormat counterFormat = new SimpleDateFormat("HH:mm:ss");
	
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
		objectTree.setBounds(17, 42, 139, 470);
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
		obsTabScroll.setBounds(168, 281, 534, 231);
		add(obsTabScroll);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setForeground(new Color(0, 100, 0));
		infoPanel.setBorder(null);
		infoPanel.setBackground(new Color(175, 238, 238));
		infoPanel.setBounds(168, 170, 534, 99);
		add(infoPanel);
		infoPanel.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("Next Pass");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 10));
		lblNewLabel_1.setForeground(new Color(47, 79, 79));
		lblNewLabel_1.setBounds(6, 6, 103, 16);
		infoPanel.add(lblNewLabel_1);
		
		infoSatName = new JLabel("Satellite Name");
		infoSatName.setFont(new Font("Lucida Grande", Font.PLAIN, 27));
		infoSatName.setForeground(new Color(178, 34, 34));
		infoSatName.setBounds(69, 6, 194, 28);
		infoPanel.add(infoSatName);
		
		infoObsName = new JLabel("Ground Station");
		infoObsName.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		infoObsName.setForeground(new Color(47, 79, 79));
		infoObsName.setBounds(69, 34, 194, 28);
		infoPanel.add(infoObsName);
		
		infoPassTime = new JLabel("on 00:00:00 |");
		infoPassTime.setBounds(69, 64, 90, 16);
		infoPanel.add(infoPassTime);
		
		JLabel lblNewLabel_1_1 = new JLabel("Latter");
		lblNewLabel_1_1.setForeground(new Color(47, 79, 79));
		lblNewLabel_1_1.setFont(new Font("Lucida Grande", Font.BOLD, 10));
		lblNewLabel_1_1.setBounds(298, 6, 103, 16);
		infoPanel.add(lblNewLabel_1_1);
		
		latterPass1 = new JLabel("00:00:00 Satellite Observer");
		latterPass1.setForeground(new Color(47, 79, 79));
		latterPass1.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		latterPass1.setBounds(298, 31, 230, 16);
		infoPanel.add(latterPass1);
		
		latterPass2 = new JLabel("00:00:00 Satellite Observer");
		latterPass2.setForeground(new Color(47, 79, 79));
		latterPass2.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		latterPass2.setBounds(298, 46, 230, 16);
		infoPanel.add(latterPass2);
		
		latterPass3 = new JLabel("00:00:00 Satellite Observer");
		latterPass3.setForeground(new Color(47, 79, 79));
		latterPass3.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		latterPass3.setBounds(298, 59, 230, 16);
		infoPanel.add(latterPass3);
		
		countDown = new JLabel("00:00:00");
		countDown.setForeground(new Color(47, 79, 79));
		countDown.setBounds(159, 64, 90, 16);
		infoPanel.add(countDown);
		
		// Time Function
		globalTime = new Timer(1000,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timeString();
				if (satList.size() > 0) {
					paintSatDataToTable();
				}
				if (scheduleStack != null && scheduleStack.size() > 0) {
					try {
						countDown.setText(countDownMaker());
						if(countDownMaker().equals("0:0:0")) {
							initObsTable();
							setInfoBoard();
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvalidTleException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SatNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
	
	private String countDownMaker() throws ParseException {
		String passTime = this.infoPassTime.getText();
		String curTime = this.timeField.getText();
		Date t1 = this.counterFormat.parse(passTime);
		Date t2 = this.counterFormat.parse(curTime);
		long diff = t1.getTime()-t2.getTime();
		
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		
		String out = Long.toString(diffHours)+":"+Long.toString(diffMinutes)+":"+Long.toString(diffSeconds);
		return out;
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
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	public void setSelectedSatObj(HashMap<String,ArrayList<String>> map) throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
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
		// Paint schedule table
		if (this.obsList != null && this.obsList.size() > 0) {
			this.initObsTable();
			this.setInfoBoard();
		}
		
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
		
		// Paint schedule table
		if (this.satList != null && this.satList.size() > 0) {
			this.initObsTable();
			this.setInfoBoard();
		}
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
		Calendar cal = Calendar.getInstance(/**TimeZone.getTimeZone("GMT")**/);
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
	
	private void setInfoBoard() {
		int idx = 0;
		for(Map.Entry<Date, String[]> entry : this.scheduleStack.entrySet()) {
			String[] time = entry.getKey().toString().split(" ",0);
			String[] val = entry.getValue();
			if(idx == 0) {
				this.infoSatName.setText(val[0]);
				this.infoObsName.setText(val[3]);
				this.infoPassTime.setText(time[3]);
			} else if (idx == 1) {
				this.latterPass1.setText(time[3]+" "+val[0]+" "+val[3]);
			} else if (idx == 2) {
				this.latterPass2.setText(time[3]+" "+val[0]+" "+val[3]);
			} else if (idx == 3) {
				this.latterPass3.setText(time[3]+" "+val[0]+" "+val[3]);
			} else {
				break;
			}
			idx++;
		}
	}
	
	/**
	 * Initiate Observer table header
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	private void initObsTable() throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		DefaultTableModel obsTabModel = new DefaultTableModel();
		ArrayList<String> colSeed = new ArrayList<String>(Arrays.asList("Day","Sats ","AOS","LOS","Max El"));
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
		
		int numbCol = 5 * numbObs;
		String[][] rowCols = new String[maxSch][numbCol];
		
		// Direct access to col and row number
		int gsIdx = 0;
		for(Map.Entry<String, TreeMap<Date, String[]>> element : schedule.entrySet()) {
			int r = 0;
			for (Map.Entry<Date, String[]> schItem : element.getValue().entrySet()) {
				String[] aos =  schItem.getKey().toString().split(" ",0);
				String[] los =  schItem.getValue()[1].split(" ",0);
				rowCols[r][5*gsIdx+0] = aos[0]+" "+aos[1]+" "+aos[2]; // Date
				rowCols[r][5*gsIdx+1] = schItem.getValue()[0]; // Sat
				rowCols[r][5*gsIdx+2] = aos[3]; // AOS
				rowCols[r][5*gsIdx+3] = los[3]; // LOS
				rowCols[r][5*gsIdx+4] = schItem.getValue()[2].substring(0,4); // MaxEl
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
		this.scheduleStack = new TreeMap<>();
		
		// Create Data Structure for Schedule based on each observer
		// The Structure should be HashMap<Observer,HashMap<ComparableSatPass, String[]>>
		
		for (int x = 0; x < obsList.size(); x++) {
			gs = obsList.get(x);
			TreeMap<Date,String[]> obsSch = new TreeMap<>();
			
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
					String[] val = {tle.getName(),strDate,Double.toString(spt.getMaxEl()),gs.getName()};
					obsSch.put(spt.getStartTime(), val);
					this.scheduleStack.put(spt.getStartTime(), val);
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
