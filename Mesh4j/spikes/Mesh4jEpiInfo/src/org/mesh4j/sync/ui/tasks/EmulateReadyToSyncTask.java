package org.mesh4j.sync.ui.tasks;

import javax.swing.SwingWorker;

import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;

public class EmulateReadyToSyncTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private EpiinfoCompactUI ui;
	
	// BUSINESS METHODS
	public EmulateReadyToSyncTask(EpiinfoCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		DataSourceMapping dataSource = (DataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();

		SmsChannel foregroundChannel = (SmsChannel)ui.getSyncEngine().getChannel();
		InMemorySmsConnection smsConnection = (InMemorySmsConnection) foregroundChannel.getSmsConnection();
		
		String message = ReadyToSyncTask.makeQuestion(dataSource.getAlias());
		smsConnection.receive(message, new SmsEndpoint(endpoint.getEndpoint()));
		return null;
    }
}
