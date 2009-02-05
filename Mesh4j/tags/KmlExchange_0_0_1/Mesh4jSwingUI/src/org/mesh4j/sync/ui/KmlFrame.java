package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.ui.tasks.CleanKMLTask;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.tasks.PrepareKMLToSyncTask;
import org.mesh4j.sync.ui.tasks.PurgeKMLTask;
import org.mesh4j.sync.ui.translator.KmlUITranslator;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class KmlFrame extends JFrame implements IErrorListener {
	
	private static final long serialVersionUID = -4012111410850219031L;

	public final static Log Logger = LogFactory.getLog(KmlFrame.class);

	// MODEL VARIABLES
	private KmlUI kmlUI;
	private JButton buttonPrepareFileToSync;
	private JButton buttonOpenFileKml;
	private JButton buttonCleanSyncInfo;
	private JButton buttonPurgeKml;
	private JTextField textFieldKmlFile;
	private JLabel imageStatus;
	private JTextArea textAreaStatus; 
	
	// BUSINESS METHODS

	public KmlFrame(KmlUI kmlUI) throws IOException {
		super();
		
		this.kmlUI = kmlUI;
		
		setIconImage(IconManager.getInsteddImage());
		getContentPane().setBackground(Color.WHITE);
		setTitle(KmlUITranslator.getKmlManagerWindowTitle());
		setResizable(false);
		setBounds(100, 100, 619, 204);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("14dlu"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("232dlu"),
				ColumnSpec.decode("11dlu"),
				ColumnSpec.decode("19dlu"),
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				RowSpec.decode("14dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("27dlu"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("14dlu")}));

		final JLabel labelKmlFile = DefaultComponentFactory.getInstance().createLabel(KmlUITranslator.getLabelKMLFile());
		labelKmlFile.setFont(new Font("Calibri", Font.PLAIN, 12));
		getContentPane().add(labelKmlFile, new CellConstraints(2, 2, CellConstraints.FILL, CellConstraints.FILL));

		textFieldKmlFile = new JTextField();
		textFieldKmlFile.setEditable(false);
		textFieldKmlFile.setText(this.kmlUI.getPropertiesProvider().getDefaultEnpoint1());
		textFieldKmlFile.setToolTipText(KmlUITranslator.getToolTipKMLFile());
		getContentPane().add(textFieldKmlFile, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener fileChooserKmlFileActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = FileChooser.openFileDialogKML(KmlFrame.this, textFieldKmlFile.getText());
				if(selectedFileName != null){
					textFieldKmlFile.setText(selectedFileName);
				}
			}
		};
		buttonOpenFileKml = new JButton();
		buttonOpenFileKml.setContentAreaFilled(false);
		buttonOpenFileKml.setBorderPainted(false);
		buttonOpenFileKml.setActionCommand("");
		buttonOpenFileKml.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonOpenFileKml.setBackground(Color.WHITE);
		buttonOpenFileKml.setToolTipText(KmlUITranslator.getToolTipFileChooser());
		buttonOpenFileKml.setText(KmlUITranslator.getLabelFileChooser());
		buttonOpenFileKml.addActionListener(fileChooserKmlFileActionListener);
		getContentPane().add(buttonOpenFileKml, new CellConstraints(5, 2, CellConstraints.LEFT, CellConstraints.FILL));

		final JPanel panelKmlButtons = new JPanel();
		panelKmlButtons.setBackground(Color.WHITE);
		panelKmlButtons.setLayout(new FormLayout(new ColumnSpec[] {FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC}, new RowSpec[] {FormFactory.DEFAULT_ROWSPEC}));
		getContentPane().add(panelKmlButtons, new CellConstraints(2, 4, 4, 1));

		ActionListener prepareFileToSyncActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				PrepareKMLToSyncTask task = new PrepareKMLToSyncTask(KmlFrame.this, getFileName());
				task.execute();
			}
		};
		buttonPrepareFileToSync = new JButton();
		buttonPrepareFileToSync.setFont( new Font("Calibri", Font.PLAIN, 10));
		buttonPrepareFileToSync.setOpaque(false);
		buttonPrepareFileToSync.setContentAreaFilled(false);
		buttonPrepareFileToSync.setBorderPainted(false);
		buttonPrepareFileToSync.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonPrepareFileToSync.setToolTipText(KmlUITranslator.getToolTipPrepareToSync());
		buttonPrepareFileToSync.setText(KmlUITranslator.getLabelPrepareToSync());
		buttonPrepareFileToSync.addActionListener(prepareFileToSyncActionListener);
		panelKmlButtons.add(buttonPrepareFileToSync, new CellConstraints());

		ActionListener cleanSyncInfoActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				CleanKMLTask task = new CleanKMLTask(KmlFrame.this, getFileName());
				task.execute();
			}
		};
		buttonCleanSyncInfo = new JButton();
		buttonCleanSyncInfo.setFont( new Font("Calibri", Font.PLAIN, 10));
		buttonCleanSyncInfo.setOpaque(false);
		buttonCleanSyncInfo.setContentAreaFilled(false);
		buttonCleanSyncInfo.setBorderPainted(false);
		buttonCleanSyncInfo.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonCleanSyncInfo.setToolTipText(KmlUITranslator.getToolTipCleanSyncInfo());
		buttonCleanSyncInfo.setText(KmlUITranslator.getLabelClean());
		buttonCleanSyncInfo.addActionListener(cleanSyncInfoActionListener);
		panelKmlButtons.add(buttonCleanSyncInfo, new CellConstraints(3, 1));

		ActionListener purgeKmlActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				PurgeKMLTask task = new PurgeKMLTask(KmlFrame.this, getFileName());
				task.execute();
			}
		};
		buttonPurgeKml = new JButton();
		buttonPurgeKml.setFont( new Font("Calibri", Font.PLAIN, 10));
		buttonPurgeKml.setOpaque(false);
		buttonPurgeKml.setContentAreaFilled(false);
		buttonPurgeKml.setBorderPainted(false);
		buttonPurgeKml.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonPurgeKml.setToolTipText(KmlUITranslator.getToolTipPurge());
		buttonPurgeKml.setText(KmlUITranslator.getLabelPurgue());
		buttonPurgeKml.addActionListener(purgeKmlActionListener);
		panelKmlButtons.add(buttonPurgeKml, new CellConstraints(5, 1));

		ActionListener openEndpoint1ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OpenFileTask task = new OpenFileTask(KmlFrame.this, KmlFrame.this, textFieldKmlFile.getText());
				task.execute();
			}
		};
			
		final JButton buttonOpenKml = new JButton();
		buttonOpenKml.setContentAreaFilled(false);
		buttonOpenKml.setBorderPainted(false);
		buttonOpenKml.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenKml.setBackground(Color.WHITE);
		buttonOpenKml.setText("");
		buttonOpenKml.setToolTipText(KmlUITranslator.getTooltipView());
		buttonOpenKml.setIcon(IconManager.getMapIcon());
		buttonOpenKml.addActionListener(openEndpoint1ActionListener);
		getContentPane().add(buttonOpenKml, new CellConstraints(6, 2));

		final JPanel panelStatus = new JPanel();
		panelStatus.setBackground(Color.WHITE);
		panelStatus.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("230dlu"),
				ColumnSpec.decode("17dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));
		getContentPane().add(panelStatus, new CellConstraints(2, 3, 5, 1, CellConstraints.FILL, CellConstraints.BOTTOM));

		textAreaStatus = new JTextArea();
		textAreaStatus.setFont(new Font("Calibri", Font.BOLD, 12));
		textAreaStatus.setLineWrap(true);
		textAreaStatus.setWrapStyleWord(true);
		textAreaStatus.setOpaque(true);
		textAreaStatus.setEditable(false);
		textAreaStatus.setText(KmlUITranslator.getMessageWelcome(LoggedInIdentityProvider.getUserName()));
		panelStatus.add(textAreaStatus, new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.BOTTOM));

		imageStatus = new JLabel();
		imageStatus.setIcon(IconManager.getStatusReadyIcon());
		imageStatus.setText("");
		panelStatus.add(imageStatus, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));
		
	}
	
	public String getFileName(){
		return this.textFieldKmlFile.getText();
	}

	public KmlUI getKmlUI() {
		return this.kmlUI;
	}
	
	public void setStatusOk(String text) {
		this.imageStatus.setIcon(IconManager.getStatusOkIcon());
		this.imageStatus.setToolTipText(text);
		this.textAreaStatus.setText(text);
	}

	public void setStatusError(String text) {
		this.imageStatus.setIcon(IconManager.getStatusErrorIcon());
		this.imageStatus.setToolTipText(text);
		this.textAreaStatus.setText(text);		
	}


	public void setStatusInProcess(String text) {
		this.imageStatus.setIcon(IconManager.getStatusInProgressIcon());
		this.imageStatus.setToolTipText(text);
		this.textAreaStatus.setText(text);		
	}

	@Override
	public void notifyError(String error) {
		setStatusError(error);		
	}
}
