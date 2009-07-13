package org.mesh4j.ektoo.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.mesh4j.ektoo.ui.image.ImageManager;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.multimode.ISyncProcessListener;
import org.mesh4j.sync.adapters.multimode.SyncProcess;
import org.mesh4j.sync.adapters.multimode.SyncStatus;
import org.mesh4j.sync.adapters.multimode.SyncTask;
import org.mesh4j.sync.security.IIdentityProvider;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SyncProcessUI extends JFrame implements ISyncProcessListener{

	private static final long serialVersionUID = -4054400437647554634L;

	// MODEL VARIABLES
	private Map<String, String> dataSources = new HashMap<String, String>();
	private JLabel labelStatus;
	private SyncStatus status;
	
	// BUSINESS METHODS
	
	public void synchronizeMsAccessVsCloud(final Date sinceDate, final String fileName, final String serverURL, final String meshGroup, final IIdentityProvider identityProvider, final String baseDirectory) {
//		SwingWorker<Void, Void> task = new SwingWorker<Void, Void>(){
//
//			@Override
//			protected Void doInBackground() throws Exception {
				dataSources = new HashMap<String, String>();
				SyncProcess syncProcess = SyncProcess.makeSyncProcessForSyncMsAccessVsHttp(fileName, serverURL, meshGroup, identityProvider, baseDirectory, SyncProcessUI.this);
				if(syncProcess.getStatus().isReadyToSync()){
					syncProcess.synchronize(sinceDate);
				}
				status = syncProcess.getStatus();
//				return null;
//			}
//		};
//		
//		task.execute();
	}
	
	public void synchronizeMsAccessVsCloud(final Date sinceDate, final String fileName, final Set<String> tableNames, final String serverURL, final String meshGroup, final IIdentityProvider identityProvider, final String baseDirectory) {
//		SwingWorker<Void, Void> task = new SwingWorker<Void, Void>(){
//
//			@Override
//			protected Void doInBackground() throws Exception {
				dataSources = new HashMap<String, String>();
				SyncProcess syncProcess = SyncProcess.makeSyncProcessForSyncMsAccessVsHttp(fileName, tableNames, serverURL, meshGroup, identityProvider, baseDirectory, SyncProcessUI.this);
				if(syncProcess.getStatus().isReadyToSync()){
					syncProcess.synchronize(sinceDate);
				}
				status = syncProcess.getStatus();
//				return null;
//			}
//		};
//		
//		task.execute();
	}
	
	public void synchronizeMySqlVsCloud(final Date sinceDate, final String user, final String password, final String hostName, final int portNo, final String databaseName, final Set<String> tables, final String serverURL, final String meshGroup, final IIdentityProvider identityProvider, final String baseDirectory) {
//		SwingWorker<Void, Void> task = new SwingWorker<Void, Void>(){
//
//			@Override
//			protected Void doInBackground() throws Exception {
				dataSources = new HashMap<String, String>();
				SyncProcess syncProcess = SyncProcess.makeSyncProcessForSyncMySqlVsHttp(user, password, hostName, portNo, databaseName, tables, serverURL, meshGroup, identityProvider, baseDirectory, SyncProcessUI.this);
				if(syncProcess.getStatus().isReadyToSync()){
					syncProcess.synchronize(sinceDate);
				}
				status = syncProcess.getStatus();
//				return null;
//			}
//		};
//		
//		task.execute();
	}

	public SyncProcessUI() {
		super();
		this.setIconImage(ImageManager.getLogoSmall());
		this.setTitle(EktooUITranslator.getSyncProcessTitle());
		setResizable(false);
		
		setBounds(100, 100, 404, 477);
		getContentPane().setLayout(new FormLayout(
			new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("191dlu"),
				FormFactory.RELATED_GAP_COLSPEC},
			new RowSpec[] {
				RowSpec.decode("273dlu"),
				FormFactory.RELATED_GAP_ROWSPEC}));

		final JPanel panel = new JPanel();
		//panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.setLayout(new FormLayout(
			"191dlu",
			"265dlu"));
		getContentPane().add(panel, new CellConstraints(2, 1, CellConstraints.FILL, CellConstraints.TOP));
		
		labelStatus = new JLabel();
		labelStatus.setHorizontalAlignment(SwingConstants.LEFT);
		labelStatus.setAutoscrolls(true);
		panel.add(labelStatus, new CellConstraints(1, 1, CellConstraints.LEFT, CellConstraints.TOP));

	}

	// TODO (RAJU) add resource bundles
	@Override
	public void syncProcessChangeStatusNotification(SyncTask syncTask) {
		if(syncTask.isReadyToSync()){
			this.dataSources.put(syncTask.getDataSource(), "<font color=gray>ready to sync</font>");
		}else if(syncTask.isSuccessfully()){
			this.dataSources.put(syncTask.getDataSource(), "<font color=green>successfully</font>");
		}else if(syncTask.isFailed()){
			this.dataSources.put(syncTask.getDataSource(), "<font color=yellow>failed (has conflicts)</font>");
		}else if(syncTask.isError()){
			this.dataSources.put(syncTask.getDataSource(), "<font color=red>unexpected error</font>");
		} else{
			this.dataSources.put(syncTask.getDataSource(), "<font color=blue>synchronizing</font>");
		}
		this.showDataSources();
	}

	@Override
	public void notifyCreatingCloudSyncAdapter(String dataSource, String url) {
		this.dataSources.put(dataSource, "<font color=blue>creating Http sync adapter</font>");
		this.showDataSources();
	}

	@Override
	public void notifyCreatingMsAccessSyncAdapter(String dataSource) {
		this.dataSources.put(dataSource, "<font color=blue>creating MsAccess sync adapter</font>");
		this.showDataSources();
	}

	@Override
	public void syncProcessDataSourcesNotification(Set<String> dataSources) {
		for (String dataSource : dataSources) {
			this.dataSources.put(dataSource, "&nbsp;");
		}
		this.showDataSources();
	}

	@Override
	public void notifyErrorReadingMsAccessTables() {
	}

	@Override
	public void notifyErrorCreatingHttpAdapter(String dataSource) {
		this.dataSources.put(dataSource, "<font color=red>error creating Http sync adapter</font>");
		this.showDataSources();
		
	}

	@Override
	public void notifyErrorCreatingMsAccessAdapter(String dataSource) {
		this.dataSources.put(dataSource, "<font color=red>error creating MsAccess sync adapter</font>");
		this.showDataSources();
	}

	private void showDataSources() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>\n\n");
		sb.append("<table>\n");
		
		for (String dataSource : this.dataSources.keySet()) {
			sb.append("<tr><td>&nbsp;&nbsp;</td><td>"+dataSource+"</td><td>&nbsp;&nbsp;"+this.dataSources.get(dataSource)+"</td>\n");
		}
		
		sb.append("</table>\n");
		this.labelStatus.setText(sb.toString());
		this.labelStatus.repaint();		
	}

	@Override
	public void notifyCreatingMySqlSyncAdapter(String dataSource) {
		this.dataSources.put(dataSource, "<font color=blue>creating MySQL sync adapter</font>");
		this.showDataSources();
	}

	@Override
	public void notifyErrorCreatingMySqlAdapter(String dataSource) {
		this.dataSources.put(dataSource, "<font color=red>error creating MySQL sync adapter</font>");
		this.showDataSources();
	}

	public SyncStatus getStatus(){
		return status;
	}
}
