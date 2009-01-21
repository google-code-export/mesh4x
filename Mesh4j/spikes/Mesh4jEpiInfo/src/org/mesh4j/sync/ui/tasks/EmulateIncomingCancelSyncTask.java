package org.mesh4j.sync.ui.tasks;

import javax.swing.SwingWorker;

import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;

public class EmulateIncomingCancelSyncTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private EpiinfoCompactUI ui;
	
	// BUSINESS METHODS
	public EmulateIncomingCancelSyncTask(EpiinfoCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
    	
		DataSourceMapping dataSource = (DataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();

		SmsChannel foregroundChannel = (SmsChannel)ui.getSyncEngine().getChannel();
		InMemorySmsConnection smsConnection = (InMemorySmsConnection) foregroundChannel.getSmsConnection(); 
		InMemorySmsConnection smsConnectionEndpoint = smsConnection.getEndpoint(endpoint.getEndpoint());
		SmsReceiver smsReceiverEndpoint = (SmsReceiver)smsConnectionEndpoint.getMessageReceiver();
		SmsChannel channelEndpoint = (SmsChannel)smsReceiverEndpoint.getSmsBatchReceiver();
		MessageSyncEngine messageSyncEngineEndpoint = (MessageSyncEngine) channelEndpoint.getMessageReceiver();
		
		String sourceID = dataSource.getSourceId();
		SmsEndpoint target = new SmsEndpoint(EpiInfoUITranslator.getLabelDemo());
		messageSyncEngineEndpoint.cancelSync(sourceID, target);
		return null;
    }
}
