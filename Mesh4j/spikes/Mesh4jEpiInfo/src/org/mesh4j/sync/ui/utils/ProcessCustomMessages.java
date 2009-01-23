package org.mesh4j.sync.ui.utils;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.ui.tasks.ReadyToSyncResponseTask;
import org.mesh4j.sync.ui.tasks.ReadyToSyncTask;
import org.mesh4j.sync.ui.tasks.TestPhoneTask;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;

public class ProcessCustomMessages implements ISmsReceiver {

	private final static IFilter<String> FILTER = new IFilter<String>(){		
		@Override public boolean applies(String message) {
			return ReadyToSyncTask.isQuestion(message) || ReadyToSyncTask.isAnswer(message) || TestPhoneTask.isQuestion(message);
		}			
	};	
	
	// MODEL VARIABLES
	private MeshCompactUI ui;

	private boolean readyToSyncInProcess = false;
	private EndpointMapping readyToSyncEndpoint;
	private DataSourceMapping readyToSyncDataSource;
	
	private boolean phoneCompatibilityInProcess = false;
	private EndpointMapping phoneCompatibilityEndpoint;
	private String phoneCompatibilityId;
	
	
	public ProcessCustomMessages(MeshCompactUI ui) {

		super();
		this.ui = ui;
		
		((ISmsChannel)this.ui.getSyncEngine().getChannel()).getSmsConnection().registerMessageReceiver(FILTER, this);
	}


	// MODEL VARIABLES
	@Override
	public void receiveSms(SmsEndpoint endpoint, String message, Date date) {
		if(ReadyToSyncTask.isQuestion(message)){
			String dataSourceAlias = ReadyToSyncTask.getDataSourceAlias(message);
			
			boolean isDataSourceAvailable = ui.getSourceIdResolver().isDataSourceAvailable(dataSourceAlias);
			ReadyToSyncResponseTask responseTask = new ReadyToSyncResponseTask(ui, endpoint.getEndpointId(), dataSourceAlias, isDataSourceAvailable);
			responseTask.execute();
		}
		
		if(this.readyToSyncInProcess 
				&& this.readyToSyncEndpoint.getEndpoint().equals(endpoint.getEndpointId())){
			if(ReadyToSyncTask.isAnswerOk(message, this.readyToSyncDataSource.getAlias())){
				notifyEndpointIsReadyToSync();
			}
			
			if(ReadyToSyncTask.isAnswerNotOk(message, this.readyToSyncDataSource.getAlias())){
				notifyEndpointIsNotReadyToSync();
			}
		} 

		if(this.phoneCompatibilityInProcess 
				&& this.phoneCompatibilityEndpoint.getEndpoint().equals(endpoint.getEndpointId()) 
				&& TestPhoneTask.makeAnswer(this.phoneCompatibilityId).equals(message)){
			
			this.resetPhoneCompatibility();			
			ui.getSyncSessionView().setReady(MeshCompactUITranslator.getMessagePhoneIsCompatible());
			ui.fullEnableAllButtons();
			ui.notifyOwnerNotWorking();
		}		

	}

	public void resetPhoneCompatibility() {
		phoneCompatibilityInProcess = false;
		phoneCompatibilityEndpoint = null;
		phoneCompatibilityId = null;
		
	}


	public boolean isPhoneCompatibilityInProcess() {
		return phoneCompatibilityInProcess;
	}
	
	public void notifyStartTestForPhoneCompatibility(EndpointMapping endpoint, String id){
		this.phoneCompatibilityInProcess = true;
		this.phoneCompatibilityEndpoint = endpoint;
		this.phoneCompatibilityId = id;	
		
		ui.fullDisableAllButtons();
		
		String msg = MeshCompactUITranslator.getMessageTestingPhoneCompatibility();
		ui.getSyncSessionView().setInProcess(msg);
		ui.notifyOwnerWorking();
		
		Action errorAction = new AbstractAction(){

			private static final long serialVersionUID = 4028395273128514170L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isPhoneCompatibilityInProcess()){
					resetPhoneCompatibility();
					
					String msg = MeshCompactUITranslator.getMessageTimeOutPhoneCompatibility();
					ui.getSyncSessionView().setError(msg);
					
					ui.fullEnableAllButtons();
					
					ui.notifyOwnerNotWorking();
				}
			}
		};
		new Timer(ui.getPropertiesProvider().getDefaultTestPhoneDelay(), errorAction).start();
	}
	
	public void notifyStartReadyToSync(EndpointMapping endpoint, DataSourceMapping dataSource){

		this.readyToSyncInProcess = true;
		this.readyToSyncEndpoint = endpoint;
		this.readyToSyncDataSource = dataSource;

		ui.fullDisableAllButtons();

		String msg = MeshCompactUITranslator.getMessageProcessingReadyToSync(endpoint.getAlias(), dataSource.getAlias());
		ui.getSyncSessionView().setInProcess(msg);
		
		ui.notifyOwnerWorking();
		
		Action errorReadyToSync = new AbstractAction(){
			private static final long serialVersionUID = 4028395273128514170L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(readyToSyncInProcess){
					notifyEndpointIsNotReadyToSync();
				}
			}
		};
		new Timer(ui.getPropertiesProvider().getDefaultReadyToSyncDelay(), errorReadyToSync).start();
	}
	
	public void notifyEndpointIsReadyToSync(){
		this.readyToSyncInProcess = false;
		
		ui.getSyncSessionView().setReady(MeshCompactUITranslator.getMessageEndpointIsReadyToSync(readyToSyncEndpoint.getAlias(), readyToSyncDataSource.getAlias()));
		this.readyToSyncEndpoint = null;
		this.readyToSyncDataSource = null;
		ui.fullEnableAllButtons();
		
		ui.notifyOwnerNotWorking();
	}
	
	public void notifyEndpointIsNotReadyToSync(){
		readyToSyncInProcess = false;
		
		String msg = MeshCompactUITranslator.getMessageEndpointIsNotReadyToSync(readyToSyncEndpoint.getAlias(), readyToSyncDataSource.getAlias());
		ui.getSyncSessionView().setError(msg);
		readyToSyncEndpoint = null;
		readyToSyncDataSource = null;
		ui.fullEnableAllButtons();
		
		ui.notifyOwnerNotWorking();
	}

}