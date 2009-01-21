package org.mesh4j.sync.epiinfo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.utils.EpiinfoSourceIdResolver;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.swtdesigner.SwingResourceManager;

public class LogFrame extends JFrame implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware {
	
	private static final long serialVersionUID = -5672081373978129329L;
	
	private final static Log Logger = LogFactory.getLog(LogFrame.class);

	// MODEL VARIABLES
	private JTextArea textAreaConsoleView;
	private EpiinfoSourceIdResolver sourceIdResolver;
	
	// BUSINESS METHODS

	public LogFrame(EpiinfoSourceIdResolver sourceIdResolver) {
		super();
		
		this.sourceIdResolver = sourceIdResolver;
		
		setIconImage(SwingResourceManager.getImage(LogFrame.class, "/cdc.gif"));
		getContentPane().setBackground(Color.WHITE);
		setTitle(EpiInfoCompactUITranslator.getLogWindowTitle());
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
		textAreaConsoleView.setToolTipText(EpiInfoCompactUITranslator.getLogWindowToolTipConsoleView());
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
		buttonClean.setText(EpiInfoCompactUITranslator.getLogWindowLabelClean());
		buttonClean.setToolTipText(EpiInfoCompactUITranslator.getLogWindowToolTipClean());
		buttonClean.addActionListener(cleanActionListener);

		final JButton buttonClose = new JButton();
		buttonClose.setOpaque(true);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setContentAreaFilled(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(EpiInfoCompactUITranslator.getLogWindowLabelClose());
		buttonClose.setToolTipText(EpiInfoCompactUITranslator.getLogWindowToolTipClose());
		
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
	
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		String error = EpiInfoUITranslator.getMessageNotifyReceiveMessageError(endpointId, message);
		this.log("\t"+error);
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		String error = EpiInfoUITranslator.getMessageNotifySendMessageError(endpointId, message);
		this.log("\t"+error);
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
	}

	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		this.log(EpiInfoUITranslator.getLabelStart());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId(), conflicts);
	}
	
	public void endSync(String target, String sourceId, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			this.log(EpiInfoUITranslator.getLabelSuccess());
		} else {
			this.log(EpiInfoUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		String error = EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId()));
		this.log(error);		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		this.log(EpiInfoUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId())));
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		String error = EpiInfoUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId());
		this.log(error);		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		this.log(EpiInfoUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		this.log(EpiInfoUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		this.log(EpiInfoUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		this.log(EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceIdResolver.getSourceName(sourceId)));
	}
}
