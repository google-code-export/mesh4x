package org.mesh4j.ektoo.ui.settings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Util;
import org.mesh4j.ektoo.controller.AbstractViewController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.settings.prop.IPropertyManager;


public class SettingsController extends AbstractViewController {

	private static final  Log LOGGER = LogFactory.getLog(SettingsController.class);
	
	private IPropertyManager propertyManager;
	
	public final static String PATH_SOURCE = "PathSource";
	public final static String PATH_TARGET = "PathTarget";
	public final static String LANGUAGE = "Language";
	
	public final static String PATH_SOURCE_EXCEL = "PathSourceExcel";
	public final static String PATH_TARGET_EXCEL = "PathTargetExcel";
	
	public final static String PATH_SOURCE_ACCESS = "PathSourceAccess";
	public final static String PATH_TARGET_ACCESS = "PathTargetAccess";
	
	public final static String PATH_SOURCE_KML = "PathSourceKml";
	public final static String PATH_TARGET_KML = "PathTargetKml";
	
	public final static String PATH_SOURCE_RSS = "PathSourceRss";
	public final static String PATH_TARGET_RSS = "PathTargetRss";
	
	public final static String PATH_SOURCE_ATOM = "PathSourceAtom";
	public final static String PATH_TARGET_ATOM = "PathTargetAtom";
	
	public final static String PATH_SOURCE_FOLDER = "PathSourceFolder";
	public final static String PATH_TARGET_FOLDER = "PathTargetFolder";
	
	public final static String PATH_SOURCE_ZIP = "PathSourceZip";
	
	public final static String CLOUD_ROOT_URI = "SyncServerRootUri";
	public final static String CLOUD_MESH_NAME = "MeshName";
	public final static String CLOUD_DATASET_NAME = "DatasetName";
	
	public final static String USER_NAME_MYSQL = "UserName";
	public final static String USER_PASSWORD_MYSQL = "UserPassword";
	public final static String HOST_NAME_MYSQL = "HostName";
	public final static String PORT_MYSQL = "PortNo";
	public final static String DATABASE_NAME_MYSQL = "DatabaseName";
	
	
	public final static String USER_NAME_GOOGLE = "GUserName";
	public final static String USER_PASSWORD_GOOGLE = "GPassword";
	
	
	
	public SettingsController(IPropertyManager propertyManager) {
		super();
		this.propertyManager = propertyManager;
	}

	public void loadSettings(){
		loadGeneralSettings();
		loadMysqlSettings();
		loadCloudSettings();
		loadGoogleSettings();
	}
	
	private void loadGoogleSettings(){
		try {
			modifySettings(USER_NAME_GOOGLE,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_NAME_GOOGLE));
			modifySettings(USER_PASSWORD_GOOGLE,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_PASSWORD_GOOGLE));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	private void loadCloudSettings(){
		modifySettings(CLOUD_ROOT_URI,propertyManager.getProperty(EktooProperties.CLOUD_ROOT_URI));
		modifySettings(CLOUD_MESH_NAME,propertyManager.getProperty(EktooProperties.CLOUD_MESH_NAME));
		modifySettings(CLOUD_DATASET_NAME,propertyManager.getProperty(EktooProperties.CLOUD_DATASET_NAME));
	}
	
	private void loadMysqlSettings(){
		try {
			modifySettings(USER_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_NAME_MYSQL));
			modifySettings(USER_PASSWORD_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.USER_PASSWORD_MYSQL));
			modifySettings(HOST_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.HOST_NAME_MYSQL));
			modifySettings(PORT_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.PORT_MYSQL));
			modifySettings(DATABASE_NAME_MYSQL,propertyManager.getPropertyAsDecrepted(EktooProperties.DATABASE_NAME_MYSQL));
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	private void loadGeneralSettings(){
		
		modifySettings(PATH_SOURCE,propertyManager.getProperty(EktooProperties.PATH_SOURCE_DIR));
		modifySettings(PATH_TARGET,propertyManager.getProperty(EktooProperties.PATH_TARGET_DIR));
		
		modifySettings(LANGUAGE,propertyManager.getProperty(EktooProperties.LANGUAGE));
		
		modifySettings(PATH_SOURCE_EXCEL, propertyManager.getProperty(EktooProperties.PATH_SOURCE_EXCEL));
		modifySettings(PATH_TARGET_EXCEL, propertyManager.getProperty(EktooProperties.PATH_TARGET_EXCEL));
		
		modifySettings(PATH_SOURCE_ACCESS, propertyManager.getProperty(EktooProperties.PATH_SOURCE_ACCESS));
		modifySettings(PATH_TARGET_ACCESS, propertyManager.getProperty(EktooProperties.PATH_TARGET_ACCESS));
		
		modifySettings(PATH_SOURCE_KML, propertyManager.getProperty(EktooProperties.PATH_SOURCE_KML));
		modifySettings(PATH_TARGET_KML, propertyManager.getProperty(EktooProperties.PATH_TARGET_KML));
		
		modifySettings(PATH_SOURCE_RSS, propertyManager.getProperty(EktooProperties.PATH_SOURCE_RSS));
		modifySettings(PATH_TARGET_RSS, propertyManager.getProperty(EktooProperties.PATH_TARGET_RSS));
		
		modifySettings(PATH_SOURCE_ATOM, propertyManager.getProperty(EktooProperties.PATH_SOURCE_ATOM));
		modifySettings(PATH_TARGET_ATOM, propertyManager.getProperty(EktooProperties.PATH_TARGET_ATOM));
		
		modifySettings(PATH_SOURCE_FOLDER, propertyManager.getProperty(EktooProperties.PATH_SOURCE_FOLDER));
		modifySettings(PATH_TARGET_FOLDER, propertyManager.getProperty(EktooProperties.PATH_TARGET_FOLDER));
		
		modifySettings(PATH_SOURCE_ZIP, propertyManager.getProperty(EktooProperties.PATH_SOURCE_ZIP));
		
		modifySettings(CLOUD_ROOT_URI,propertyManager.getProperty(EktooProperties.CLOUD_ROOT_URI));
		modifySettings(CLOUD_MESH_NAME,propertyManager.getProperty(EktooProperties.CLOUD_MESH_NAME));
		modifySettings(CLOUD_DATASET_NAME,propertyManager.getProperty(EktooProperties.CLOUD_DATASET_NAME));
	}
	
	
	public void modifySettings(String proName,String value){
		setModelProperty(proName, value);
	}
	
	public void loadDefaultGeneralSettings(){
		
		setModelProperty(PATH_SOURCE, EktooProperties.PATH_SOURCE_DIR_DEFAULT);
		setModelProperty(PATH_TARGET, EktooProperties.PATH_TARGET_DIR_DEFAULT);
		
		setModelProperty(LANGUAGE, EktooProperties.LANGUAGE_DEFAULT);
		
		String fileName = "";
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_EXCEL_DEFAULT);
		setModelProperty(PATH_SOURCE_EXCEL, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_EXCEL_DEFAULT);
		setModelProperty(PATH_TARGET_EXCEL, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ACCESS_DEFAULT);
		setModelProperty(PATH_SOURCE_ACCESS, EktooProperties.PATH_SOURCE_ACCESS);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_ACCESS_DEFAULT);
		setModelProperty(PATH_TARGET_ACCESS, EktooProperties.PATH_TARGET_ACCESS);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_KML_DEFAULT);
		setModelProperty(PATH_SOURCE_KML, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_KML_DEFAULT);
		setModelProperty(PATH_TARGET_KML, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_RSS_DEFAULT);
		setModelProperty(PATH_SOURCE_RSS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_RSS_DEFAULT);
		setModelProperty(PATH_TARGET_RSS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ATOM_DEFAULT);
		setModelProperty(PATH_SOURCE_ATOM, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_ATOM_DEFAULT);
		setModelProperty(PATH_TARGET_ATOM, fileName);
		
		setModelProperty(PATH_SOURCE_FOLDER, EktooProperties.PATH_SOURCE_FOLDER);
		setModelProperty(PATH_TARGET_FOLDER, EktooProperties.PATH_TARGET_FOLDER);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ZIP);
		setModelProperty(PATH_SOURCE_ZIP, fileName);
		
	}
	
	public void loadDefaultCloudSettings(){
		setModelProperty(CLOUD_ROOT_URI, EktooProperties.CLOUD_ROOT_URI_DEFAULT);
		setModelProperty(CLOUD_MESH_NAME, EktooProperties.CLOUD_MESH_NAME_DEFAULT);
		setModelProperty(CLOUD_DATASET_NAME, EktooProperties.CLOUD_DATASET_NAME_DEFAULT);
	}
	
	public void save(){
		for( AbstractModel model : getModels() ){
			if ( model instanceof GeneralSettingsModel ){
					GeneralSettingsModel  generalSettingsModel = (GeneralSettingsModel)model;
					saveGeneralSettings(generalSettingsModel);
			} else if ( model instanceof CloudSettingsModel ){
					CloudSettingsModel  cloudSettingsModel = (CloudSettingsModel)model;
					saveCloudSettings(cloudSettingsModel);
			} else if ( model instanceof MySqlSettingsModel ){
					MySqlSettingsModel  mysqlSettingsModel = (MySqlSettingsModel)model;
					saveMysqlSettings(mysqlSettingsModel);
			}  
		}
		propertyManager.save();
	}
	
	
	private void saveGeneralSettings(GeneralSettingsModel  generalSettingsModel){
		propertyManager.setProperty(EktooProperties.LANGUAGE, generalSettingsModel.getLanguage());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_DIR, generalSettingsModel.getPathSource());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_DIR, generalSettingsModel.getPathTarget());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_EXCEL, generalSettingsModel.getPathSourceExcel());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_EXCEL, generalSettingsModel.getPathTargetExcel());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ACCESS, generalSettingsModel.getPathSourceAccess());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_ACCESS, generalSettingsModel.getPathTargetAccess());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_KML, generalSettingsModel.getPathSourceKml());
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_KML, generalSettingsModel.getPathTargetKml());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_RSS, generalSettingsModel.getPathSourceRss());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_RSS, generalSettingsModel.getPathTargetRss());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ATOM, generalSettingsModel.getPathSourceAtom());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_ATOM, generalSettingsModel.getPathTargetAtom());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_FOLDER, generalSettingsModel.getPathSourceFolder());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_FOLDER, generalSettingsModel.getPathTargetFolder());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ZIP, generalSettingsModel.getPathSourceZip());
		
	}
	
	private void saveCloudSettings(CloudSettingsModel  cloudSettingsModel){
		propertyManager.setProperty(EktooProperties.CLOUD_ROOT_URI, cloudSettingsModel.getSyncServerRootUri());
		propertyManager.setProperty(EktooProperties.CLOUD_MESH_NAME, cloudSettingsModel.getMeshName());
		propertyManager.setProperty(EktooProperties.CLOUD_DATASET_NAME, cloudSettingsModel.getDatasetName());
	}
	
	private void saveMysqlSettings(MySqlSettingsModel  mysqlSettingsModel){
		try {
			propertyManager.setPropertyAsEncrypted(EktooProperties.USER_NAME_MYSQL, mysqlSettingsModel.getUserName());
			propertyManager.setPropertyAsEncrypted(EktooProperties.USER_PASSWORD_MYSQL, mysqlSettingsModel.getUserPassword());
			propertyManager.setPropertyAsEncrypted(EktooProperties.HOST_NAME_MYSQL, mysqlSettingsModel.getHostName());
			propertyManager.setPropertyAsEncrypted(EktooProperties.PORT_MYSQL, mysqlSettingsModel.getPortNo());
			propertyManager.setPropertyAsEncrypted(EktooProperties.DATABASE_NAME_MYSQL, mysqlSettingsModel.getDatabaseName());
		} catch (Exception e) {
			LOGGER.debug(e);
		}
	}
}
