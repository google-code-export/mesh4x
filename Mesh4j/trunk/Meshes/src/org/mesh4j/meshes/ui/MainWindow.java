package org.mesh4j.meshes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -1240237676349762846L;

	public MainWindow() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(new Dimension(800, 560));
		this.getContentPane().setLayout(new BorderLayout());
		this.add(new JTree(),BorderLayout.WEST);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JComponent syncNowTab = makeTextPanel("Panel #1");
		tabbedPane.addTab("Sync now", syncNowTab);
		
		JComponent schedulesTab = makeTextPanel("Panel #2");
		tabbedPane.addTab("Schedules", schedulesTab);
		
		JComponent securityTab = makeTextPanel("Panel #3");
		tabbedPane.addTab("Security", securityTab);
		
		JComponent conflictsTab = makeTextPanel("Panel #4");
		tabbedPane.addTab("Conflicts", conflictsTab);
		
		JComponent historyTab = makeTextPanel("Panel #5");
		tabbedPane.addTab("History", historyTab);
		
		JComponent smsSettingsTab = makeTextPanel("Panel #6");
		tabbedPane.addTab("SMS Settings", smsSettingsTab);

		this.add(tabbedPane,BorderLayout.CENTER);

		this.setTitle("Meshes");
		this.setResizable(false);
	}
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

}
