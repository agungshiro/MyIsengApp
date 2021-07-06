import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Object");
	private DefaultMutableTreeNode observer = new DefaultMutableTreeNode("Observer");
	private DefaultMutableTreeNode satellite = new DefaultMutableTreeNode("Satellite");
	
	ObjectTree() {
		super(root);
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
		
		if(e.getActionCommand().equals("remove")) {
			node = (DefaultMutableTreeNode) dmtn.getParent();
			System.out.println(path.getPathCount());
			int nodeIndex = node.getIndex(dmtn);
			if(path.getPathCount() == 1) {
				return;
			}
			if(node.getLevel() > 0) {
				dmtn.removeAllChildren();
				node.remove(nodeIndex);
				((DefaultTreeModel) this.getModel()).nodeStructureChanged((TreeNode) dmtn);
			}
			
		}
		
	}
	
	
	

}
