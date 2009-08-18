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
	
	public final static String CREATE_PROP_AS_DEFAULT = "CreateAsDefaultProp";
	
	
	
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
			e.printStackTrace();
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
	
	
	public void modifySettings(String proName,Object value){
		setModelProperty(proName, value);
	}
	
	
	public void loadDefaultGoogleSettings(){
		try{
			modifySettings(USER_NAME_GOOGLE, propertyManager.getPropertyAsDecrepted(EktooProperties.USER_NAME_GOOGLE_DEFAULT));
			modifySettings(USER_PASSWORD_GOOGLE, propertyManager.getPropertyAsDecrepted(EktooProperties.USER_PASSWORD_GOOGLE_DEFAULT));
		} catch (Exception e){
			LOGGER.error(e);
		}
	}

	
	public void loadDefaultGeneralSettings(){
		
		modifySettings(PATH_SOURCE, propertyManager.getProperty(EktooProperties.PATH_SOURCE_DIR_DEFAULT));
		modifySettings(PATH_TARGET, propertyManager.getProperty(EktooProperties.PATH_TARGET_DIR_DEFAULT));
		
		modifySettings(LANGUAGE, EktooProperties.LANGUAGE_DEFAULT);
		
		String fileName = "";
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_EXCEL_DEFAULT);
		modifySettings(PATH_SOURCE_EXCEL, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_EXCEL_DEFAULT);
		modifySettings(PATH_TARGET_EXCEL, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ACCESS_DEFAULT);
		modifySettings(PATH_SOURCE_ACCESS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_ACCESS_DEFAULT);
		modifySettings(PATH_TARGET_ACCESS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_KML_DEFAULT);
		modifySettings(PATH_SOURCE_KML, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_KML_DEFAULT);
		modifySettings(PATH_TARGET_KML, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_RSS_DEFAULT);
		modifySettings(PATH_SOURCE_RSS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_RSS_DEFAULT);
		modifySettings(PATH_TARGET_RSS, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ATOM_DEFAULT);
		modifySettings(PATH_SOURCE_ATOM, fileName);
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_TARGET_ATOM_DEFAULT);
		modifySettings(PATH_TARGET_ATOM, fileName);
		
		modifySettings(PATH_SOURCE_FOLDER, propertyManager.getProperty(EktooProperties.PATH_SOURCE_FOLDER_DEFAULT));
		modifySettings(PATH_TARGET_FOLDER, propertyManager.getProperty(EktooProperties.PATH_TARGET_FOLDER_DEFAULT));
		
		fileName = Util.getFileName(propertyManager, EktooProperties.PATH_SOURCE_ZIP);
		modifySettings(PATH_SOURCE_ZIP, fileName);
		
	}
	
	public void loadDefaultCloudSettings(){
		modifySettings(CLOUD_ROOT_URI, propertyManager.getProperty(EktooProperties.CLOUD_ROOT_URI_DEFAULT));
		modifySettings(CLOUD_MESH_NAME, propertyManager.getProperty(EktooProperties.CLOUD_MESH_NAME_DEFAULT));
		modifySettings(CLOUD_DATASET_NAME, propertyManager.getProperty(EktooProperties.CLOUD_DATASET_NAME_DEFAULT));
	}
	
	public void loadDefaultMySqlSettings(){
		try{
			modifySettings(USER_NAME_MYSQL, propertyManager.getPropertyAsDecrepted(EktooProperties.USER_NAME_MYSQL_DEFAULT));
			modifySettings(USER_PASSWORD_MYSQL, propertyManager.getPropertyAsDecrepted(EktooProperties.USER_PASSWORD_MYSQL_DEFAULT));
			modifySettings(HOST_NAME_MYSQL, propertyManager.getPropertyAsDecrepted(EktooProperties.HOST_NAME_MYSQL_DEFAULT));
			modifySettings(PORT_MYSQL, propertyManager.getPropertyAsDecrepted(EktooProperties.PORT_MYSQL_DEFAULT));
			modifySettings(DATABASE_NAME_MYSQL, propertyManager.getPropertyAsDecrepted(EktooProperties.DATABASE_NAME_MYSQL_DEFAULT));
		} catch (Exception exception){
			LOGGER.error(exception);
		}
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
		
		String extension = "";
		if(generalSettingsModel.isCreateAsDefaultProp()){
			extension = ".default";
		} 
		propertyManager.setProperty(EktooProperties.LANGUAGE + extension, generalSettingsModel.getLanguage());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_DIR +  extension, generalSettingsModel.getPathSource());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_DIR +  extension, generalSettingsModel.getPathTarget());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_EXCEL + extension, generalSettingsModel.getPathSourceExcel());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_EXCEL + extension, generalSettingsModel.getPathTargetExcel());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ACCESS + extension, generalSettingsModel.getPathSourceAccess());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_ACCESS + extension, generalSettingsModel.getPathTargetAccess());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_KML + extension, generalSettingsModel.getPathSourceKml());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_KML + extension, generalSettingsModel.getPathTargetKml());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_RSS + extension, generalSettingsModel.getPathSourceRss());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_RSS + extension, generalSettingsModel.getPathTargetRss());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ATOM + extension, generalSettingsModel.getPathSourceAtom());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_ATOM + extension, generalSettingsModel.getPathTargetAtom());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_FOLDER + extension, generalSettingsModel.getPathSourceFolder());
		propertyManager.setProperty(EktooProperties.PATH_TARGET_FOLDER + extension, generalSettingsModel.getPathTargetFolder());
		
		propertyManager.setProperty(EktooProperties.PATH_SOURCE_ZIP + extension, generalSettingsModel.getPathSourceZip());
	}
	
	
	
	
	private void saveCloudSettings(CloudSettingsModel  cloudSettingsModel){
		String extension = "";
		if(cloudSettingsModel.isCreateAsDefaultProp()){
			extension = ".default";
		}
		
		propertyManager.setProperty(EktooProperties.CLOUD_ROOT_URI + extension, cloudSettingsModel.getSyncServerRootUri());
		propertyManager.setProperty(EktooProperties.CLOUD_MESH_NAME + extension, cloudSettingsModel.getMeshName());
		propertyManager.setProperty(EktooProperties.CLOUD_DATASET_NAME + extension, cloudSettingsModel.getDatasetName());
	}
	
	private void saveMysqlSettings(MySqlSettingsModel  mysqlSettingsModel){
		String extension = "";
		if(mysqlSettingsModel.isCreateAsDefaultProp()){
			extension = ".default";
		}
		
		try {
			propertyManager.setPropertyAsEncrypted(EktooProperties.USER_NAME_MYSQL +  extension , mysqlSettingsModel.getUserName());
			propertyManager.setPropertyAsEncrypted(EktooProperties.USER_PASSWORD_MYSQL + extension, mysqlSettingsModel.getUserPassword());
			propertyManager.setPropertyAsEncrypted(EktooProperties.HOST_NAME_MYSQL + extension, mysqlSettingsModel.getHostName());
			propertyManager.setPropertyAsEncrypted(EktooProperties.PORT_MYSQL + extension, mysqlSettingsModel.getPortNo());
			propertyManager.setPropertyAsEncrypted(EktooProperties.DATABASE_NAME_MYSQL + extension, mysqlSettingsModel.getDatabaseName());
		} catch (Exception e) {
			LOGGER.debug(e);
		}
	}
}
