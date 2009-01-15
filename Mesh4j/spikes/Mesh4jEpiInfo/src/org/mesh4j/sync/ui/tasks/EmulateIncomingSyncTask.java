package org.mesh4j.sync.ui.tasks;

import javax.swing.SwingWorker;

import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;

public class EmulateIncomingSyncTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private EpiinfoCompactUI ui;
	
	// BUSINESS METHODS
	public EmulateIncomingSyncTask(EpiinfoCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		DataSourceMapping dataSource = (DataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
		SyncMode syncMode = (SyncMode)ui.getComboBoxSyncMode().getSelectedItem();

		SmsChannel foregroundChannel = (SmsChannel)ui.getSyncEngine().getChannel();
		InMemorySmsConnection smsConnection = (InMemorySmsConnection) foregroundChannel.getSmsConnection(); 
		InMemorySmsConnection smsConnectionEndpoint = smsConnection.getEndpoint(endpoint.getEndpoint());
		SmsReceiver smsReceiverEndpoint = (SmsReceiver)smsConnectionEndpoint.getMessageReceiver();
		SmsChannel channelEndpoint = (SmsChannel)smsReceiverEndpoint.getSmsBatchReceiver();
		MessageSyncEngine messageSyncEngineEndpoint = (MessageSyncEngine) channelEndpoint.getMessageReceiver();
		
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(dataSource.getAlias());
		IMessageSyncAdapter adapter = messageSyncEngineEndpoint.getSource(sourceID);
		SmsEndpoint target = new SmsEndpoint(EpiInfoUITranslator.getLabelDemo());
		messageSyncEngineEndpoint.synchronize(adapter, target, true, syncMode.shouldSendChanges(), syncMode.shouldReceiveChanges());
		return null;
    }
}
