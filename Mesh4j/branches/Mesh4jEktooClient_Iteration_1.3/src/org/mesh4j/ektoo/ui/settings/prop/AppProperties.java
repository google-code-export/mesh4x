package org.mesh4j.ektoo.ui.settings.prop;

/**
 * All the static property required by application along with their
 * default value represent here.Naming convention as follows.
 * <li>
 * Regular property(domain specific).
 * EX.CLOUD_ROOT_URI
 * <li>
 * Default property(append _DEFAULT as suffix along with regular property)
 * EX.CLOUD_ROOT_URI_DEFAULT
 * <li>
 * Default property value (append _VALUE as suffix along with default prop 
 * or regular porp) 
 * EX.CLOUD_ROOT_URI_DEFAULT_VALUE
 * 
 * @author raju
 */

public class AppProperties {

	
	public static final String USER_NAME_GOOGLE = "user.google";
	public static final String USER_PASSWORD_GOOGLE = "user.password.google";
	
	public static final String USER_NAME_MYSQL = "user.name.mysql";
	public static final String USER_PASSWORD_MYSQL = "user.password.mysql";
	public static final String HOST_NAME_MYSQL = "host.name.mysql";
	public static final String PORT_MYSQL = "port.name.mysql";
	public static final String DATABASE_NAME_MYSQL = "database.name.mysql";
	public static final String TABLE_NAME_MYSQL = "table.name.mysql";
	
	
	public static final String CLOUD_ROOT_URI = "cloud.root.uri";
	public static final String CLOUD_MESH_NAME = "cloud.mesh.name";
	public static final String CLOUD_DATASET_NAME = "cloud.dataset.name";
	
	public static final String LANGUAGE= "language";
	public static final String PATH_SOURCE_DIR = "path.source.dir";
	public static final String PATH_TARGET_DIR = "path.target.dir";

	public final static String PATH_SOURCE_EXCEL = "path.source.excel";
	public final static String PATH_TARGET_EXCEL = "path.target.excel";
	
	public final static String PATH_SOURCE_ACCESS = "path.source.access";
	public final static String PATH_TARGET_ACCESS = "path.target.access";
	
	public final static String PATH_SOURCE_KML = "path.source.kml";
	public final static String PATH_TARGET_KML = "path.source.kml";
	
	public final static String PATH_SOURCE_RSS = "path.source.rss";
	public final static String PATH_TARGET_RSS = "path.target.rss";
	
	public final static String PATH_SOURCE_ATOM = "path.source.atom";
	public final static String PATH_TARGET_ATOM = "path.target.atom";
	
	public final static String PATH_SOURCE_FOLDER = "path.source.folder";
	public final static String PATH_TARGET_FOLDER = "path.target.folder";
	
	public final static String PATH_SOURCE_ZIP = "path.source.zip";
	
	
	
	public final static String GOOGLE_GEO_CODER_KEY = "google.geo.coder.key";

	public final static String LOOK_AND_FEEL_CLASS_NAME = "look.and.feel.className";

	public final static String URL_GOOGLE_DOCS = "google.docs.url";
	public final static String URL_MESH4X = "url.mesh4x";
	public final static String URL_MESH4X_EKTOO = "url.mesh4x.ektoo";
	public final static String BASE_DIRECTORY = "base.directory";
	public final static String SYNC_IDENTITY_PROVIDER = "sync.identity.provider";
	

	
	/****************************Default property***********************************/
	public static final String USER_NAME_GOOGLE_DEFAULT = "user.google.default";
	public static final String USER_PASSWORD_GOOGLE_DEFAULT = "user.password.google.default";
	
	public static final String USER_NAME_MYSQL_DEFAULT = "user.name.mysql.default";
	public static final String USER_PASSWORD_MYSQL_DEFAULT = "user.password.mysql.default";
	public static final String HOST_NAME_MYSQL_DEFAULT = "host.name.mysql.default";
	public static final String PORT_MYSQL_DEFAULT = "port.name.mysql.default";
	public static final String DATABASE_NAME_MYSQL_DEFAULT = "database.name.mysql.default";
	public static final String TABLE_NAME_MYSQL_DEFAULT = "table.name.mysql.default";
	
	
	
	public static final String CLOUD_ROOT_URI_DEFAULT = "cloud.root.uri.default";
	public static final String CLOUD_MESH_NAME_DEFAULT = "cloud.mesh.name.default";
	public static final String CLOUD_DATASET_NAME_DEFAULT = "cloud.dataset.name.default";
	
	public static final String LANGUAGE_DEFAULT = "language.default";
	
	
	
	public static final String PATH_SOURCE_DIR_DEFAULT = "path.source.dir.default";
	public static final String PATH_TARGET_DIR_DEFAULT = "path.source.dir.default";
	
	public final static String PATH_SOURCE_EXCEL_DEFAULT = "path.source.excel.default";
	public final static String PATH_TARGET_EXCEL_DEFAULT = "path.target.excel.default";
	
	public final static String PATH_SOURCE_ACCESS_DEFAULT = "path.source.access.default";
	public final static String PATH_TARGET_ACCESS_DEFAULT = "path.target.access.default";
	
	public final static String PATH_SOURCE_KML_DEFAULT = "path.source.kml.default";
	public final static String PATH_TARGET_KML_DEFAULT = "path.source.kml.default";
	
	public final static String PATH_SOURCE_RSS_DEFAULT = "path.source.rss.default";
	public final static String PATH_TARGET_RSS_DEFAULT = "path.target.rss.default";
	
	public final static String PATH_SOURCE_ATOM_DEFAULT = "path.source.atom.default";
	public final static String PATH_TARGET_ATOM_DEFAULT = "path.target.atom.default";
	
	public final static String PATH_SOURCE_FOLDER_DEFAULT = "path.source.folder.default";
	public final static String PATH_TARGET_FOLDER_DEFAULT = "path.target.folder.default";
	
	public final static String PATH_SOURCE_ZIP_DEFAULT = "path.source.zip.default";
	
	//******************************* Default properties value  ****************
	
	public static final String LANGUAGE_DEFAULT_VALUE = "English";
	public static final String LANGUAGE_ENGLISH = "English";
	public static final String LANGUAGE_SYSTEM_DEFAULT = "System default";
	
	
	public static final String USER_NAME_MYSQL_DEFAULT_VALUE = "root";
	public static final String USER_PASSWORD_MYSQL_DEFAULT_VALUE = "";
	public static final String HOST_NAME_MYSQL_DEFAULT_VALUE = "localhost";
	public static final String PORT_MYSQL_DEFAULT_VALUE = "3306";
	public static final String DATABASE_NAME_MYSQL_DEFAULT_VALUE = "ektoo";
	public static final String TABLE_NAME_MYSQL_DEFAULT_VALUE = "ektoo";
	
	
	public static final String CLOUD_ROOT_URI_DEFAULT_VALUE = "http://localhost:8080/mesh4x/feeds";
	public static final String CLOUD_MESH_NAME_DEFAULT_VALUE = "myMesh";
	public static final String CLOUD_DATASET_NAME_DEFAULT_VALUE = "myFeed";
	
	
	public static final String USER_NAME_GOOGLE_DEFAULT_VALUE = "gspreadsheet.test@gmail.com";
	public static final String USER_PASSWORD_GOOGLE_DEFAULT_VALUE = "java123456";
	
	
	
	public static final String PATH_SOURCE_DIR_DEFAULT_VALUE = "..\\..\\.\\mesh4x\\demos\\ektoo\\data\\source";
	public static final String PATH_TARGET_DIR_DEFAULT_VALUE = "..\\..\\.\\mesh4x\\demos\\ektoo\\data\\target";
	
	public final static String PATH_SOURCE_EXCEL_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\EktooSource.xls";
	public final static String PATH_TARGET_EXCEL_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\EktooSource.xls";
	
	public final static String PATH_SOURCE_ACCESS_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\EktooSource.mdb";
	public final static String PATH_TARGET_ACCESS_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\EktooSource.mdb";
	
	public final static String PATH_SOURCE_KML_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\Ektoo.kml";
	public final static String PATH_TARGET_KML_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\Ektoo.kml";
	
	public final static String PATH_SOURCE_RSS_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\rss.xml";
	public final static String PATH_TARGET_RSS_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\rss.xml";
	
	public final static String PATH_SOURCE_ATOM_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\atom.xml";
	public final static String PATH_TARGET_ATOM_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\atom.xml";
	
	public final static String PATH_SOURCE_FOLDER_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\ektooFolder";
	public final static String PATH_TARGET_FOLDER_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\ektooFolder";
	
	public final static String PATH_SOURCE_ZIP_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo\\data\\Ektto.zip";
	
	
	
	public final static String GOOGLE_GEO_CODER_KEY_VALUE = "ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg";

	public final static String LOOK_AND_FEEL_CLASS_NAME_DEFAULT_VALUE = "javax.swing.plaf.metal.MetalLookAndFeel";

	public final static String URL_GOOGLE_DOCS_VALUE = "http://spreadsheets.google.com/";
	public final static String URL_MESH4X_VALUE = "http://code.google.com/p/mesh4x/";
	public final static String URL_MESH4X_EKTOO_VALUE = "http://code.google.com/p/mesh4x/wiki/HowToUseEktooClient";
	public final static String BASE_DIRECTORY_DEFAULT_VALUE = "..\\..\\..\\..\\mesh4x\\demos\\ektoo";
	public final static String SYNC_IDENTITY_PROVIDER_VALUE = "org.mesh4j.sync.security.LoggedInIdentityProvider";
	
	
}
