package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ui.translator.KmlUITranslator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LogFrame extends JFrame {
	
	private static final long serialVersionUID = -4012111410850219031L;

	public final static Log Logger = LogFactory.getLog(LogFrame.class);

	// MODEL VARIABLES
	private JTextArea textAreaConsoleView;
	
	// BUSINESS METHODS

	public LogFrame() {
		super();
		
		setIconImage(IconManager.getInsteddImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(KmlUITranslator.getLogWindowTitle());
		setResizable(false);
		setBounds(100, 100, 867, 376);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("423dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("185dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textAreaConsoleView = new JTextArea();
		textAreaConsoleView.setFont(new Font("Calibri", Font.PLAIN, 12));
		textAreaConsoleView.setBorder(new BevelBorder(BevelBorder.LOWERED));
		textAreaConsoleView.setToolTipText(KmlUITranslator.getLogWindowToolTipConsoleView());
		scrollPane.setViewportView(textAreaConsoleView);
		
		ActionListener cleanActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cleanLog();
			}
		};	

		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("17dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("12dlu")}));
		getContentPane().add(panel, new CellConstraints(2, 4));

		final JButton buttonClean = new JButton();
		buttonClean.setOpaque(true);
		buttonClean.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClean.setContentAreaFilled(false);
		buttonClean.setBorderPainted(false);
		buttonClean.setFont(new Font("Calibri", Font.BOLD, 12));
		panel.add(buttonClean, new CellConstraints(1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT));
		buttonClean.setText(KmlUITranslator.getLogWindowLabelClean());
		buttonClean.setToolTipText(KmlUITranslator.getLogWindowToolTipClean());
		buttonClean.addActionListener(cleanActionListener);

		final JButton buttonClose = new JButton();
		buttonClose.setOpaque(true);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setContentAreaFilled(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(KmlUITranslator.getLogWindowLabelClose());
		buttonClose.setToolTipText(KmlUITranslator.getLogWindowToolTipClose());
		
		ActionListener closeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LogFrame.this.setVisible(false);
			}
		};
		
		buttonClose.addActionListener(closeActionListener);
		
		panel.add(buttonClose, new CellConstraints(3, 1));
	}

	public JTextArea getTextAreaConsoleView() {
		return textAreaConsoleView;
	}

	public void cleanLog() {
		this.textAreaConsoleView.setText("");
	}
	
	public void log(String text) {
		log(text, true);
	}
	
	public void log(String text, boolean mustUseConsole) {
		if(mustUseConsole){
			this.textAreaConsoleView.setText(text + "\n"+ this.textAreaConsoleView.getText());
		}
		
		if(Logger.isInfoEnabled()){
			Logger.info(text);
		}
	}

	public void logError(Throwable t, String errorMessage) {
		this.textAreaConsoleView.setText(errorMessage + "\n"+ this.textAreaConsoleView.getText());
		Logger.error(t.getMessage(), t);
	}
	
	public void logAppendEndLine(String text) {
		String line, allWithOutLine;
		
		String consoleText = this.textAreaConsoleView.getText();
		int lfIndex = consoleText.indexOf("\n");
		if(lfIndex == -1){
			line = consoleText;
			allWithOutLine = "";
		} else {
			line = consoleText.substring(0, lfIndex);
			allWithOutLine = consoleText.substring(lfIndex, consoleText.length());
		}
		
		this.textAreaConsoleView.setText(line+ text + allWithOutLine);
		if(Logger.isInfoEnabled()){
			Logger.info(text);
		}
	}
}
