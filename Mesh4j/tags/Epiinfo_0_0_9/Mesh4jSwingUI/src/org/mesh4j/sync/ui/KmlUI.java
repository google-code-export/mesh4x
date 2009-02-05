package org.mesh4j.sync.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.ui.tasks.IErrorListener;
import org.mesh4j.sync.ui.tasks.OpenFileTask;
import org.mesh4j.sync.ui.tasks.OpenURLTask;
import org.mesh4j.sync.ui.tasks.SynchronizationTask;
import org.mesh4j.sync.ui.translator.KmlUITranslator;
import org.mesh4j.sync.utils.SyncAdapterFactory;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class KmlUI implements IErrorListener {

	// MODEL VARIABLES
	private JLabel labelEndpoint1;
	private JButton buttonSynchronize;
	private JLabel labelEndpoint2;
	private JTextField textFieldEndpoint1;
	private JButton buttonOpenFileEndpoint2;
	private JTextField textFieldEndpoint2;
	private JButton buttonOpenFileEndpoint1;
	private JFrame frame;
	private JLabel imageStatus;
	private JTextArea textAreaStatus; 
	private JButton buttonOpenEndpoint1;
	private JButton buttonOpenEndpoint2;
	private JButton buttonKmlManager;
	
	private LogFrame logFrame;
	private KmlFrame kmlFrame;
	
	private PropertiesProvider propertiesProvider;
	private SyncAdapterFactory syncAdapterFactory;
	
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KmlUI window = new KmlUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					LogFrame.Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public KmlUI() throws IOException {
		this.propertiesProvider = new PropertiesProvider();
		this.syncAdapterFactory = new SyncAdapterFactory(this.propertiesProvider);
		this.logFrame = new LogFrame();
		this.kmlFrame = new KmlFrame(this);
		createUI();
	}

	private void createUI() throws IOException {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setIconImage(IconManager.getInsteddImage());
		frame.setBackground(Color.WHITE);
		frame.getContentPane().setLayout(new FormLayout(
			"14dlu, 291dlu",
			"12dlu, 49dlu, 32dlu, 32dlu"));
		frame.setResizable(false);
		frame.setTitle(KmlUITranslator.getTitle());
		frame.setBounds(100, 100, 616, 228);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				KmlUI.this.close();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		
		final JPanel panelSync = new JPanel();
		panelSync.setBackground(Color.WHITE);
		panelSync.setFocusCycleRoot(true);
		panelSync.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("198dlu"),
				ColumnSpec.decode("11dlu"),
				ColumnSpec.decode("6dlu"),
				ColumnSpec.decode("24dlu"),
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("6dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelSync, new CellConstraints(2, 2));

		textFieldEndpoint1 = new JTextField();
		textFieldEndpoint1.setFont(new Font("Calibri", Font.PLAIN, 12));
		textFieldEndpoint1.setToolTipText(KmlUITranslator.getToolTipFile());
		panelSync.add(textFieldEndpoint1, new CellConstraints(3, 1, CellConstraints.FILL, CellConstraints.FILL));
		textFieldEndpoint1.setText(this.propertiesProvider.getDefaultEnpoint1());
		textFieldEndpoint1.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				// NOTHING TO DO
			}

			@Override
			public void focusLost(FocusEvent e) {
				buttonOpenEndpoint1.setIcon(IconManager.getViewIcon(syncAdapterFactory, getEndpoint1()));
			}
			
		});
		
		textFieldEndpoint2 = new JTextField();
		textFieldEndpoint2.setFont(new Font("Calibri", Font.PLAIN, 12));
		textFieldEndpoint2.setToolTipText(KmlUITranslator.getToolTipFile());
		textFieldEndpoint2.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				// NOTHING TO DO
			}

			@Override
			public void focusLost(FocusEvent e) {
				buttonOpenEndpoint2.setIcon(IconManager.getViewIcon(syncAdapterFactory, getEndpoint2()));
			}
			
		});
		
		
		panelSync.add(textFieldEndpoint2, new CellConstraints(3, 3, CellConstraints.FILL, CellConstraints.FILL));
		textFieldEndpoint2.setText(this.propertiesProvider.getDefaultEnpoint2());
		
		ActionListener fileChooserEndpoint1ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = FileChooser.openFileDialogKML(KmlUI.this.frame, getEndpoint1());
				if(selectedFileName != null){
					textFieldEndpoint1.setText(selectedFileName);
				}
			}
		};		
		buttonOpenFileEndpoint1 = new JButton();
		buttonOpenFileEndpoint1.setContentAreaFilled(false);
		buttonOpenFileEndpoint1.setBorderPainted(false);
		buttonOpenFileEndpoint1.setActionCommand("");
		buttonOpenFileEndpoint1.setBackground(Color.WHITE);
		buttonOpenFileEndpoint1.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonOpenFileEndpoint1.setToolTipText(KmlUITranslator.getToolTipFileChooser());
		buttonOpenFileEndpoint1.setText(KmlUITranslator.getLabelFileChooser());
		panelSync.add(buttonOpenFileEndpoint1, new CellConstraints(4, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
		buttonOpenFileEndpoint1.addActionListener(fileChooserEndpoint1ActionListener);

		
		ActionListener fileChooserEndpoint2ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String selectedFileName = FileChooser.openFileDialogKML(KmlUI.this.frame, getEndpoint2());
				if(selectedFileName != null){
					textFieldEndpoint2.setText(selectedFileName);
				}
			}
		};
		buttonOpenFileEndpoint2 = new JButton();
		buttonOpenFileEndpoint2.setContentAreaFilled(false);
		buttonOpenFileEndpoint2.setBorderPainted(false);
		buttonOpenFileEndpoint2.setActionCommand("");
		buttonOpenFileEndpoint2.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonOpenFileEndpoint2.setBackground(Color.WHITE);
		buttonOpenFileEndpoint2.setToolTipText(KmlUITranslator.getToolTipFileChooser());
		buttonOpenFileEndpoint2.setText(KmlUITranslator.getLabelFileChooser());
		panelSync.add(buttonOpenFileEndpoint2, new CellConstraints(4, 3, CellConstraints.LEFT, CellConstraints.BOTTOM));
		buttonOpenFileEndpoint2.addActionListener(fileChooserEndpoint2ActionListener);

		labelEndpoint1 = DefaultComponentFactory.getInstance().createLabel(KmlUITranslator.getLabelEndpoint1());
		labelEndpoint1.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelSync.add(labelEndpoint1, new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));

		labelEndpoint2 = DefaultComponentFactory.getInstance().createLabel(KmlUITranslator.getLabelEndpoint2());
		labelEndpoint2.setFont(new Font("Calibri", Font.PLAIN, 12));
		panelSync.add(labelEndpoint2, new CellConstraints(1, 3, CellConstraints.FILL, CellConstraints.FILL));

		ActionListener openEndpoint1ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String endpoint1 = getEndpoint1();
				if(syncAdapterFactory.isHTTP(endpoint1)){
					OpenURLTask task = new OpenURLTask(KmlUI.this.getFrame(), KmlUI.this, endpoint1);
					task.execute();
				} else if(syncAdapterFactory.isKml(endpoint1)) {
					OpenFileTask task = new OpenFileTask(KmlUI.this.getFrame(), KmlUI.this, endpoint1);
					task.execute();
				}
			}
		};
		
		buttonOpenEndpoint1 = new JButton();
		buttonOpenEndpoint1.setContentAreaFilled(false);
		buttonOpenEndpoint1.setBorderPainted(false);
		buttonOpenEndpoint1.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenEndpoint1.setBackground(Color.WHITE);
		buttonOpenEndpoint1.setText("");
		buttonOpenEndpoint1.setToolTipText(KmlUITranslator.getTooltipView());
		buttonOpenEndpoint1.setIcon(IconManager.getViewIcon(this.syncAdapterFactory, getEndpoint1()));
		buttonOpenEndpoint1.addActionListener(openEndpoint1ActionListener);

		panelSync.add(buttonOpenEndpoint1, new CellConstraints(6, 1));

		ActionListener openEndpoint2ActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String endpoint2 = getEndpoint2();
				if(syncAdapterFactory.isHTTP(endpoint2)){
					OpenURLTask task = new OpenURLTask(KmlUI.this.getFrame(), KmlUI.this, endpoint2);
					task.execute();
				} else if(syncAdapterFactory.isKml(endpoint2)) {
					OpenFileTask task = new OpenFileTask(KmlUI.this.getFrame(), KmlUI.this, endpoint2);
					task.execute();
				}
			}
		};
		
		buttonOpenEndpoint2 = new JButton();
		buttonOpenEndpoint2.setContentAreaFilled(false);
		buttonOpenEndpoint2.setBorderPainted(false);
		buttonOpenEndpoint2.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenEndpoint2.setBackground(Color.WHITE);
		buttonOpenEndpoint2.setText("");
		buttonOpenEndpoint2.setToolTipText(KmlUITranslator.getTooltipView());
		buttonOpenEndpoint2.setIcon(IconManager.getViewIcon(this.syncAdapterFactory, getEndpoint2()));
		buttonOpenEndpoint2.addActionListener(openEndpoint2ActionListener);
		panelSync.add(buttonOpenEndpoint2, new CellConstraints(6, 3));

		final JPanel panelStatus = new JPanel();
		panelStatus.setBackground(Color.WHITE);
		panelStatus.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("211dlu"),
				ColumnSpec.decode("17dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("37dlu"),
				ColumnSpec.decode("14dlu")},
			new RowSpec[] {
				RowSpec.decode("19dlu"),
				RowSpec.decode("4dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelStatus, new CellConstraints(2, 3));

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

		final JPanel panelStatusButtons = new JPanel();
		panelStatusButtons.setBackground(Color.WHITE);
		panelStatusButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC}));
		panelStatus.add(panelStatusButtons, new CellConstraints(1, 3, 2, 1));

		final JButton buttonOpenLog = new JButton();
		Font font = new Font("Calibri", Font.PLAIN, 10);
		buttonOpenLog.setFont(font);
		buttonOpenLog.setOpaque(false);
		buttonOpenLog.setContentAreaFilled(false);
		buttonOpenLog.setBorderPainted(false);
		buttonOpenLog.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonOpenLog.setText(KmlUITranslator.getLabelOpenLogWindow());
		buttonOpenLog.setToolTipText(KmlUITranslator.getToolTipOpenLogWindow());
		buttonOpenLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(logFrame.isVisible()){
					logFrame.toFront();
				} else {
					logFrame.pack();
					logFrame.setVisible(true);
				}
			}
		});
		panelStatusButtons.add(buttonOpenLog, new CellConstraints());

		buttonKmlManager = new JButton();
		Font font1 = new Font("Calibri", Font.PLAIN, 10);
		buttonKmlManager.setFont(font1);
		buttonKmlManager.setOpaque(false);
		buttonKmlManager.setContentAreaFilled(false);
		buttonKmlManager.setBorderPainted(false);
		buttonKmlManager.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonKmlManager.setText(KmlUITranslator.getLabelOpenKmlManagerWindow());
		buttonKmlManager.setToolTipText(KmlUITranslator.getToolTipOpenKmlManagerWindow());
		buttonKmlManager.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(kmlFrame.isVisible()){
					kmlFrame.toFront();
				} else {
					kmlFrame.pack();
					kmlFrame.setVisible(true);
				}
			}
		});
		panelStatusButtons.add(buttonKmlManager, new CellConstraints(3, 1));

		ActionListener synchronizeActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				SynchronizationTask task = new SynchronizationTask(KmlUI.this);
				task.execute();	
			}
		};
		buttonSynchronize = new JButton();
		buttonSynchronize.setContentAreaFilled(false);
		buttonSynchronize.setBorderPainted(false);
		buttonSynchronize.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonSynchronize.setFont(new Font("Arial", Font.PLAIN, 16));
		buttonSynchronize.setToolTipText(KmlUITranslator.getToolTipSync());
		buttonSynchronize.setText(KmlUITranslator.getLabelSyncronize());
		buttonSynchronize.addActionListener(synchronizeActionListener);
		panelStatus.add(buttonSynchronize, new CellConstraints(4, 1, 2, 1, CellConstraints.CENTER, CellConstraints.CENTER));

		final JPanel panelTrademark = new JPanel();
		panelTrademark.setBackground(Color.WHITE);
		panelTrademark.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("297dlu"),
				ColumnSpec.decode("5dlu")},
			new RowSpec[] {
				RowSpec.decode("16dlu"),
				FormFactory.DEFAULT_ROWSPEC}));
		frame.getContentPane().add(panelTrademark, new CellConstraints(1, 4, 2, 1));

		ActionListener openMesh4xActionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				OpenURLTask task = new OpenURLTask(KmlUI.this.getFrame(), KmlUI.this, KmlUI.this.getPropertiesProvider().getMesh4xURL());
				task.execute();
			}
		};
		
		final JButton buttonLabelTrademark = new JButton();
		buttonLabelTrademark.setContentAreaFilled(false);
		buttonLabelTrademark.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonLabelTrademark.setBorderPainted(false);
		buttonLabelTrademark.setBackground(Color.WHITE);
		buttonLabelTrademark.setFont(new Font("Calibri", Font.BOLD, 10));
		buttonLabelTrademark.setText(KmlUITranslator.getTradeMark());
		buttonLabelTrademark.setToolTipText(KmlUITranslator.getToolTipTradeMark(this.propertiesProvider.getMesh4xURL()));
		buttonLabelTrademark.addActionListener(openMesh4xActionListener);
		
		panelTrademark.add(buttonLabelTrademark, new CellConstraints(1, 1, 1, 2, CellConstraints.RIGHT, CellConstraints.BOTTOM));
				
		final JButton buttonImageTrademark = new JButton();
		buttonImageTrademark.setContentAreaFilled(false);
		buttonImageTrademark.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonImageTrademark.setBorderPainted(false);
		buttonImageTrademark.setBackground(Color.WHITE);
		buttonImageTrademark.setIcon(IconManager.getTrademarkIcon());
		buttonImageTrademark.setText("");
		buttonImageTrademark.setToolTipText(KmlUITranslator.getToolTipTradeMark(this.propertiesProvider.getMesh4xURL()));
		buttonImageTrademark.addActionListener(openMesh4xActionListener);
		panelTrademark.add(buttonImageTrademark, new CellConstraints(2, 1, 1, 2, CellConstraints.DEFAULT, CellConstraints.BOTTOM));

	}
	
	public void enableAllButtons(){
		 this.buttonSynchronize.setEnabled(true);
		 this.buttonOpenFileEndpoint2.setEnabled(true);
		 this.buttonOpenFileEndpoint1.setEnabled(true);
		 this.buttonOpenEndpoint1.setEnabled(true);
		 this.buttonOpenEndpoint2.setEnabled(true);
		 this.textFieldEndpoint1.setEnabled(true);
		 this.textFieldEndpoint2.setEnabled(true);
		 this.buttonKmlManager.setEnabled(true);
	 }
	 
	 public void disableAllButtons(){
		 this.buttonSynchronize.setEnabled(false);
		 this.buttonOpenFileEndpoint2.setEnabled(false);
		 this.buttonOpenFileEndpoint1.setEnabled(false);
		 this.buttonOpenEndpoint1.setEnabled(false);
		 this.buttonOpenEndpoint2.setEnabled(false);
		 this.textFieldEndpoint1.setEnabled(false);
		 this.textFieldEndpoint2.setEnabled(false);
		 this.buttonKmlManager.setEnabled(false);
	 }

	public SyncAdapterFactory getSyncAdapterFactory() {
		return this.syncAdapterFactory;
	}

	public String getEndpoint2() {
		return this.textFieldEndpoint2.getText();
	}

	public String getEndpoint1() {
		return this.textFieldEndpoint1.getText();
	}

	protected void close() {
		this.logFrame.setVisible(false);
		this.logFrame.dispose();
		
		this.kmlFrame.setVisible(false);
		this.kmlFrame.dispose();
		
		this.frame.setVisible(false);
		this.frame.dispose();
	}

	public PropertiesProvider getPropertiesProvider() {
		return this.propertiesProvider;
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
	
	
	public LogFrame getLog() {
		return this.logFrame;
	}

	public JFrame getFrame() {
		return this.frame;
	}

	@Override
	public void notifyError(String error) {
		setStatusError(error);		
	}
}
