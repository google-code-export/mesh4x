package org.mesh4j.ektoo.properties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class PropertiesProvider {

	private static final String MESH4J_PROPERTIES = "mesh4j.properties";
	private static final String MESH4J_PROPERTIES_SETTINGS = "mesh4j_settings.properties";

	// MODEL VARIABLES
	private String resourceName;
	private Properties properties;

	// BUSINESS METHODS
	public PropertiesProvider() {
		super();
		this.resourceName = getCurrentDirectory() + "\\properties\\"
				+ MESH4J_PROPERTIES;
		initialize();
	}

	public PropertiesProvider(String resourceName) {
		super();
		this.resourceName = resourceName;
		initialize();
	}

	private void initialize() {
		try {
			FileReader reader = new FileReader(resourceName);
			Properties prop = new Properties();
			prop.load(reader);
			this.properties = prop;
			reader.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static String getSettingsPropertyLocation(){
		String settingsPropAbsoluteLocation = "";
		settingsPropAbsoluteLocation = getCurrentDirectory() + "\\properties\\"
		+ MESH4J_PROPERTIES_SETTINGS;
		return settingsPropAbsoluteLocation;
	}
	public String getMsAccessFile() {
		String defaultValue = this.properties.getProperty("default.mdb.file",
				"");
		File file = new File(defaultValue);
		if (file.exists() && file.canRead()) {
			try{
				defaultValue = file.getCanonicalPath();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
		return defaultValue;
	}

	public String getMsAccessTable() {
		return this.properties.getProperty("default.mdb.table", "");
	}

	public String getMsExcelFile() {
		String defaultValue = this.properties.getProperty("default.excel.file", "");
		File file = new File(defaultValue);
		if (file.exists() && file.canRead()) {
			try{
				defaultValue = file.getCanonicalPath();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		}
		return defaultValue;
	}

	public String getMsExcelSheet() {
		return this.properties.getProperty("default.excel.sheet", "");
	}

	public String getMsExcelUniqueColumnName() {
		return this.properties.getProperty("default.excel.id", "");
	}

	public IIdentityProvider getIdentityProvider() {
		try {
			String identityProviderClassName = this.properties.getProperty(
					"sync.identity.provider", LoggedInIdentityProvider.class
							.getName());
			IIdentityProvider security = (IIdentityProvider) makeNewInstance(identityProviderClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object makeNewInstance(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = Class.forName(className);
		return clazz.newInstance();
	}

	public String getBaseDirectory() {
		return this.properties.getProperty("default.base.directory",
				getCurrentDirectory());
	}

	public static String getCurrentDirectory() {
		return System.getProperty("user.dir");
	}

	private String getString(String key) {
		return this.properties.getProperty(key, "");
	}

	public void store() {
		try {
			FileWriter writer = new FileWriter(resourceName);
			this.properties.store(writer, "");
			writer.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String getDefaultURL() {
		return getString("default.url");
	}

	public String getMeshURL(String alias) {
		return getMeshSyncServerURL() + "/" + alias;
	}

	public String getMeshSyncServerURL() {
		return getString("mesh4x.sync.url");
	}

	public String getMesh4xURL() {
		return getString("mesh4x.url");
	}

	public String getMesh4xEktooURL() {
		return getString("mesh4x.ektoo.url");
	}
	
	public String getLoggedUserName() {
		return LoggedInIdentityProvider.getUserName();
	}

	public String getDefaultMySQLHost() {
		return getString("default.mysql.host");
	}

	public String getDefaultMySQLPort() {
		return getString("default.mysql.port");
	}

	public String getDefaultMySQLUser() {
		return getString("default.mysql.user");
	}

	public String getDefaultMySQLPassword() {
		return getString("default.mysql.password");
	}

	public String getDefaultMySQLSchema() {
		return getString("default.mysql.schema");
	}

	public String getDefaultMySQLTable() {
		return getString("default.mysql.table");
	}

	public String getDefaultKMLFile() {
		String fileName = getString("default.kml.file.name");
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}

	public String getLookAndFeel() {
		return getString("default.look.and.feel.className");
	}

	public String getDefaultRSSFile() {
		String fileName = getString("default.rss.file.name");
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}

	public String getDefaultAtomFile() {
		String fileName = getString("default.atom.file.name");
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}

	public String getDefaultFolderFile() {
		String fileName = getString("default.folder.name");
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}

	public String getGoogleDocsURL() {
		return getString("google.docs.url");
	}

	public String getDefaultZipFileName() {
		String fileName = getString("default.zip.name");
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}

}
