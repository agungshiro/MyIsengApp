import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SatellitePass {
	
	private static JMenu menuFile;
	private static JMenuItem openTLE, addObserver;
	private static SatelliteChooserPanel sc = new SatelliteChooserPanel();
	private static JDialog satChooseDialog, addObsDialog;
	private static JFrame frame;
	private static mainPanel mainPanel;
	private static observerPanel obsPanel = new observerPanel();
	
	/**
	 * Show GUI
	 */
	private static void showGUI() {
		
		mainPanel = new mainPanel();
		
		frame = new JFrame("Satellite Pass Alarm");
		frame.getContentPane().add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 686, 474);
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		openTLE = new JMenuItem("Open TLE");
		menuFile.add(openTLE);
		openTLE.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					showOpenFileDialog();
				} catch (IllegalArgumentException | InvalidTleException | SatNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		addObserver = new JMenuItem("Add Observer");
		menuFile.add(addObserver);
		addObserver.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					showAddObsDialog();
				} catch (IllegalArgumentException | InvalidTleException | SatNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setSize(747,545);
		frame.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

		
	}
	
	/**
	 * Open Add Observer Dialog
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	private static void showAddObsDialog() throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		if (addObsDialog == null) {
			Window win = SwingUtilities.getWindowAncestor(frame);
			//if(win != null) {
				addObsDialog = new JDialog(win,"Add Observer",
						ModalityType.APPLICATION_MODAL);
				
				addObsDialog.getContentPane().add(obsPanel);
				addObsDialog.pack();
				addObsDialog.setLocationRelativeTo(null);
			//}
		}
		addObsDialog.setPreferredSize(new Dimension(450,325));
		addObsDialog.pack();
		addObsDialog.setVisible(true);
		
		mainPanel.setNewObsObj(obsPanel.getNewObserver());
	}
	
	/**
	 * Open File Dialog to add Satellite Object by TLE file
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	private static void showOpenFileDialog() throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Text file", "pdf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("TLE file", "tle"));
 
        //fc.setAcceptAllFileFilterUsed(true);
		
		int result = fc.showOpenDialog(fc);
		if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            openSatChooseDialog(selectedFile.getAbsolutePath());
            
        }
	}
	
	/**
	 * Open Satellite Chooser Dialog Function 
	 * @throws SatNotFoundException 
	 * @throws InvalidTleException 
	 * @throws IllegalArgumentException 
	 */
	private static void openSatChooseDialog(String filePath) throws IllegalArgumentException, InvalidTleException, SatNotFoundException {
		if (satChooseDialog == null) {
			Window win = SwingUtilities.getWindowAncestor(frame);
			//if(win != null) {
				satChooseDialog = new JDialog(win,"Satellite Chooser Dialog",
						ModalityType.APPLICATION_MODAL);
				sc.setFilePath(filePath);
				sc.readFile();
				satChooseDialog.getContentPane().add(sc);
				satChooseDialog.pack();
				satChooseDialog.setLocationRelativeTo(null);
			//}
		}
		satChooseDialog.setPreferredSize(new Dimension(450,325));
		satChooseDialog.pack();
		satChooseDialog.setVisible(true);
		
		
		// this line starts *after* the modal dialog has been disposed
		mainPanel.setSelectedSatObj(sc.getSelectedMap());
		
	}
	
	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				showGUI();
			}
		});
	}

}
