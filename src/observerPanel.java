import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.Window;
import java.awt.event.ActionEvent;

public class observerPanel extends JPanel {
	private JTextField obsName;
	private JTextField obsLat;
	private JTextField obsLong;
	private JTextField obsAlt;

	/**
	 * Create the panel.
	 */
	public observerPanel() {
		setLayout(null);
		
		JLabel obsNameLbl = new JLabel("Name");
		obsNameLbl.setBounds(6, 22, 61, 16);
		add(obsNameLbl);
		
		JLabel obsLatLbl = new JLabel("Latitude");
		obsLatLbl.setBounds(6, 63, 61, 16);
		add(obsLatLbl);
		
		JLabel obsLongLbl = new JLabel("Longitude");
		obsLongLbl.setBounds(6, 105, 89, 16);
		add(obsLongLbl);
		
		JLabel lblNewLabel = new JLabel("Altitude");
		lblNewLabel.setBounds(6, 150, 61, 16);
		add(lblNewLabel);
		
		obsName = new JTextField();
		obsName.setText("Observer");
		obsName.setBounds(98, 17, 346, 26);
		add(obsName);
		obsName.setColumns(10);
		
		obsLat = new JTextField();
		obsLat.setText("0");
		obsLat.setColumns(10);
		obsLat.setBounds(98, 58, 346, 26);
		add(obsLat);
		
		obsLong = new JTextField();
		obsLong.setText("0");
		obsLong.setColumns(10);
		obsLong.setBounds(98, 100, 346, 26);
		add(obsLong);
		
		obsAlt = new JTextField();
		obsAlt.setText("0");
		obsAlt.setColumns(10);
		obsAlt.setBounds(98, 145, 346, 26);
		add(obsAlt);
		
		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okBtnAction();
			}
		});
		okBtn.setBounds(162, 265, 117, 29);
		add(okBtn);

	}
	
	public HashMap<String,ArrayList<String>> getNewObserver() {
		HashMap<String,ArrayList<String>> obs = new HashMap<>();
		ArrayList<String> loc = new ArrayList<>();
		loc.add(obsLat.getText());
		loc.add(obsLong.getText());
		loc.add(obsAlt.getText());
		obs.put(obsName.getText(), loc);
		
		return obs;
	}
	
	/**
	 * Ok Button Action
	 */
	private void okBtnAction() {
		Window win = SwingUtilities.getWindowAncestor(this);
		if (win != null) {
			win.dispose();
		}
	}

}
