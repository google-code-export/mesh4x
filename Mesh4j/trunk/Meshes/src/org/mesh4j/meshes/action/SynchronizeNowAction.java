package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.sync.SyncManager;

@SuppressWarnings("serial")
public class SynchronizeNowAction extends AbstractAction {

	private final DataSource dataSource;

	public SynchronizeNowAction(DataSource dataSource) {
		super("Synchronize now");
		this.dataSource = dataSource;
		//setEnabled(dataSource.getState() != DataSetState.SYNC);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SyncManager.getInstance().synchronize(dataSource);		
			}
		}).start();
	}

}
