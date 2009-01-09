package org.mesh4j.sync.epiinfo.ui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class LogFrame extends JFrame {
	
	private static final long serialVersionUID = -5672081373978129329L;
	
	private final static Log Logger = LogFactory.getLog(LogFrame.class);

	// MODEL VARIABLES
	private JTextArea textAreaConsoleView;
	
	// BUSINESS METHODS

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogFrame frame = new LogFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LogFrame() {
		super();
		setTitle("Log Windows");
		setResizable(false);
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

		final JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textAreaConsoleView = new JTextArea();
		scrollPane.setViewportView(textAreaConsoleView);

		final JButton buttonClean = new JButton();
		buttonClean.setText("Clean");
		getContentPane().add(buttonClean, new CellConstraints(2, 4));
	}

	public JTextArea getTextAreaConsoleView() {
		return textAreaConsoleView;
	}

	public void cleanLog() {
		this.textAreaConsoleView.setText("");
	}
	
	public void log(String text) {
		this.textAreaConsoleView.setText(text + "\n"+ this.textAreaConsoleView.getText());
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
