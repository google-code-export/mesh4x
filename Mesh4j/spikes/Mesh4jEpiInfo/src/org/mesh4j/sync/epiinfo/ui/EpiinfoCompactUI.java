package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncAdapterFactory;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.ui.tasks.CancelSyncTask;
import org.mesh4j.sync.ui.tasks.SynchronizeTask;
import org.mesh4j.sync.ui.tasks.TestPhoneTask;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.EpiinfoCompactConsoleNotification;
import org.mesh4j.sync.utils.SyncEngineUtil;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class EpiinfoCompactUI {

	private final static Log Logger = LogFactory.getLog(EpiinfoCompactUI.class);
	
	// MODEL VARIABLES
	private JFrame frame;
	private JLabel imageStatus;
	private JButton buttonOpenLog;
	private JButton buttonConfiguration;
	private JLabel labelStatus;
	private JLabel labelRemoteDeleted;
	private JLabel labelRemoteUpdated;
	private JLabel labelRemoteNew;
	private JLabel labelRemoteDataSource;
	private JLabel labelOut;
	private JLabel labelIn;
	private JLabel labelSyncType;
	private JLabel labelLocalDeleted;
	private JLabel labelLocalUpdated;
	private JLabel labelLocalNew;
	private JLabel labelLocalDataSource;
	private JPanel panelProgress;
	private JComboBox comboBoxSyncMode;
	private JComboBox comboBoxMappingDataSource;
	private JButton buttonSync;
	private JButton buttonReadyToSync;
	private JButton buttonTestPhone;
	private JComboBox comboBoxEndpoint;
	private JLabel labelSyncWith;
	private JPanel panelSync;
	
	private LogFrame logFrame;
	private ConfigurationFrame cfgFrame;	
	private EpiinfoCompactConsoleNotification consoleNotification;
	
	private MessageSyncEngine syncEngine;
	private boolean syncInProcess = false;	
	private int smsIn = 0;
	private int smsOut = 0;
	private int numberOfRemoteAddedItems = 0;
	private int numberOfRemoteUpdatedItems = 0;
	private int numberOfRemoteDeletedItems = 0;
			
	// BUSINESS METHODS
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EpiinfoCompactUI window = new EpiinfoCompactUI();
					window.frame.pack();
					window.frame.setSize(window.frame.getPreferredSize());
					window.frame.setVisible(true);
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
				}
			}
		});
	}

	public EpiinfoCompactUI() throws Exception {
		this.createUI();
		this.consoleNotification = new EpiinfoCompactConsoleNotification(logFrame, this);
		this.setReadyImageStatus();
		this.syncEngine = SyncEngineUtil.createSyncEngine(consoleNotification);
		this.startUpSyncEngine();
	}
	
	protected void startUpSyncEngine() throws Exception {
		try{
			if(this.syncEngine == null){
				this.setStartUpError();
			} else {
				this.syncEngine.getChannel().startUp();		
			}
		} catch(Throwable e){
			this.setStartUpError();
			Logger.error(e.getMessage(), e);
		}
	}

	private void shutdownSyncEngine(){
		try{
			this.syncEngine.getChannel().shutdown();
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
		}
	}
	
	// Status
	public void setStatus(String status) {
		this.labelStatus.setText(status);
	}
	
	public void setEndSync(boolean error) {
		this.syncInProcess = false;
		if(error){
			this.setStatus("Sync failed");
			this.setErrorImageStatus();
		} else {
			this.setStatus("Sync successfully");
			this.setEndSyncImageStatus();
		}
		
		this.buttonSync.setText("Sync now");
		this.enableAllButtons();		
	}
	
	public void updateRemoteDataSource(String sourceType) {
		this.labelRemoteDataSource.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, getSourceImage(sourceType)));
	}
	
	private String getSourceImage(String sourceType) {
		if(FeedSyncAdapterFactory.SOURCE_TYPE.equals(sourceType)){
			return "/feedRSSDataSource.png";
		} else if (HttpSyncAdapterFactory.SOURCE_TYPE.equals(sourceType)){
			return "/httpDataSource.png";
		} else if (KMLDOMLoaderFactory.SOURCE_TYPE.equals(sourceType)){
			return "/kmlDataSource.png";
		} else if (MsAccessSyncAdapterFactory.SOURCE_TYPE.equals(sourceType)){
			return "/msAccessDataSource.png";
		} else if (MsExcelSyncAdapterFactory.SOURCE_TYPE.equals(sourceType)){
			return "/msExcelDataSource.png";
		} else {
			return "/undefinedDataSource.png";
		}
	}

	//	17:23 (7 minutes ago)
	public void setBeginSync(String sourceId, boolean sendChanges, boolean receiveChanges) {
		
		IMessageSyncAdapter adapter = this.syncEngine.getSource(sourceId);
		
		this.labelLocalDataSource.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, getSourceImage(adapter.getSourceType())));
		this.labelRemoteDataSource.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/undefinedDataSource.png"));

		if(sendChanges && receiveChanges){
			this.labelSyncType.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/2WaySync.png"));
		}else if(sendChanges){
			this.labelSyncType.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/sendChangesOnly.png"));
		} else {
			this.labelSyncType.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/receiveChangesOnly.png"));
		}
		this.labelLocalNew.setText("New: 0");
		this.labelLocalDeleted.setText("Deleted: 0");
		this.labelLocalUpdated.setText("Updated: 0");

		this.labelRemoteNew.setText("New: 0");
		this.labelRemoteDeleted.setText("Deleted: 0");
		this.labelRemoteUpdated.setText("Updated: 0");

		this.labelIn.setText("In: 0");
		this.labelOut.setText("Out: 0");
		
		this.smsIn = 0;
		this.smsOut = 0;
		this.numberOfRemoteAddedItems = 0;
		this.numberOfRemoteDeletedItems = 0;
		this.numberOfRemoteUpdatedItems = 0;
		
		this.syncInProcess = true;
		this.setStatus("Sync Started "+ new Date());
		this.buttonSync.setText("Cancel Sync");	
		this.setInProcessImageStatus();
		this.logFrame.cleanLog();
		this.disableAllButtons();
	}
	
	public void setErrorSync(Throwable t)	{
		this.syncInProcess = false;
		this.setErrorImageStatus();
		this.logFrame.logError(t, EpiInfoUITranslator.getLabelFailed());
	}

	public void setBeginCancelSync()	{
		this.disableAllButtons();
		this.setInProcessImageStatus();
	}

	public void setEndCancelSync() {
		this.syncInProcess = false;
		this.setStatus("Cancel Sync successfully");
		
		this.buttonSync.setText("Sync now");
		this.setEndSyncImageStatus();
		this.enableAllButtons();		
	}
	
	public void setStartUpError()	{
		this.setStatus("Start up error, please check phone configuration and compatibility !!!");
		this.setErrorImageStatus();
// TODO (JMT) Uncomment this section
//		comboBoxEndpoint.setEnabled(false);
//		comboBoxMappingDataSource.setEnabled(false);
//		comboBoxSyncType.setEnabled(false);
		
//		buttonReadyToSync.setEnabled(false);
//		buttonTestPhone.setEnabled(true);
//		buttonSync.setEnabled(false); 
	}
	
	public void setStartTestForPhoneCompatibility(){
		setInProcessImageStatus();
		setStatus("Testing phone compatibility....");
	}
	
	public void setPhoneIsCompatible() {
		setStatus("Phone is compatible....");
		enableAllButtons();
		setReadyImageStatus();
	}
	
	// enable/Disable
	public void enableAllButtons() {
		comboBoxEndpoint.setEnabled(true);
		comboBoxMappingDataSource.setEnabled(true);
		comboBoxSyncMode.setEnabled(true);
		
		buttonReadyToSync.setEnabled(true);
		buttonTestPhone.setEnabled(true);		
	}
	
	public void disableAllButtons() {
		comboBoxEndpoint.setEnabled(false);
		comboBoxMappingDataSource.setEnabled(false);
		comboBoxSyncMode.setEnabled(false);
		
		buttonReadyToSync.setEnabled(false);
		buttonTestPhone.setEnabled(false);
	}
	
	// Status images methods
	
	public void setErrorImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/error.gif"));		
	}
	
	public void setInProcessImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/inprocess.gif"));
	}
	
	public void setEndSyncImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/endsync.png"));		
	}
	
	public void setReadyImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/endsync.png"));	
	}
	
	// Detailed status
	public void increaseSmsIn() {
		this.smsIn = this.smsIn + 1;
		this.labelIn.setText("In: " + this.smsIn);
	}
	
	public void increaseSmsOut() {
		this.smsOut = this.smsOut + 1;
		this.labelOut.setText("Out: " + this.smsOut);
	}

	public void updateLocalStatus(int addTotal, int updateTotal, int deleteTotal) {
		this.labelLocalNew.setText("New: " + addTotal);
		this.labelLocalDeleted.setText("Deleted: " + deleteTotal);
		this.labelLocalUpdated.setText("Updated: " + updateTotal);		
	}
	
	public void updateRemoteStatus(int addTotal, int updateTotal, int deleteTotal) {
		if(addTotal > this.numberOfRemoteAddedItems || 
				updateTotal > this.numberOfRemoteUpdatedItems ||
				deleteTotal > this.numberOfRemoteDeletedItems){
			this.numberOfRemoteAddedItems = addTotal;
			this.numberOfRemoteDeletedItems = deleteTotal;
			this.numberOfRemoteUpdatedItems = updateTotal;
		
			this.labelRemoteNew.setText("New: " + addTotal);
			this.labelRemoteDeleted.setText("Deleted: " + deleteTotal);
			this.labelRemoteUpdated.setText("Updated: " + updateTotal);
		}
	}
	
	// UI Design
	private void createUI() {
		frame = new JFrame();
		
		WindowAdapter windowAdapter = new WindowAdapter() {
			public void windowClosed(final WindowEvent e) {
				shutdownSyncEngine();
			}
		};
		
		frame.addWindowListener(windowAdapter);
		frame.setIconImage(SwingResourceManager.getImage(EpiinfoCompactUI.class, "/cdc.gif"));
		frame.getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("287dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("58dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("114dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC}));
		frame.setResizable(false);
		frame.setTitle(EpiInfoUITranslator.getTitle());
		frame.setBounds(100, 100, 593, 413);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(getPanelSync(), new CellConstraints(2, 2));
		frame.getContentPane().add(getPanelProgress(), new CellConstraints(2, 4));

		final JPanel panelStatus = new JPanel();
		panelStatus.setLayout(new FormLayout(
			new ColumnSpec[] {
				ColumnSpec.decode("253dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu")},
			new RowSpec[] {
				RowSpec.decode("20dlu"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("24dlu")}));
		frame.getContentPane().add(panelStatus, new CellConstraints(2, 6));
		panelStatus.add(getLabelStatus(), new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));
		panelStatus.add(getImageStatus(), new CellConstraints(3, 1, 1, 3, CellConstraints.FILL, CellConstraints.FILL));

		final JPanel panelStatusButtons = new JPanel();
		panelStatusButtons.setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("22dlu")}));
		panelStatus.add(panelStatusButtons, new CellConstraints(1, 3));
		panelStatusButtons.add(getButtonOpenLog(), new CellConstraints(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

		panelStatusButtons.add(getButtonConfiguration(), new CellConstraints(3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
		
		logFrame = new LogFrame();
		cfgFrame = new ConfigurationFrame();

	}

	protected JPanel getPanelSync() {
		if (panelSync == null) {
			panelSync = new JPanel();
			panelSync.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("179dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("70dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC}));
			panelSync.add(getLabelSyncWith(), new CellConstraints());
			panelSync.add(getComboBoxEndpoint(), new CellConstraints(3, 1));
			panelSync.add(getComboBoxMappingDataSource(), new CellConstraints(3, 3));
			panelSync.add(getComboBoxSyncMode(), new CellConstraints(3, 5));
			panelSync.add(getButtonTestPhone(), new CellConstraints(5, 1));
			panelSync.add(getButtonReadyToSync(), new CellConstraints(5, 3));
			panelSync.add(getButtonSync(), new CellConstraints(5, 5));
		}
		return panelSync;
	}

	protected JLabel getLabelSyncWith() {
		if (labelSyncWith == null) {
			labelSyncWith = new JLabel();
			labelSyncWith.setText("Sync With:");
		}
		return labelSyncWith;
	}

	public JComboBox getComboBoxEndpoint() {
		if (comboBoxEndpoint == null) {
			comboBoxEndpoint = new JComboBox();
			comboBoxEndpoint.setModel(new DefaultComboBoxModel(SyncEngineUtil.getEndpointMappings()));
		}
		return comboBoxEndpoint;
	}

	protected JButton getButtonTestPhone() {
		if (buttonTestPhone == null) {
			buttonTestPhone = new JButton();
			buttonTestPhone.setText("Test Phone");
			
			ActionListener testPhoneActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					TestPhoneTask task = new TestPhoneTask(EpiinfoCompactUI.this);
					task.execute();
				}
			};	
			buttonTestPhone.addActionListener(testPhoneActionListener);
		} 
		return buttonTestPhone;
	}

	protected JButton getButtonReadyToSync() {
		if (buttonReadyToSync == null) {
			buttonReadyToSync = new JButton();
			buttonReadyToSync.setText("Ready To Sync ?");
		}
		return buttonReadyToSync;
	}

	protected JButton getButtonSync() {
		if (buttonSync == null) {
			buttonSync = new JButton();
			buttonSync.setText("Sync now");
			
			ActionListener synchronizeActionListener = new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					SwingWorker<Void, Void> task = syncInProcess ? new CancelSyncTask(EpiinfoCompactUI.this) : new SynchronizeTask(EpiinfoCompactUI.this);
					task.execute();
				}
			};	
			buttonSync.addActionListener(synchronizeActionListener);
		}
		return buttonSync;
	}


	public JComboBox getComboBoxMappingDataSource() {
		if (comboBoxMappingDataSource == null) {
			comboBoxMappingDataSource = new JComboBox();
			comboBoxMappingDataSource.setModel(new DefaultComboBoxModel(SyncEngineUtil.getDataSourceMappings()));
		}
		return comboBoxMappingDataSource;
	}

	public JComboBox getComboBoxSyncMode() {
		if (comboBoxSyncMode == null) {
			comboBoxSyncMode = new JComboBox();

			DefaultComboBoxModel syncTypesTableModel = new DefaultComboBoxModel(new SyncMode[]{SyncMode.SendAndReceiveChanges, SyncMode.SendChangesOnly, SyncMode.ReceiveChangesOnly});
			comboBoxSyncMode.setModel(syncTypesTableModel);
		}
		return comboBoxSyncMode;
	}

	protected JPanel getPanelProgress() {
		if (panelProgress == null) {
			panelProgress = new JPanel();
			panelProgress.setBackground(Color.WHITE);
			panelProgress.setBorder(new BevelBorder(BevelBorder.LOWERED));
			panelProgress.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("55dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("159dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("59dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC}));
			panelProgress.add(getLabelLocalDataSource(), new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));
			panelProgress.add(getLabelLocalNew(), new CellConstraints(2, 3));
			panelProgress.add(getLabelLocalUpdated(), new CellConstraints(2, 5));
			panelProgress.add(getLabelLocalDeleted(), new CellConstraints(2, 7));
			panelProgress.add(getLabelSyncType(), new CellConstraints(4, 1, CellConstraints.CENTER, CellConstraints.FILL));
			panelProgress.add(getLabelRemoteDataSource(), new CellConstraints(6, 1, CellConstraints.FILL, CellConstraints.FILL));
			panelProgress.add(getLabelRemoteNew(), new CellConstraints(6, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
			panelProgress.add(getLabelRemoteUpdated(), new CellConstraints(6, 5, CellConstraints.LEFT, CellConstraints.DEFAULT));
			panelProgress.add(getLabelRemoteDeleted(), new CellConstraints(6, 7, CellConstraints.LEFT, CellConstraints.DEFAULT));

			final JPanel panelInOut = new JPanel();
			panelInOut.setBackground(Color.WHITE);
			panelInOut.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("27dlu"),
					ColumnSpec.decode("56dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("42dlu")},
				new RowSpec[] {
					RowSpec.decode("19dlu"),
					FormFactory.RELATED_GAP_ROWSPEC,
					RowSpec.decode("18dlu"),
					FormFactory.RELATED_GAP_ROWSPEC}));
			panelProgress.add(panelInOut, new CellConstraints(4, 3, 1, 5));

			final JLabel imageInOut = new JLabel();
			imageInOut.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/inOut.png"));
			imageInOut.setText("");
			panelInOut.add(imageInOut, new CellConstraints(2, 1, 1, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			panelInOut.add(getLabelIn(), new CellConstraints(4, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
			panelInOut.add(getLabelOut(), new CellConstraints(4, 3, CellConstraints.LEFT, CellConstraints.TOP));
		}
		return panelProgress;
	}

	protected JLabel getLabelLocalDataSource() {
		if (labelLocalDataSource == null) {
			labelLocalDataSource = new JLabel();
			labelLocalDataSource.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/undefinedDataSource.png"));
			labelLocalDataSource.setText("");
		}
		return labelLocalDataSource;
	}

	protected JLabel getLabelLocalNew() {
		if (labelLocalNew == null) {
			labelLocalNew = new JLabel();
			labelLocalNew.setText("New: ");
		}
		return labelLocalNew;
	}

	protected JLabel getLabelLocalUpdated() {
		if (labelLocalUpdated == null) {
			labelLocalUpdated = new JLabel();
			labelLocalUpdated.setText("Updated: ");
		}
		return labelLocalUpdated;
	}

	protected JLabel getLabelLocalDeleted() {
		if (labelLocalDeleted == null) {
			labelLocalDeleted = new JLabel();
			labelLocalDeleted.setText("Deleted: ");
		}
		return labelLocalDeleted;
	}

	protected JLabel getLabelSyncType() {
		if (labelSyncType == null) {
			labelSyncType = new JLabel();
			labelSyncType.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/2WaySync.png"));
			labelSyncType.setText("");
		}
		return labelSyncType;
	}

	protected JLabel getLabelIn() {
		if (labelIn == null) {
			labelIn = new JLabel();
			labelIn.setText("In: ");
		}
		return labelIn;
	}

	protected JLabel getLabelOut() {
		if (labelOut == null) {
			labelOut = new JLabel();
			labelOut.setText("Out: ");
		}
		return labelOut;
	}

	protected JLabel getLabelRemoteDataSource() {
		if (labelRemoteDataSource == null) {
			labelRemoteDataSource = new JLabel();
			labelRemoteDataSource.setIcon(SwingResourceManager.getIcon(EpiinfoCompactUI.class, "/undefinedDataSource.png"));
			labelRemoteDataSource.setText("");
		}
		return labelRemoteDataSource;
	}

	protected JLabel getLabelRemoteNew() {
		if (labelRemoteNew == null) {
			labelRemoteNew = new JLabel();
			labelRemoteNew.setText("New: ");
		}
		return labelRemoteNew;
	}

	protected JLabel getLabelRemoteUpdated() {
		if (labelRemoteUpdated == null) {
			labelRemoteUpdated = new JLabel();
			labelRemoteUpdated.setText("Updated: ");
		}
		return labelRemoteUpdated;
	}

	protected JLabel getLabelRemoteDeleted() {
		if (labelRemoteDeleted == null) {
			labelRemoteDeleted = new JLabel();
			labelRemoteDeleted.setText("Deleted: ");
		}
		return labelRemoteDeleted;
	}


	protected JLabel getLabelStatus() {
		if (labelStatus == null) {
			labelStatus = new JLabel();
			labelStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
			labelStatus.setText("");
		}
		return labelStatus;
	}

	protected JButton getButtonOpenLog() {
		if (buttonOpenLog == null) {
			buttonOpenLog = new JButton();
			buttonOpenLog.setText("Open Log Windows");
			
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
		}
		return buttonOpenLog;
	}
	
	protected JButton getButtonConfiguration() {
		if (buttonConfiguration == null) {
			buttonConfiguration = new JButton();
			buttonConfiguration.setText("Configuration");
			
			buttonConfiguration.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(cfgFrame.isVisible()){
						cfgFrame.toFront();
					} else {
						cfgFrame.pack();
						cfgFrame.setVisible(true);
					}
				}
			});
		}
		return buttonConfiguration;
	}

	protected JLabel getImageStatus() {
		if (imageStatus == null) {
			imageStatus = new JLabel();
			imageStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
			imageStatus.setText("");
		}
		return imageStatus;
	}

	public Component getFrame() {
		return this.frame;
	}

	public MessageSyncEngine getSyncEngine() {
		return this.syncEngine;
	}
	
	public EpiinfoCompactConsoleNotification getConsoleNotification() {
		return this.consoleNotification;
	}

}
