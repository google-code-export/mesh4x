package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

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
		getContentPane().setBackground(Color.WHITE);
		setIconImage(SwingResourceManager.getImage(ConfigurationFrame.class, "/cdc.gif"));
		setResizable(false);
		setTitle(EpiInfoCompactUITranslator.getConfigurationWindowTitle());
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

		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		getContentPane().add(panel, new CellConstraints(2, 4));

		final JButton buttonClose = new JButton();
		buttonClose.setContentAreaFilled(false);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setOpaque(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(EpiInfoCompactUITranslator.getConfigurationWindowLabelClose());
		buttonClose.setToolTipText(EpiInfoCompactUITranslator.getConfigurationWindowToolTipClose());
		ActionListener closeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ConfigurationFrame.this.setVisible(false);
			}
		};
		
		buttonClose.addActionListener(closeActionListener);
		
		panel.add(buttonClose, new CellConstraints());
	}

}
