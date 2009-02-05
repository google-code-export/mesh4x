package org.mesh4j.sync.properties;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.core.NonMessageEncoding;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;


public class PropertiesProvider {

	private static final String MESH4J_PROPERTIES = "mesh4j.properties";
	
	// MODEL VARIABLES
	private String resourceName;
	private Properties properties;
	
	// BUSINESS METHODS
	public PropertiesProvider(){
		super();
		this.resourceName = getCurrentDirectory()+"\\properties\\"+MESH4J_PROPERTIES;
		initialize();
	}
	
	public PropertiesProvider(String resourceName){
		super();
		this.resourceName = resourceName;
		initialize();
	}
	
	private void initialize() {
		try{
			FileReader reader = new FileReader(resourceName);
			Properties prop = new Properties();
			prop.load(reader);
			this.properties = prop;
			reader.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String getDefaultDataSource() {
		String defaultValue =  this.properties.getProperty("default.mdb.file", "");
		File file = new File(defaultValue);
		if(file.exists() && file.canRead()){
			defaultValue = file.getAbsolutePath();
		}	
		return defaultValue;
	}

	public String getDefaultTable() {
		return this.properties.getProperty("default.mdb.table", "");
	}
	
	public String getDefaultPhoneNumber() {
		return this.properties.getProperty("default.phone.number", "");
	}

	public IIdentityProvider getIdentityProvider() {
		try{
			String identityProviderClassName =  this.properties.getProperty("sync.identity.provider", LoggedInIdentityProvider.class.getName());
			IIdentityProvider security = (IIdentityProvider)makeNewInstance(identityProviderClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Object makeNewInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = Class.forName(className);
		return clazz.newInstance();
	}

	public String getBaseDirectory() {
		return this.properties.getProperty("default.base.directory", getCurrentDirectory());
	}

	public static String getCurrentDirectory(){
		return System.getProperty("user.dir");
	}
	
	private int getInt(String key) {
		return Integer.valueOf(this.properties.getProperty(key, "0"));
	}

	private String getString(String key) {
		return this.properties.getProperty(key, "");
	}

	private boolean getBoolean(String key) {
		return Boolean.valueOf(this.properties.getProperty(key, "true"));
	}

	private Object getInstance(String key, Object defaultInstanceIfAbsent) {
		try{
			String className = this.getString(key);
			if(className == null || className.length() == 0){
				return defaultInstanceIfAbsent;
			}
			return makeNewInstance(className);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public void setDefaults(Modem modem, String defaultPhoneNumber, String defaultDataSource, String defaultTableName, String defaultURL) {
		if(modem != null && modem.getComPort() != null){
			this.properties.put("default.sms.port", modem.getComPort());
		}
		
		if(modem != null && modem.getBaudRate() > 0){
			this.properties.put("default.sms.baud.rate", String.valueOf(modem.getBaudRate()));
		}
		
		this.properties.put("default.phone.number", defaultPhoneNumber);
		this.properties.put("default.mdb.file", defaultDataSource);
		this.properties.put("default.mdb.table", defaultTableName);
		this.properties.put("default.url", defaultURL);
	
	}

	public void store() {
		try{
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

	public IMessageEncoding getDefaultMessageEncoding() {
		return (IMessageEncoding) getInstance("default.sms.compress.method", NonMessageEncoding.INSTANCE);
	}

	public String getDefaultPort() {
		return getString("default.sms.port");
	}

	public int getDefaultBaudRate() {
		return getInt("default.sms.baud.rate");
	}

	public String getDefaultKMLTemplateFileName() {
		return getString("default.kml.template.file.name");
	}
	
	public String getDefaultKMLTemplateNetworkLinkFileName() {
		return getString("default.kml.template.networklink.file.name");
	}

	public String getGeoCoderKey() {
		return getString("google.geo.coder.key");
	}

	private void setString(String key, String value) {
		this.properties.put(key, value);		
	}

	private void setInt(String key, int value) {
		this.properties.put(key, String.valueOf(value));		
	}

	public int getDefaultSendRetryDelay() {
		return getInt("default.sms.sender.delay");
	}

	public int getDefaultReceiveRetryDelay() {
		return getInt("default.sms.receiver.delay");
	}

	public int getDefaultReadyToSyncDelay() {
		return getInt("default.ready.to.sync.delay");
	}

	public int getDefaultTestPhoneDelay() {
		return getInt("default.test.phone.delay");
	}

	public void saveDefaultProperties(String portName, int baudRate,
			int sendRetryDelay, int receiveRetryDelay, int readyToSyncDelay,
			int testPhoneDelay) {
		this.setString("default.sms.port", portName);
		this.setInt("default.sms.baud.rate", baudRate);
		this.setInt("default.sms.sender.delay", sendRetryDelay);
		this.setInt("default.sms.receiver.delay", receiveRetryDelay);
		this.setInt("default.ready.to.sync.delay", readyToSyncDelay);
		this.setInt("default.test.phone.delay", testPhoneDelay);
		this.store();
	}

	public String getMesh4xURL() {
		return getString("mesh4x.url");
	}

	public boolean mustTraceProtocol() {
		return getBoolean("dafault.trace.protocol");
	}

	public boolean mustTraceSms() {
		return getBoolean("dafault.trace.sms");
	}
	
	public boolean mustUseItemEncodingFixedBlock(){
		return getBoolean("default.sms.item.encoding.fixed.block");
	}
	
	public int getItemEncodingBlockSize(){
		return Integer.valueOf(this.properties.getProperty("default.sms.item.encoding.block.size", "100"));
	}

	public int getDefaultMaxMessageLenght() {
		return getInt("default.sms.max.message.lenght");
	}

	public boolean isSyncCloudEnabled() {
		return getBoolean("mesh4x.sync.web.enabled");
	}

	public boolean isEmulationModeActive() {
		return getBoolean("emulate.sync");
	}

	public String getEmulationInFolder() {
		return getCurrentDirectory()+"\\in";
		//return getBaseDirectory()+"\\in";
	}

	public String getEmulationOutRootFolder() {
		File file = new File(getCurrentDirectory());
		//File file = new File(getBaseDirectory());
		return file.getParent();
	}

	public String getEmulationEndpointId() {
		File file = new File(getCurrentDirectory());
		//File file = new File(getBaseDirectory());
		return file.getName();
	}

	public String getLoggedUserName() {
		if(this.isEmulationModeActive()){
			return this.getEmulationEndpointId();
		} else {
			return LoggedInIdentityProvider.getUserName();
		}
	}
}
