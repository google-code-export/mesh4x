package org.mesh4j.ektoo.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mesh4j.ektoo.ui.component.RoundBorder;
import org.mesh4j.ektoo.ui.settings.CloudSettingsModel;
import org.mesh4j.ektoo.ui.settings.CloudSettingsUI;
import org.mesh4j.ektoo.ui.settings.GSSSettingsUI;
import org.mesh4j.ektoo.ui.settings.GeneralSettingsModel;
import org.mesh4j.ektoo.ui.settings.GeneralSettingsUI;
import org.mesh4j.ektoo.ui.settings.MySqlSettingsModel;
import org.mesh4j.ektoo.ui.settings.MySqlSettingsUI;
import org.mesh4j.ektoo.ui.settings.SettingsController;


public class SettingsContainer extends JPanel{

	private static final long serialVersionUID = -2277428339975245711L;
	private JPanel parentSettingsPanel;
	private SettingsController controller;
	private final static String SETTINGS_GENERAL = "General";
	private final static String SETTINGS_CLOUD = "Cloud";
	private final static String SETTINGS_MYSQL = "Mysql";
	private final static String SETTINGS_GOOGLE = "Google spreadsheet";
	private EktooFrame ektooFrame = null;
	
	public SettingsContainer(SettingsController controller,EktooFrame ektooFrame){
		this.controller = controller;
		this.ektooFrame = ektooFrame;
		this.setLayout(new BorderLayout());
		initComponents();
		controller.loadSettings();
	}

	private void initComponents(){
		this.add(createHeaderPane(),BorderLayout.NORTH);
		this.add(createTreeMenuPane(),BorderLayout.WEST);
		this.add(createSettingsComponentPane(),BorderLayout.CENTER);
		this.add(createFooterPane(),BorderLayout.SOUTH);
	}
	
	
	private JPanel createHeaderPane(){
		JPanel headerPanel = new JPanel();
		headerPanel.setPreferredSize(new Dimension(400,30));
		headerPanel.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY)));
		return headerPanel;
	}
	
	private JPanel createTreeMenuPane(){
		JPanel treeItemPanel = new JPanel(new BorderLayout());
		treeItemPanel.setPreferredSize(new Dimension(150,100));
		treeItemPanel.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY)));
		JScrollPane pane = new JScrollPane(createSettingsTree());
		treeItemPanel.add(pane,BorderLayout.CENTER);
		return treeItemPanel;
	}
	
	private JTree createSettingsTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Settings");
		DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode(SETTINGS_GENERAL);
	    root.add(generalNode);
	    
	    DefaultMutableTreeNode dataSourceNode = new DefaultMutableTreeNode("Data source");
	    root.add(dataSourceNode);
	    
	    DefaultMutableTreeNode cloudNode = new DefaultMutableTreeNode(SETTINGS_CLOUD);
	    dataSourceNode.add(cloudNode);
	    
	    
	    DefaultMutableTreeNode gssNode = new DefaultMutableTreeNode(SETTINGS_GOOGLE);
	    dataSourceNode.add(gssNode);
	    DefaultMutableTreeNode mysqlNode = new DefaultMutableTreeNode(SETTINGS_MYSQL);
	    dataSourceNode.add(mysqlNode);
    
        final JTree settingsTree = new JTree(root);
    	settingsTree.addTreeSelectionListener(new TreeSelectionListener(){
    		@Override
    		public void valueChanged(TreeSelectionEvent e) {
				  Object o = settingsTree.getLastSelectedPathComponent();
			      DefaultMutableTreeNode show = (DefaultMutableTreeNode) o;
			      String title = (String) show.getUserObject();
			      updateSettingsPane(title);
		}}
    	);
    	settingsTree.setRootVisible(false);
	return settingsTree;
	}
	
	private void updateSettingsPane(String paneName){
		CardLayout cl = (CardLayout) (parentSettingsPanel.getLayout());
		cl.show(parentSettingsPanel, paneName);
	}
	
	private JPanel createSettingsComponentPane(){
		parentSettingsPanel = new JPanel(new CardLayout());
		
		GeneralSettingsUI generalSettingsUI = new GeneralSettingsUI(controller);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(generalSettingsUI);
		
		controller.addModel(new GeneralSettingsModel());
		controller.addView(generalSettingsUI);
		
		GSSSettingsUI gssSettingsUI = new GSSSettingsUI(controller);
		CloudSettingsUI cloudSettingsUI = new CloudSettingsUI(controller);
		controller.addModel(new CloudSettingsModel());
		controller.addView(cloudSettingsUI);
		
		MySqlSettingsUI mysqlSettingsUI = new MySqlSettingsUI(controller);
		controller.addModel(new MySqlSettingsModel());
		controller.addView(mysqlSettingsUI);
		
		parentSettingsPanel.add(scrollPane,SETTINGS_GENERAL);
		parentSettingsPanel.add(gssSettingsUI,SETTINGS_GOOGLE);
		parentSettingsPanel.add(cloudSettingsUI,SETTINGS_CLOUD);
		parentSettingsPanel.add(mysqlSettingsUI,SETTINGS_MYSQL);
		
		return parentSettingsPanel;
	}
	
	
	
	
	private JPanel createFooterPane(){
		JPanel headerPanel = new JPanel(new BorderLayout(10,5));
		headerPanel.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY)));
		headerPanel.add(getButtonPanel(),BorderLayout.LINE_END);
		return headerPanel;
	}
	
	private JPanel getButtonPanel(){
		JPanel headerPanel = new JPanel(new GridLayout(0,2,10,0));
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsContainer.this.controller.save();
				close();
		}});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}});
		
		headerPanel.add(okButton);
		headerPanel.add(cancelButton);
		return headerPanel;
	}
	
	private void close(){
		ektooFrame.closePopupViewWindow();
	}
}
