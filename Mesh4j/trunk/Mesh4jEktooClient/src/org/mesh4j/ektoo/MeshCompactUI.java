package org.mesh4j.ektoo;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class MeshCompactUI {

	private final static Log Logger = LogFactory.getLog(MeshCompactUI.class);
	
	// MODEL VARIABLES
	private JFrame frame;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MeshCompactUI window = new MeshCompactUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public MeshCompactUI() throws Exception {
	
		this.createUI();
		
	}


	// UI Design
	private void createUI() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("64dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("162dlu")},
			new RowSpec[] {
				RowSpec.decode("67dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("124dlu")}));

	}


}