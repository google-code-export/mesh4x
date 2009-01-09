package org.mesh4j.sync.epiinfo.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class ConfigurationFrame extends JFrame {

	private static final long serialVersionUID = 1688018089793859404L;

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigurationFrame frame = new ConfigurationFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ConfigurationFrame() {
		super();
		setResizable(false);
		setTitle("Configuration");
		setBounds(100, 100, 500, 375);
		getContentPane().setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("239dlu"),
					FormFactory.RELATED_GAP_COLSPEC},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("185dlu"),
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("19dlu"),
					FormFactory.RELATED_GAP_ROWSPEC}));
	}

}
