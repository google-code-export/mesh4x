package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.mesh4j.sync.epiinfo.ui.utils.EpiInfoIconManager;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.protocol.ACKMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EqualStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.LastVersionStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.NoChangesMessageProcessor;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.EpiinfoSourceIdResolver;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SyncSessionView extends JPanel implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware{
	
	private static final long serialVersionUID = -3827267831214432274L;
	
	// MODEL VARIABLES
	private JPanel panelProgress;
	private JPanel panelTraffic;
	private JPanel panelStatus;
	private JLabel imageStatus;
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
	private JLabel imageLocalNew;
	private JLabel imageLocalUpdated;
	private JLabel imageLocalDeleted;
	private JLabel imageRemoteNew;
	private JLabel imageRemoteUpdated;
	private JLabel imageRemoteDeleted;
	private JTextArea textAreaStatus;

	private ISyncSession syncSession;
	
	private ISyncSessionViewOwner owner;
	private MessageSyncEngine syncEngine;
	private EpiinfoSourceIdResolver sourceIdResolver;
	
	private int smsIn = 0;
	private int smsOut = 0;
	private int numberOfRemoteAddedItems = 0;
	private int numberOfRemoteUpdatedItems = 0;
	private int numberOfRemoteDeletedItems = 0;
	private int syncMinutes = 0;
	private boolean syncInProcess = false;
	
	// BUSINESS METHODS

	public SyncSessionView() {
		super();
		
		startScheduler();
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 867, 346);
		setLayout(new FormLayout(
			"272dlu",
			"58dlu, 41dlu, default"));

		add(getPanelProgress(), new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.FILL));
		add(getPanelTraffic(), new CellConstraints(1, 2));
		add(getPanelStatus(), new CellConstraints(1,3));
		
	}
	
	private Component getPanelStatus() {
		if (panelStatus == null) {
			panelStatus = new JPanel();
			panelStatus.setBackground(Color.WHITE);
			panelStatus.setLayout(new FormLayout("259dlu","10dlu"));
			panelStatus.add(getTextAreaStatus(), new CellConstraints(1, 1, CellConstraints.FILL, CellConstraints.CENTER));
		}
		return panelStatus;
	}

	private JPanel getPanelTraffic() {
		if (panelTraffic == null) {
			panelTraffic = new JPanel();
			panelTraffic.setBackground(Color.WHITE);
			panelTraffic.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("12dlu"),
					ColumnSpec.decode("35dlu"),
					ColumnSpec.decode("2dlu"),
					ColumnSpec.decode("11dlu"),
					ColumnSpec.decode("18dlu"),
					FormFactory.DEFAULT_COLSPEC,
					ColumnSpec.decode("31dlu"),
					ColumnSpec.decode("35dlu"),
					ColumnSpec.decode("2dlu"),
					FormFactory.DEFAULT_COLSPEC},
				new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					RowSpec.decode("9dlu")}));
			
			panelTraffic.add(getLabelLocalNew(), new CellConstraints(2, 2, CellConstraints.DEFAULT, CellConstraints.TOP));
			panelTraffic.add(getLabelLocalUpdated(), new CellConstraints(2, 3));
			panelTraffic.add(getLabelLocalDeleted(), new CellConstraints(2, 4));
			panelTraffic.add(getLabelRemoteNew(), new CellConstraints(8, 2));
			panelTraffic.add(getLabelRemoteUpdated(), new CellConstraints(8, 3));
			panelTraffic.add(getLabelRemoteDeleted(), new CellConstraints(8, 4));
	
			final JPanel panelInOut = new JPanel();
			panelTraffic.add(panelInOut, new CellConstraints(6, 1, 1, 5));
			panelInOut.setBackground(Color.WHITE);
			panelInOut.setLayout(new FormLayout(
				"56dlu, 25dlu, 2dlu, 18dlu",
				"19dlu, 18dlu"));
	
			final JLabel imageInOut = new JLabel();
			imageInOut.setIcon(EpiInfoIconManager.getInOutIcon());
			imageInOut.setText("");
			panelInOut.add(imageInOut, new CellConstraints(1, 1, 1, 2, CellConstraints.RIGHT, CellConstraints.DEFAULT));
			panelInOut.add(getLabelIn(), new CellConstraints(2, 1, CellConstraints.LEFT, CellConstraints.BOTTOM));
			panelInOut.add(getLabelOut(), new CellConstraints(2, 2, CellConstraints.LEFT, CellConstraints.DEFAULT));
			panelInOut.add(getImageStatus(), new CellConstraints(4, 1, 1, 2, CellConstraints.CENTER, CellConstraints.CENTER));
	
			imageLocalNew = new JLabel();
			imageLocalNew.setText("");
			panelTraffic.add(imageLocalNew, new CellConstraints(4, 2, CellConstraints.FILL, CellConstraints.FILL));
	
			imageLocalUpdated = new JLabel();
			imageLocalUpdated.setText("");
			panelTraffic.add(imageLocalUpdated, new CellConstraints(4, 3, CellConstraints.FILL, CellConstraints.FILL));
	
			imageLocalDeleted = new JLabel();
			imageLocalDeleted.setText("");
			panelTraffic.add(imageLocalDeleted, new CellConstraints(4, 4, CellConstraints.FILL, CellConstraints.FILL));
	
			imageRemoteNew = new JLabel();
			imageRemoteNew.setText("");
			panelTraffic.add(imageRemoteNew, new CellConstraints(10, 2, CellConstraints.FILL, CellConstraints.FILL));
	
			imageRemoteUpdated = new JLabel();
			imageRemoteUpdated.setText("");
			panelTraffic.add(imageRemoteUpdated, new CellConstraints(10, 3, CellConstraints.FILL, CellConstraints.FILL));
	
			imageRemoteDeleted = new JLabel();
			imageRemoteDeleted.setText("");
			panelTraffic.add(imageRemoteDeleted, new CellConstraints(10, 4, CellConstraints.FILL, CellConstraints.FILL));
		}
		return panelTraffic;
	}

	protected JPanel getPanelProgress() {
		if (panelProgress == null) {
			panelProgress = new JPanel();
			panelProgress.setBackground(Color.WHITE);
			panelProgress.setLayout(new FormLayout(
				new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("52dlu"),
					ColumnSpec.decode("140dlu"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("69dlu")},
				new RowSpec[] {
					RowSpec.decode("58dlu")}));
			panelProgress.add(getLabelLocalDataSource(), new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.FILL));
			panelProgress.add(getLabelSyncType(), new CellConstraints(3, 1, CellConstraints.LEFT, CellConstraints.FILL));
			panelProgress.add(getLabelRemoteDataSource(), new CellConstraints(5, 1, CellConstraints.FILL, CellConstraints.FILL));
		}
		return panelProgress;
	}
	
	protected JLabel getLabelLocalDataSource() {
		if (labelLocalDataSource == null) {
			labelLocalDataSource = new JLabel();
			labelLocalDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());
			labelLocalDataSource.setText("");
		}
		return labelLocalDataSource;
	}

	protected JLabel getLabelLocalNew() {
		if (labelLocalNew == null) {
			labelLocalNew = new JLabel();
			labelLocalNew.setFont(new Font("Calibri", Font.BOLD, 12));
			labelLocalNew.setText(EpiInfoCompactUITranslator.getLabelNew(0));
		}
		return labelLocalNew;
	}

	protected JLabel getLabelLocalUpdated() {
		if (labelLocalUpdated == null) {
			labelLocalUpdated = new JLabel();
			labelLocalUpdated.setFont(new Font("Calibri", Font.BOLD, 12));
			labelLocalUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(0));
		}
		return labelLocalUpdated;
	}

	protected JLabel getLabelLocalDeleted() {
		if (labelLocalDeleted == null) {
			labelLocalDeleted = new JLabel();
			labelLocalDeleted.setFont(new Font("Calibri", Font.BOLD, 12));
			labelLocalDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(0));
		}
		return labelLocalDeleted;
	}

	protected JLabel getLabelSyncType() {
		if (labelSyncType == null) {
			labelSyncType = new JLabel();
			labelSyncType.setIcon(EpiInfoIconManager.getSyncMode2WayIcon());
			labelSyncType.setText("");
		}
		return labelSyncType;
	}

	protected JLabel getLabelIn() {
		if (labelIn == null) {
			labelIn = new JLabel();
			labelIn.setFont(new Font("Calibri", Font.BOLD, 12));
			labelIn.setText(EpiInfoCompactUITranslator.getLabelIn(0));
		}
		return labelIn;
	}

	protected JLabel getLabelOut() {
		if (labelOut == null) {
			labelOut = new JLabel();
			labelOut.setFont(new Font("Calibri", Font.BOLD, 12));
			labelOut.setText(EpiInfoCompactUITranslator.getLabelOut(0));
		}
		return labelOut;
	}

	protected JLabel getLabelRemoteDataSource() {
		if (labelRemoteDataSource == null) {
			labelRemoteDataSource = new JLabel();
			labelRemoteDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());
			labelRemoteDataSource.setText("");
		}
		return labelRemoteDataSource;
	}

	protected JLabel getLabelRemoteNew() {
		if (labelRemoteNew == null) {
			labelRemoteNew = new JLabel();
			labelRemoteNew.setFont(new Font("Calibri", Font.BOLD, 12));
			labelRemoteNew.setText(EpiInfoCompactUITranslator.getLabelNew(0));
		}
		return labelRemoteNew;
	}

	protected JLabel getLabelRemoteUpdated() {
		if (labelRemoteUpdated == null) {
			labelRemoteUpdated = new JLabel();
			labelRemoteUpdated.setFont(new Font("Calibri", Font.BOLD, 12));
			labelRemoteUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(0));
		}
		return labelRemoteUpdated;
	}

	protected JLabel getLabelRemoteDeleted() {
		if (labelRemoteDeleted == null) {
			labelRemoteDeleted = new JLabel();
			labelRemoteDeleted.setFont(new Font("Calibri", Font.BOLD, 12));
			labelRemoteDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(0));
		}
		return labelRemoteDeleted;
	}

	protected JLabel getImageStatus() {
		if (imageStatus == null) {
			imageStatus = new JLabel();
			imageStatus.setIcon(EpiInfoIconManager.getStatusReadyIcon());
			imageStatus.setText("");
		}
		return imageStatus;
	}
	
	private JTextArea getTextAreaStatus(){
		if (textAreaStatus == null) {
			textAreaStatus = new JTextArea();
			textAreaStatus.setFont(new Font("Calibri", Font.BOLD, 12));
			textAreaStatus.setLineWrap(true);
			textAreaStatus.setWrapStyleWord(true);
			textAreaStatus.setOpaque(true);
			textAreaStatus.setEditable(false);
			textAreaStatus.setText("");
		}
		return textAreaStatus;
		
	}
	
	// BUSINESS METHODS
	public void updateRemoteDataSource(String sourceType) {
		this.labelRemoteDataSource.setIcon(EpiInfoIconManager.getSourceImage(sourceType, true));
	}
	
	public void viewSession(ISyncSession syncSession){
		this.syncSession = syncSession;
		this.reset();
		
		if(syncSession == null){
			this.labelLocalDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());		
			this.labelSyncType.setIcon(EpiInfoIconManager.getSyncModeIcon(true, true));
			this.setReadyImageStatus();
		} else {
			IMessageSyncAdapter adapter = this.syncEngine.getSource(syncSession.getSourceId());
			this.labelLocalDataSource.setIcon(EpiInfoIconManager.getSourceImage(adapter.getSourceType(), false));
			
			this.labelSyncType.setIcon(EpiInfoIconManager.getSyncModeIcon(syncSession.shouldSendChanges(), syncSession.shouldReceiveChanges()));
			
			if(syncSession.isOpen()){
				this.setInProcessImageStatus("");
			} else {
				this.setReadyImageStatus();
			}
			updateLocalStatus();
		}
		
		this.labelRemoteDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());
		
	}
	
	public void reset(){
		
		this.labelLocalNew.setText(EpiInfoCompactUITranslator.getLabelNew(0));
		this.labelLocalDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(0));
		this.labelLocalUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(0));

		this.labelRemoteNew.setText(EpiInfoCompactUITranslator.getLabelNew(0));
		this.labelRemoteDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(0));
		this.labelRemoteUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(0));

		this.labelIn.setText(EpiInfoCompactUITranslator.getLabelIn(0));
		this.labelOut.setText(EpiInfoCompactUITranslator.getLabelOut(0));
		
		this.smsIn = 0;
		this.smsOut = 0;
		this.numberOfRemoteAddedItems = 0;
		this.numberOfRemoteDeletedItems = 0;
		this.numberOfRemoteUpdatedItems = 0;
	}
	
	// Status images methods
	
	public void setErrorImageStatus(String msg) {
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusErrorIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setInProcessImageStatus(String msg) {
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusInProcessIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setEndSyncImageStatus(String msg) {
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusOkIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setReadyImageStatus() {
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusReadyIcon());
		this.imageStatus.setToolTipText("");
	}
	
	// Detailed status
	
	public void increaseSmsIn() {
		this.smsIn = this.smsIn + 1;
		this.labelIn.setText(EpiInfoCompactUITranslator.getLabelIn(this.smsIn));
	}
	
	public void increaseSmsOut() {
		this.smsOut = this.smsOut + 1;
		this.labelOut.setText(EpiInfoCompactUITranslator.getLabelOut(this.smsOut));
	}

	public void updateLocalStatus() {
		this.labelLocalNew.setText(EpiInfoCompactUITranslator.getLabelNew(this.syncSession.getNumberOfAddedItems()));
		this.labelLocalDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(this.syncSession.getNumberOfDeletedItems()));
		this.labelLocalUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(this.syncSession.getNumberOfUpdatedItems()));
	}
	
	public void updateRemoteStatus(int addTotal, int updateTotal, int deleteTotal) {
		if(addTotal > this.numberOfRemoteAddedItems || 
				updateTotal > this.numberOfRemoteUpdatedItems ||
				deleteTotal > this.numberOfRemoteDeletedItems){
			this.numberOfRemoteAddedItems = addTotal;
			this.numberOfRemoteDeletedItems = deleteTotal;
			this.numberOfRemoteUpdatedItems = updateTotal;
		
			this.labelRemoteNew.setText(EpiInfoCompactUITranslator.getLabelNew(addTotal));
			this.labelRemoteDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(deleteTotal));
			this.labelRemoteUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(updateTotal));
		}
	}

	private boolean accepts(ISyncSession syncSession) {
		return this.syncSession != null && this.syncSession.getSessionId().equals(syncSession.getSessionId());
	}
	
	// IMessageSyncAware methods
	
	@Override
	public void beginSync(ISyncSession syncSession) {
		if(this.syncSession == null){
			this.syncInProcess = true;
			this.syncSession = syncSession;
			this.reset();
			
			IMessageSyncAdapter adapter = this.syncEngine.getSource(syncSession.getSourceId());
			this.labelLocalDataSource.setIcon(EpiInfoIconManager.getSourceImage(adapter.getSourceType(), false));
			this.labelRemoteDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());
			this.labelSyncType.setIcon(EpiInfoIconManager.getSyncModeIcon(syncSession.shouldSendChanges(), syncSession.shouldReceiveChanges()));
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			
			String msg = EpiInfoCompactUITranslator.getMessageSyncStarted(dateFormat.format(new Date()));
			this.setStatus(msg);
			
		}

		if(this.owner != null){
			this.owner.notifyNewSync();
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		if(this.accepts(syncSession)){
			String error = EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId()));
			this.setErrorImageStatus(error);
			
			if(owner != null){
				this.owner.notifyEndSync(true);
			}
			this.viewSession(null);
			this.syncInProcess = false;
		}		
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		if(accepts(syncSession)){
			if(!conflicts.isEmpty()){
				String msg = EpiInfoCompactUITranslator.getMessageSyncFailed();
				this.setErrorImageStatus(msg);	
				this.setStatus(msg);
			} else {
				String msg = EpiInfoCompactUITranslator.getMessageSyncSuccessfully();
				this.setEndSyncImageStatus(msg);
				this.setStatus(msg);
			}
			
			if(owner != null){
				this.owner.notifyEndSync(!conflicts.isEmpty());
			}
			
			this.viewSession(null);
			this.syncInProcess = false;
		}		
	}
	
	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		if(accepts(syncSession)){
			String msg = EpiInfoCompactUITranslator.getMessageCancelSyncSuccessfully();
			this.setEndSyncImageStatus(msg);
			this.setStatus(msg);
			
			if(this.owner != null){
				this.owner.notifyEndCancelSync();
			}
			
			this.viewSession(null);
			this.syncInProcess = false;
		}			
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		if(accepts(syncSession)){
			String error = EpiInfoUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId());
			this.setEndSyncImageStatus(error);
			
			this.syncInProcess = false;
			this.viewSession(null);
		}
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		// nothing to do		
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		// nothing to do		
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		if(accepts(syncSession)){
			this.updateLocalStatus();
			
			if(BeginSyncMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				String dataSourceType = BeginSyncMessageProcessor.getSourceType(message.getData());
				this.updateRemoteDataSource(dataSourceType);
				this.increaseSmsIn();
			}

			if(EqualStatusMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				String dataSourceType = message.getData();
				this.updateRemoteDataSource(dataSourceType);
			}
			
			if(NoChangesMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				String dataSourceType = message.getData();
				this.updateRemoteDataSource(dataSourceType);
			}
			
			if(LastVersionStatusMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				String dataSourceType = LastVersionStatusMessageProcessor.getSourceType(message.getData());
				this.updateRemoteDataSource(dataSourceType);
			}
			
			if(ACKMergeMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				this.updateRemoteStatus(
						ACKMergeMessageProcessor.getNumberOfAddedItems(message.getData()), 
						ACKMergeMessageProcessor.getNumberOfUpdatedItems(message.getData()),
						ACKMergeMessageProcessor.getNumberOfDeletedItems(message.getData()));			
			}
		}
		
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		// nothing to do		
	}

	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.increaseSmsIn();
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		String error = EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message);
		this.setErrorImageStatus(error);
		this.increaseSmsIn();
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.increaseSmsOut();
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		String error = EpiInfoUITranslator.getMessageNotifySendMessageError(endpointId, message);
		this.setErrorImageStatus(error);
		this.increaseSmsOut();
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		// nothing to do
	}

	
	public void initialize(ISyncSessionViewOwner owner, MessageSyncEngine syncEngine, EpiinfoSourceIdResolver sourceIdResolver){
		this.owner = owner;
		this.syncEngine = syncEngine;
		this.sourceIdResolver = sourceIdResolver;
	}
	
	
	public void setStatus(String status) {
		this.textAreaStatus.setText(status);
	}
	
	public void setError(String error){
		this.textAreaStatus.setForeground(new Color(255, 0, 0));
		this.textAreaStatus.setText(error);
	}
	
	private void startScheduler() {
		Action refreshStatus = new AbstractAction() {
			private static final long serialVersionUID = 6527495893765136292L;
			public void actionPerformed(ActionEvent e) {
				refresh();
		    }
		};

		// 1 minute
		new Timer(1 * 60 * 1000, refreshStatus).start();
	}

	protected void refresh(){
		if (syncInProcess) {
			this.syncMinutes = this.syncMinutes + 1;
			String actualStatus = this.textAreaStatus.getText();
			int index = actualStatus.indexOf(" (");
			if(index > 0){
				actualStatus = actualStatus.substring(0, index);
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append(actualStatus);
					
			int minutes = this.syncMinutes % 60;
			if(minutes > 0){
				sb.append(" (");
				int hrs = this.syncMinutes / 60;
				if(hrs > 0){
					hrs = hrs % 24;
					int days = hrs / 24;
					if(days > 0){
						sb.append(days);
						if(days > 1){
							sb.append(" ");
							sb.append(EpiInfoCompactUITranslator.getLabelDays());
							sb.append(" ");
						} else {
							sb.append(" ");
							sb.append(EpiInfoCompactUITranslator.getLabelDay());
							sb.append(" ");
						}
					}
					
					sb.append(hrs);
					if(hrs > 1){
						sb.append(" ");
						sb.append(EpiInfoCompactUITranslator.getLabelHours());
						sb.append(" ");
					} else {
						sb.append(" ");
						sb.append(EpiInfoCompactUITranslator.getLabelHour());
						sb.append(" ");
					}
				}
				
				sb.append(minutes);
				if(minutes > 1){
					sb.append(" ");
					sb.append(EpiInfoCompactUITranslator.getLabelMinutes());
					sb.append(" ");
				} else {
					sb.append(" ");
					sb.append(EpiInfoCompactUITranslator.getLabelMinute());
					sb.append(" ");
				}				
				sb.append(EpiInfoCompactUITranslator.getLabelAgo());
				sb.append(")");
			}		
		    setStatus(sb.toString());
		}
	}

	public boolean isSyncInProcess() {
		return this.syncInProcess;
	}	
}
