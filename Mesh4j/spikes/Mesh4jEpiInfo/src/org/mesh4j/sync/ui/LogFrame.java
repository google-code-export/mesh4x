package org.mesh4j.sync.ui;

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
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.utils.SourceIdResolver;

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
	private SourceIdResolver sourceIdResolver;
	
	// BUSINESS METHODS

	public LogFrame(SourceIdResolver sourceIdResolver) {
		super();
		
		this.sourceIdResolver = sourceIdResolver;
		
		setIconImage(SwingResourceManager.getImage(LogFrame.class, "/cdc.gif"));
		getContentPane().setBackground(Color.WHITE);
		setTitle(MeshCompactUITranslator.getLogWindowTitle());
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
		textAreaConsoleView.setToolTipText(MeshCompactUITranslator.getLogWindowToolTipConsoleView());
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
		buttonClean.setText(MeshCompactUITranslator.getLogWindowLabelClean());
		buttonClean.setToolTipText(MeshCompactUITranslator.getLogWindowToolTipClean());
		buttonClean.addActionListener(cleanActionListener);

		final JButton buttonClose = new JButton();
		buttonClose.setOpaque(true);
		buttonClose.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonClose.setBorderPainted(false);
		buttonClose.setContentAreaFilled(false);
		buttonClose.setFont(new Font("Calibri", Font.BOLD, 12));
		buttonClose.setText(MeshCompactUITranslator.getLogWindowLabelClose());
		buttonClose.setToolTipText(MeshCompactUITranslator.getLogWindowToolTipClose());
		
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
		this.log("\t"+MeshUITranslator.getMessageNotifyReceiveMessage(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		String error = MeshUITranslator.getMessageNotifyReceiveMessageError(endpointId, message);
		this.log("\t"+error);
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+MeshUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		String error = MeshUITranslator.getMessageNotifySendMessageError(endpointId, message);
		this.log("\t"+error);
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
	}

	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		this.log(MeshUITranslator.getLabelStart());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId(), conflicts);
	}
	
	public void endSync(String target, String sourceId, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			this.log(MeshUITranslator.getLabelSuccess());
		} else {
			this.log(MeshUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		String error = MeshUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId()));
		this.log(error);		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		this.log(MeshUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId())));
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		String error = MeshUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId());
		this.log(error);		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		this.log(MeshUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		this.log(MeshUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		this.log(MeshUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		this.log(MeshUITranslator.getMessageErrorSessionCreation(message, sourceIdResolver.getSourceName(sourceId)));
	}
}
