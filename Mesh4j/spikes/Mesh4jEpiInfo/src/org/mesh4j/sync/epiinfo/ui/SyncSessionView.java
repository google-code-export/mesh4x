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
import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.InOutStatistics;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.protocol.ACKEndSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EndSyncMessageProcessor;
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
	private JLabel labelInOutPendings;
	
	private ISyncSession syncSession;
	
	private ISyncSessionViewOwner owner;
	private EpiinfoSourceIdResolver sourceIdResolver;
	private IChannel channel;
	
	private int syncMinutes = 0;
	
	// BUSINESS METHODS

	public SyncSessionView(boolean mustStartRefreshStatus) {
		super();
		
		if(mustStartRefreshStatus){
			startScheduler();
		}
		
		setBackground(Color.WHITE);
		setBounds(100, 100, 867, 346);
		setLayout(new FormLayout(
			"272dlu",
			"58dlu, 50dlu, 12dlu"));

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
					RowSpec.decode("9dlu"),
					RowSpec.decode("default")}));
			
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

			final JPanel panelInOurPendings = new JPanel();
			panelInOurPendings.setBackground(Color.WHITE);
			panelInOurPendings.setLayout(new FormLayout(
				new ColumnSpec[] {
					ColumnSpec.decode("78dlu"),
					ColumnSpec.decode("102dlu")},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC}));
			panelTraffic.add(panelInOurPendings, new CellConstraints(1, 6, 10, 1, CellConstraints.DEFAULT, CellConstraints.TOP));

			labelInOutPendings = new JLabel();
			labelInOutPendings.setForeground(new Color(128, 128, 128));
			labelInOutPendings.setFont(new Font("Calibri", Font.PLAIN, 8));
			labelInOutPendings.setText("");
			panelInOurPendings.add(labelInOutPendings, new CellConstraints(2, 1, CellConstraints.CENTER, CellConstraints.FILL));
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

	public void viewSession(ISyncSession syncSession){
		this.syncSession = syncSession;
		this.reset();
		
		if(syncSession != null){
			updateSessionStatus();
			updateInOut();
			
			if(syncSession.isOpen()){
				setInProcess("");
			}
			
			if(syncSession.isBroken()){
				setError("");
			}
		}
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
		this.labelInOutPendings.setText("");
		
		this.syncMinutes = 0;
		
		this.labelLocalDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());		
		this.labelRemoteDataSource.setIcon(EpiInfoIconManager.getUndefinedSourceImage());
		this.labelSyncType.setIcon(EpiInfoIconManager.getSyncModeIcon(true, true));
		this.setReady("");
	}
	
	private void updateSessionStatus() {
		this.labelLocalNew.setText(EpiInfoCompactUITranslator.getLabelNew(this.syncSession.getNumberOfAddedItems()));
		this.labelLocalDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(this.syncSession.getNumberOfDeletedItems()));
		this.labelLocalUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(this.syncSession.getNumberOfUpdatedItems()));

		this.labelRemoteNew.setText(EpiInfoCompactUITranslator.getLabelNew(this.syncSession.getTargetNumberOfAddedItems()));
		this.labelRemoteDeleted.setText(EpiInfoCompactUITranslator.getLabelDeleted(this.syncSession.getTargetNumberOfDeletedItems()));
		this.labelRemoteUpdated.setText(EpiInfoCompactUITranslator.getLabelUpdated(this.syncSession.getTargetNumberOfUpdatedItems()));
		
		this.labelLocalDataSource.setIcon(EpiInfoIconManager.getSourceImage(this.syncSession.getSourceType(), false));
		this.labelRemoteDataSource.setIcon(EpiInfoIconManager.getSourceImage(this.syncSession.getTargetSourceType(), true));
		
		this.labelSyncType.setIcon(EpiInfoIconManager.getSyncModeIcon(this.syncSession.shouldSendChanges(), this.syncSession.shouldReceiveChanges()));
		
		if(!this.syncSession.isOpen()){
			this.setReady("");
		}
	}
	
	private boolean accepts(ISyncSession syncSession) {
		return this.syncSession != null && this.syncSession.getSessionId().equals(syncSession.getSessionId());
	}
	
	public void updateInOut() {
		if(this.syncSession.isOpen()){
			InOutStatistics stat = this.channel.getInOutStatistics(this.syncSession.getSessionId(), this.syncSession.getVersion());
			this.labelIn.setText(EpiInfoCompactUITranslator.getLabelIn(stat.getNumberInMessages()));
			this.labelOut.setText(EpiInfoCompactUITranslator.getLabelOut(stat.getNumberOutMessages()));
			
			if(stat.getNumberInPendingToArriveMessages() == 0 && stat.getNumberOutPendingAckMessages() == 0){
				this.labelInOutPendings.setText("");
			} else {
				this.labelInOutPendings.setText(EpiInfoCompactUITranslator.getMessageInOutPendings(stat.getNumberInPendingToArriveMessages(), stat.getNumberOutPendingAckMessages()));				
			}
		} else {
			this.labelIn.setText(EpiInfoCompactUITranslator.getLabelIn(syncSession.getLastNumberInMessages()));
			this.labelOut.setText(EpiInfoCompactUITranslator.getLabelOut(syncSession.getLastNumberOutMessages()));
			this.labelInOutPendings.setText("");
		}
	}

	// IMessageSyncAware methods
	
	@Override
	public void beginSync(ISyncSession syncSession) {
		if(this.syncSession == null){
			this.syncSession = syncSession;
		}
		
		if(accepts(syncSession)){
			this.reset();
			
			if(this.owner != null){
				this.owner.notifyBeginSync();
			}
			
			this.updateSessionStatus();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
			String msg = EpiInfoCompactUITranslator.getMessageSyncStarted(dateFormat.format(new Date()));
			this.setInProcess(msg);
		}

		if(this.owner != null){
			this.owner.notifyNewSync();
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		if(this.accepts(syncSession) || this.syncSession == null){
			String error = EpiInfoCompactUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId()));
			this.setError(error);
			
			if(owner != null){
				this.owner.notifyEndSync(true);
			}
//			this.viewSession(null);
		}		
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		if(accepts(syncSession)){
			if(!conflicts.isEmpty()){
				String msg = EpiInfoCompactUITranslator.getMessageSyncFailed();
				this.setError(msg);
			} else {
				String msg = EpiInfoCompactUITranslator.getMessageSyncSuccessfully();
				this.setOk(msg);
			}
			
			if(owner != null){
				this.owner.notifyEndSync(!conflicts.isEmpty());
			}
			
//			this.viewSession(null);
		}		
	}
	
	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		if(accepts(syncSession)){
			String msg = EpiInfoCompactUITranslator.getMessageCancelSyncSuccessfully();
			this.setOk(msg);
			
			if(this.owner != null){
				this.owner.notifyEndCancelSync();
			}
			
//			this.viewSession(null);
		}			
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		if(accepts(syncSession) || this.syncSession == null){
			String error = EpiInfoCompactUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId());
			this.setError(error);
//			this.viewSession(null);
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
			this.updateInOut();
			if(!ACKEndSyncMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType()) && 
					!EndSyncMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
				this.updateSessionStatus();
			}
		}
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		String error = EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceIdResolver.getSourceName(sourceId));
		this.setError(error);
	}

	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		if(this.syncSession != null && this.syncSession.getTarget().getEndpointId().equals(endpointId)){
			this.updateInOut();
		}
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		if(this.syncSession != null && this.syncSession.getTarget().getEndpointId().equals(endpointId)){
			this.updateInOut();
			
			String error = EpiInfoCompactUITranslator.getMessageNotifyReceiveMessageError(endpointId, message);
			this.setError(error);
		}
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		if(this.syncSession != null && this.syncSession.getTarget().getEndpointId().equals(endpointId)){
			this.updateInOut();
		}
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		if(this.syncSession != null && this.syncSession.getTarget().getEndpointId().equals(endpointId)){
			this.updateInOut();
		
			String error = EpiInfoCompactUITranslator.getMessageNotifySendMessageError(endpointId, message);
			this.setError(error);
		}
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		// nothing to do
	}

	
	public void initialize(ISyncSessionViewOwner owner, EpiinfoSourceIdResolver sourceIdResolver, IChannel channel){
		this.owner = owner;
		this.sourceIdResolver = sourceIdResolver;
		this.channel = channel;
	}
	
	private void setStatusText(String text) {
		this.textAreaStatus.setText(text);
		this.textAreaStatus.setToolTipText(text);
	}

	public void setReady(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(null);
		this.imageStatus.setToolTipText(msg);
	}

	public void setInProcess(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(null);
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setOk(String msg){
		this.textAreaStatus.setForeground(Color.BLACK);
		this.setStatusText(msg);
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusOkIcon());
		this.imageStatus.setToolTipText(msg);
	}
	
	public void setError(String error){
		this.textAreaStatus.setForeground(Color.RED);
		this.setStatusText(error);
		this.imageStatus.setIcon(EpiInfoIconManager.getStatusErrorIcon());
		this.imageStatus.setToolTipText(error);
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
		if (isSyncInProcess()) {
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
		    setStatusText(sb.toString());
		}
	}

	public boolean isSyncInProcess() {
		return this.syncSession != null && this.syncSession.isOpen();
	}

	public ISyncSession getSyncSession() {
		return this.syncSession;
	}	
}
