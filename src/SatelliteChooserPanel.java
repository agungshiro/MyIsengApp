import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.Window;
import java.awt.event.ActionEvent;

public class SatelliteChooserPanel extends JPanel {
	private JTextField filePath;
	private String path;
	//private DefaultListModel<Set<String>> listModel = new DefaultListModel<Set<String>>();
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private HashMap<String,ArrayList<String>> TLEMap, selectedMap;
	
	private JButton okButton;
	private JList<String> satList;

	/**
	 * Create the panel.
	 */
	public SatelliteChooserPanel() {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("File");
		lblNewLabel.setBounds(6, 11, 34, 16);
		add(lblNewLabel);
		
		filePath = new JTextField();
		filePath.setEnabled(false);
		filePath.setEditable(false);
		filePath.setBounds(49, 6, 395, 26);
		add(filePath);
		filePath.setColumns(10);
		
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okBtnAction();
			}
		});
		okButton.setBounds(333, 262, 117, 29);
		add(okButton);
		
		satList = new JList<>(listModel);
		satList.setBounds(16, 39, 417, 208);
		add(satList);
		
		JScrollPane scrollPane = new JScrollPane(satList);
		scrollPane.setBounds(16, 46, 417, 204);
		add(scrollPane);
	}
	
	/**
	 * Ok Button Action
	 */
	private void okBtnAction() {
		
		selectedMap = new HashMap<>();
		String key;
		// Move Selected
		List nameList = satList.getSelectedValuesList();
		for(int i = 0; i < nameList.size();i++) {
			
			key = (String) nameList.get(i);
			selectedMap.put(key, TLEMap.get(key));
		}
		
		Window win = SwingUtilities.getWindowAncestor(this);
		if (win != null) {
			win.dispose();
		}
	}
	
	/**
	 * Get Selected
	 */
	public HashMap<String, ArrayList<String>> getSelectedMap() {
		//System.out.println("trigered");
		return this.selectedMap;
	}
	
	/**
	 * Set Path
	 * @param textPath
	 */
	public void setFilePath(String textPath) {
		this.path = textPath;
		this.filePath.setText(textPath);
	}
	
	/**
	 * Read TLE.txt
	 */
	public void readFile() {
		TLEMap = new HashMap<>();
		
		try {
			File f = new File(this.path);
			Scanner myReader = new Scanner(f);
			int idx  = 0;
			String key = "";
			ArrayList<String> val = new ArrayList<>();
			while(myReader.hasNextLine()) {
				String data = myReader.nextLine();
				//System.out.println(data);
				if(idx == 0) {
					// Put Satellite name to the JList
					listModel.addElement(data);
					// set key for TLE Map
					key = data;
				} else {
					// set value for TLE Map
					val.add(data);
				}
				
				idx++;
				
				// Reset
				if(idx > 2) {
					TLEMap.put(key, val);
					idx = 0;
					val = new ArrayList<>();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
