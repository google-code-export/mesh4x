package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.controller.AbstractViewController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.settings.prop.IPropertyManager;


public class SettingsController extends AbstractViewController {

	private IPropertyManager propertyManager;
	public final static String PATH_SOURCE = "SourcePath";
	public final static String PATH_TARGET = "TargetPath";
	public final static String LANGUAGE = "Language";
	
	public final static String CLOUD_ROOT_URI = "SyncServerRootUri";
	public final static String CLOUD_MESH_NAME = "MeshName";
	public final static String CLOUD_DATASET_NAME = "DatasetName";
	
	public final static String USER_NAME_MYSQL = "UserName";
	public final static String USER_PASSWORD_MYSQL = "UserPassword";
	public final static String HOST_NAME_MYSQL = "HostName";
	public final static String PORT_MYSQL = "PortNo";
	public final static String DATABASE_NAME_MYSQL = "DatabaseName";
	
	
	public SettingsController(IPropertyManager propertyManager) {
		super();
		this.propertyManager = propertyManager;
	}

	public void loadSettings(){

		modifySettings(PATH_SOURCE,propertyManager.getProperty(EktooProperties.PATH_SOURCE_DIR));
		modifySettings(PATH_TARGET,propertyManager.getProperty(EktooProperties.PATH_TARGET_DIR));
		modifySettings(LANGUAGE,propertyManager.getProperty(EktooProperties.LANGUAGE));
		
		modifySettings(CLOUD_ROOT_URI,propertyManager.getProperty(EktooProperties.CLOUD_ROOT_URI));
		modifySettings(CLOUD_MESH_NAME,propertyManager.getProperty(EktooProperties.CLOUD_MESH_NAME));
		modifySettings(CLOUD_DATASET_NAME,propertyManager.getProperty(EktooProperties.CLOUD_DATASET_NAME));
		
		try {
			modifySettings(USER_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_NAME_MYSQL));
			modifySettings(USER_PASSWORD_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_PASSWORD_MYSQL));
			modifySettings(HOST_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.HOST_NAME_MYSQL));
			modifySettings(PORT_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.PORT_MYSQL));
			modifySettings(DATABASE_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.DATABASE_NAME_MYSQL));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public void modifySettings(String proName,String value){
		setModelProperty(proName, value);
	}
	
	public void loadDefaultGeneralSettings(){
		setModelProperty(PATH_SOURCE, EktooProperties.PATH_SOURCE_DIR_DEFAULT);
		setModelProperty(PATH_TARGET, EktooProperties.PATH_TARGET_DIR_DEFAULT);
		setModelProperty(LANGUAGE, EktooProperties.LANGUAGE_DEFAULT);
	}
	
	public void loadDefaultCloudSettings(){
		setModelProperty(CLOUD_ROOT_URI, EktooProperties.CLOUD_ROOT_URI_DEFAULT);
		setModelProperty(CLOUD_MESH_NAME, EktooProperties.CLOUD_MESH_NAME_DEFAULT);
		setModelProperty(CLOUD_DATASET_NAME, EktooProperties.CLOUD_DATASET_NAME_DEFAULT);
	}
	
	public void save(){
		for(AbstractModel model : getModels() ){
			if(model instanceof GeneralSettingsModel){
				GeneralSettingsModel  generalSettingsModel = (GeneralSettingsModel)model;
				propertyManager.setProperty(EktooProperties.LANGUAGE, generalSettingsModel.getLanguage());
				propertyManager.setProperty(EktooProperties.PATH_SOURCE_DIR, generalSettingsModel.getSourcePath());
				propertyManager.setProperty(EktooProperties.PATH_TARGET_DIR, generalSettingsModel.getTargetPath());
			} else  if(model instanceof CloudSettingsModel){
				CloudSettingsModel  cloudSettingsModel = (CloudSettingsModel)model;
				propertyManager.setProperty(EktooProperties.CLOUD_ROOT_URI, cloudSettingsModel.getSyncServerRootUri());
				propertyManager.setProperty(EktooProperties.CLOUD_MESH_NAME, cloudSettingsModel.getMeshName());
				propertyManager.setProperty(EktooProperties.CLOUD_DATASET_NAME, cloudSettingsModel.getDatasetName());
			}  else  if(model instanceof MySqlSettingsModel){
				MySqlSettingsModel  mysqlSettingsModel = (MySqlSettingsModel)model;
				try {
					propertyManager.setPropertyAsEncrypted(EktooProperties.USER_NAME_MYSQL, mysqlSettingsModel.getUserName());
					propertyManager.setPropertyAsEncrypted(EktooProperties.USER_PASSWORD_MYSQL, mysqlSettingsModel.getUserPassword());
					propertyManager.setPropertyAsEncrypted(EktooProperties.HOST_NAME_MYSQL, mysqlSettingsModel.getHostName());
					propertyManager.setPropertyAsEncrypted(EktooProperties.PORT_MYSQL, mysqlSettingsModel.getPortNo());
					propertyManager.setPropertyAsEncrypted(EktooProperties.DATABASE_NAME_MYSQL, mysqlSettingsModel.getDatabaseName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}  
		}
		propertyManager.save();
	}
	
	
}
