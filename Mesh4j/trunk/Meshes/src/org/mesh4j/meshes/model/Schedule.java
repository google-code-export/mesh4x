package org.mesh4j.meshes.model;

public class Schedule extends AbstractModel {
	
	public static final String SCHEDULING_OPTION_PROPERTY = "schedule_schedulingoption";
	public static final String SYNC_MODE_PROPERTY = "schedule_syncmode";
	
	private SchedulingOption schedulingOption;
	private SyncMode syncMode;
	
	public SchedulingOption getSchedulingOption() {
		return schedulingOption;
	}
	
	public void setSchedulingOption(SchedulingOption schedulingOption) {
		SchedulingOption oldSchedulingOption = this.schedulingOption;
		this.schedulingOption = schedulingOption;
		firePropertyChange(SCHEDULING_OPTION_PROPERTY, oldSchedulingOption, schedulingOption);
	}
	
	public SyncMode getSyncMode() {
		return syncMode;
	}
	
	public void setSyncMode(SyncMode syncMode) {
		SyncMode oldSyncMode = this.syncMode;
		this.syncMode = syncMode;
		firePropertyChange(SYNC_MODE_PROPERTY, oldSyncMode, syncMode);
	}
	
}
