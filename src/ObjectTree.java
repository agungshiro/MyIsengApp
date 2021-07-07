import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class ObjectTree extends JTree implements ActionListener {
	
	

	private JPopupMenu popup = new JPopupMenu();
	
	private JMenuItem menuItem = new JMenuItem("Remove");
	private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Objects");
	private DefaultMutableTreeNode observer = new DefaultMutableTreeNode("Observer");
	private DefaultMutableTreeNode satellite = new DefaultMutableTreeNode("Satellite");
	private mainPanel mp;
	
	ObjectTree() {
		super(root);
		initNodes();
	}
	
	ObjectTree(mainPanel mp){
		super(root);
		this.mp = mp;
		initNodes();
	}
	
	private void initNodes() {
		this.root.add(satellite);
		this.root.add(observer);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("remove");
		popup.add(menuItem);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}
	
	public void addSatelliteObject(DefaultMutableTreeNode sat) {
		satellite.add(sat);
		this.repaint();
	}
	
	public void addObserverObject(DefaultMutableTreeNode obs) {
		observer.add(obs);
		this.repaint();
	}
	
	

	/**
	ObjectTree(DefaultMutableTreeNode dmtn) {
		super(dmtn);
		menuItem.addActionListener(this);
		menuItem.setActionCommand("remove");
		popup.add(menuItem);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}
	**/
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode dmtn, node;
		TreePath path = this.getSelectionPath();
		dmtn = (DefaultMutableTreeNode) path.getLastPathComponent();
		
		if(e.getActionCommand().equals("remove") && dmtn.toString() != "Objects") {
			
			node = (DefaultMutableTreeNode) dmtn.getParent();
			int nodeIndex = node.getIndex(dmtn);
			if(path.getPathCount() == 1) {
				return;
			}
			if(node.getLevel() > 0) {
				
				if(node.toString() == "Satellite" ) {
					mp.removeFromSatList(dmtn.toString());
					mp.paintSatDataToTable();
					if(!mp.isObsListNull()) {
						try {
							mp.initObsTable();
							mp.setInfoBoard();
						} catch (IllegalArgumentException | InvalidTleException | SatNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
				}
				
				if(node.toString() == "Observer") {
					mp.removeFromObsList(dmtn.toString());
					if(!mp.isSatListNull()) {
						try {
							mp.initObsTable();
							mp.setInfoBoard();
						} catch (IllegalArgumentException | InvalidTleException | SatNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
					
					try {
						mp.initObsTable();
						mp.setInfoBoard();
					} catch (IllegalArgumentException | InvalidTleException | SatNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
				dmtn.removeAllChildren();
				node.remove(nodeIndex);
				((DefaultTreeModel) this.getModel()).nodeStructureChanged((TreeNode) dmtn);
			}
			
		}
		
	}
	
	
	

}
