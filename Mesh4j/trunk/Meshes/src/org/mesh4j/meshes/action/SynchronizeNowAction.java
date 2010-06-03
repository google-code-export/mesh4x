package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetState;
import org.mesh4j.meshes.sync.SyncManager;

@SuppressWarnings("serial")
public class SynchronizeNowAction extends AbstractAction {

	private final DataSet dataSet;

	public SynchronizeNowAction(DataSet dataSet) {
		super("Synchronize now");
		this.dataSet = dataSet;
		setEnabled(dataSet.getState() != DataSetState.SYNC);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				SyncManager.getInstance().synchronize(dataSet);		
			}
		}).start();
	}

}
