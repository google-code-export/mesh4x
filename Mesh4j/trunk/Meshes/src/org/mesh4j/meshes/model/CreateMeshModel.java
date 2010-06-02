package org.mesh4j.meshes.model;

import java.util.List;

import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;


public class CreateMeshModel extends AbstractModel {
	
	public static final String NAME_PROPERTY = "name";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String PASSWORD_PROPERTY = "password";
	public static final String PASSWORDCONFIRMATION_PROPERTY = "passwordConfirmation";
	public static final String SCHEDULING_PROPERTY = "scheduling";
	public static final String SYNCMODE_PROPERTY = "syncMode";
	public static final String EPIINFOLOCATION_PROPERTY = "syncMode";
	
	private String name;
	private String description;
	private String password;
	private String passwordConfirmation;
	private SchedulingOption scheduling = SchedulingOption.values()[0];
	private SyncMode syncMode = SyncMode.values()[0];
	private String epiInfoLocation;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		
		firePropertyChange(NAME_PROPERTY, oldName, name);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		String oldDescription = this.description;
		this.description = description;
		
		firePropertyChange(DESCRIPTION_PROPERTY, oldDescription, description);
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		String oldPassword = this.password;
		this.password = password;
		
		firePropertyChange(PASSWORD_PROPERTY, oldPassword, password);
	}
	
	public String getPasswordConfirmation() {
		return password;
	}
	
	public void setPasswordConfirmation(String passwordConfirmation) {
		String oldPasswordConfirmation = this.passwordConfirmation;
		this.passwordConfirmation = passwordConfirmation;
		
		firePropertyChange(PASSWORDCONFIRMATION_PROPERTY, oldPasswordConfirmation, passwordConfirmation);
	}
	
	public SchedulingOption getScheduling() {
		return scheduling;
	}
	
	public void setScheduling(SchedulingOption scheduling) {
		SchedulingOption oldScheduling = this.scheduling;
		this.scheduling = scheduling;
		
		firePropertyChange(SCHEDULING_PROPERTY, oldScheduling, scheduling);
	}
	
	public SyncMode getSyncMode() {
		return syncMode;
	}
	
	public void setSyncMode(SyncMode syncMode) {
		SyncMode oldSyncMode = this.syncMode;
		this.syncMode = syncMode;
		
		firePropertyChange(SYNCMODE_PROPERTY, oldSyncMode, syncMode);
	}
	
	public String getEpiInfoLocation() {
		return epiInfoLocation;
	}
	
	public void setEpiInfoLocation(String epiInfoLocation) {
		String oldEpiInfoLocation = this.epiInfoLocation;
		this.epiInfoLocation = epiInfoLocation;
		
		firePropertyChange(EPIINFOLOCATION_PROPERTY, oldEpiInfoLocation, epiInfoLocation);
	}

	public Mesh toMesh() {
		Mesh mesh = new Mesh();
		
		// Basic properties
		mesh.setName(getName());
		mesh.setDescription(getDescription());
		mesh.setPassword(getPassword());
		
		List<String> tableNames = EpiInfoSyncAdapterFactory.getTableNames(epiInfoLocation);
		for(String tableName : tableNames) {
			// DataSet
			DataSet dataSet = new DataSet();
			dataSet.setType(DataSetType.TABLE);
			dataSet.setName(tableName);
			mesh.getDataSets().add(dataSet);
			
			// Schedule
			Schedule schedule = new Schedule();
			schedule.setSchedulingOption(scheduling);
			schedule.setSyncMode(syncMode);
			dataSet.setSchedule(schedule);
			
			// DataSource
			EpiInfoDataSource dataSource = new EpiInfoDataSource();
			dataSource.setFileName(epiInfoLocation);
			dataSource.setTableName(tableName);
			dataSet.getDataSources().add(dataSource);
		}
		
		return mesh;
	}
}
