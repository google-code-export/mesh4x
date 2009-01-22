package org.mesh4j.sync.utils;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.GoogleGeoCoder;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.IMsAccessSourceIdResolver;
import org.mesh4j.sync.adapters.msaccess.MsAccessHelper;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.NonDeletedFilter;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.SyncMode;
import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibAsynchronousConnection;
import org.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibMessageSyncEngineFactory;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
import org.mesh4j.sync.message.core.repository.OpaqueFeedSyncAdapterFactory;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMappingResolver;
import org.mesh4j.sync.payload.mappings.MappingResolver;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineUtil {

	// TODO (JMT) Add number of GET/MERGE and ACKs to client session target values
	// TODO (JMT) Add items added/updated/deleted to client session target values

	private final static Log Logger = LogFactory.getLog(SyncEngineUtil.class);
	
	public static List<Item> synchronize(String url, String sourceAlias, IIdentityProvider identityProvider, String baseDirectory, IMsAccessSourceIdResolver sourceIdResolver) {
		
		try{
			ISyncAdapter httpAdapter = HttpSyncAdapterFactory.INSTANCE.createSyncAdapter(url, identityProvider);
			
			String sourceID = MsAccessSyncAdapterFactory.createSourceId(sourceAlias);
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(sourceIdResolver, baseDirectory);
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceID, identityProvider);
	
			SyncEngine syncEngine = new SyncEngine(syncAdapter, httpAdapter);
			List<Item> conflicts = syncEngine.synchronize();
			return conflicts;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public static void synchronize(MessageSyncEngine syncEngine, SyncMode syncMode, String toPhoneNumber, String sourceAlias, IIdentityProvider identityProvider, String baseDirectory, IMsAccessSourceIdResolver sourceIdResolver) throws Exception {
		
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(sourceAlias);
		IMessageSyncAdapter adapter = syncEngine.getSource(sourceID);
		if(adapter == null){
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(sourceIdResolver, baseDirectory);
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceID, identityProvider);
			adapter = new MessageSyncAdapter(sourceID, syncFactory.getSourceType(), identityProvider, syncAdapter);
		}
		syncEngine.synchronize(adapter, new SmsEndpoint(toPhoneNumber), true, syncMode.shouldSendChanges(), syncMode.shouldReceiveChanges());
	}

	public static void cancelSynchronize(MessageSyncEngine syncEngine,String phoneNumber, String sourceAlias) {
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(sourceAlias);
		SmsEndpoint target = new SmsEndpoint(phoneNumber);
		syncEngine.cancelSync(sourceID, target);
	}

	public static MessageSyncEngine createSyncEngineEmulator(IMsAccessSourceIdResolver sourceIdResolver,
			IMessageEncoding encoding, IIdentityProvider identityProvider,
			String baseDirectory, int senderDelay, int receiverDelay,
			int readDelay, int channelDelay, int maxMessageLenght, SmsEndpoint target,
			ISmsConnectionInboundOutboundNotification[] smsAware, IMessageSyncAware[] syncAware,
			boolean isOpaque, String inDir, String outDir) {
		
		
		ISmsConnection smsConnection = new FileWatcherSmsConnection(inDir, outDir, encoding, maxMessageLenght, smsAware);
				
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(sourceIdResolver, baseDirectory);

		MessageSyncAdapterFactory messageSyncAdapterFactory;
		if(isOpaque){
			messageSyncAdapterFactory = new MessageSyncAdapterFactory(new OpaqueFeedSyncAdapterFactory(baseDirectory), false);
		} else {
			messageSyncAdapterFactory = new MessageSyncAdapterFactory(null, false, syncAdapterFactory);
		}
		
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, baseDirectory);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, baseDirectory, channel, identityProvider, syncAware, SmsEndpointFactory.INSTANCE, messageSyncAdapterFactory);		
		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel); 

		return syncEngineEndPoint;
	}

	private static ISyncAdapterFactory makeSyncAdapterFactory(IMsAccessSourceIdResolver sourceIdResolver, String baseDirectory) {
		MsAccessSyncAdapterFactory msAccessSyncFactory = new MsAccessSyncAdapterFactory(baseDirectory, sourceIdResolver);
		return msAccessSyncFactory;
	}
	
	public static void addDataSource(EpiinfoSourceIdResolver sourceIdResolver, String fileName, String tableName) {
		File file = new File(fileName);
		if(file.exists()){
			String sourceAlias = tableName;
			sourceIdResolver.saveDataSourceMapping(new DataSourceMapping(sourceAlias, file.getName(), tableName, fileName));
		}
	}
	
	public static String getMDBName(String fileName) {
		File file = new File(fileName);
		return file.getName();
	}

	public static MessageSyncEngine createSyncEngine(
			IMsAccessSourceIdResolver sourceIdResolver, Modem modem,
			String baseDirectory, int senderDelay, int receiverDelay, int maxMessageLenght, 
			IIdentityProvider identityProvider,
			IMessageEncoding messageEncoding,
			ISmsConnectionInboundOutboundNotification[] smsAware,
			IMessageSyncAware[] syncAware) {
		
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(sourceIdResolver, baseDirectory);
		
		return SmsLibMessageSyncEngineFactory.createSyncEngine(
			modem, baseDirectory + "/", senderDelay, receiverDelay, maxMessageLenght,
			identityProvider, messageEncoding, smsAware, syncAware, syncAdapterFactory);
	}
	
	public static Modem[] getAvailableModems(IProgressMonitor progressMonitor) {
		List<Modem> availableModems = ModemHelper.getAvailableModems(progressMonitor);
		return availableModems.toArray(new Modem[0]);
	}

	public static void saveDefaults(Modem modem, String defaultPhoneNumber, String defaultDataSource, String defaultTableName, String defaultURL) {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		propertiesProvider.setDefaults(modem, defaultPhoneNumber, defaultDataSource, defaultTableName, defaultURL);
		propertiesProvider.store();
	}

	@SuppressWarnings("unchecked")
	public static void generateKML(String geoCoderKey, String templateFileName, String fromPhoneNumber, String mdbFileName, String mdbTableName, String baseDirectory, EpiinfoSourceIdResolver sourceIdResolver, IIdentityProvider identityProvider) throws Exception{
		
		String mappingsFileName = baseDirectory + "/" + mdbTableName + "_mappings.xml";
		
		IMappingResolver mappingResolver = null;
		File mappingsFile = new File(mappingsFileName);
		if(!mappingsFile.exists()){
			throw new IllegalArgumentException(EpiInfoUITranslator.getErrorKMLMappingsNotFound());
		}

		GoogleGeoCoder geoCoder = new GoogleGeoCoder(geoCoderKey);

		String sourceAlias = sourceIdResolver.getSourceName(mdbFileName, mdbTableName);
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(sourceAlias);
		ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(sourceIdResolver, baseDirectory);

		IKMLGeneratorFactory kmlGeneratorFactory = new EpiInfoKmlGeneratorFactory(baseDirectory, templateFileName, geoCoder);
		KMLTimeSpanDecoratorSyncAdapterFactory kmlDecSyncFactory = new KMLTimeSpanDecoratorSyncAdapterFactory(baseDirectory, syncFactory, kmlGeneratorFactory);

		KMLTimeSpanDecoratorSyncAdapter syncAdapter = kmlDecSyncFactory.createSyncAdapter(sourceID, identityProvider);
		syncAdapter.beginSync();
		
		CompoundFilter filter = new CompoundFilter(NonDeletedFilter.INSTANCE);
		List<Item> items = syncAdapter.getAll(filter);
		syncAdapter.endSync();
		
		String kmlFileName = baseDirectory + "/" + mdbTableName + "_last.kml";
		
		byte[] bytes = FileUtils.read(mappingsFile);
		String xml = new String(bytes);
		Element schema = DocumentHelper.parseText(xml).getRootElement();
		
		GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
		GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
		GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
		mappingResolver = new MappingResolver(schema, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
		KMLExporter.export(kmlFileName, mdbTableName, items, mappingResolver);			

	}

	public static void downloadSchema(String url, String tableName, String baseDirectory) throws Exception {
		
		HttpSyncAdapter httpSyncAdapter = HttpSyncAdapterFactory.INSTANCE.createSyncAdapter(url, NullIdentityProvider.INSTANCE);
		String xmlSchema = httpSyncAdapter.getSchema();
		
		String fileName = baseDirectory + "/" + tableName + "_schema.xml";
		FileUtils.write(fileName, xmlSchema.getBytes());
	}
	
	public static void downloadMappings(String url, String tableName, String baseDirectory) throws Exception {
		
		HttpSyncAdapter httpSyncAdapter = HttpSyncAdapterFactory.INSTANCE.createSyncAdapter(url, NullIdentityProvider.INSTANCE);
		String xmlMappings = httpSyncAdapter.getMappings();
		
		String fileName = baseDirectory + "/" + tableName + "_mappings.xml";
		FileUtils.write(fileName, xmlMappings.getBytes());
	}

	public static void makeKMLWithNetworkLink(String templateFileName, String fileName, String tableName, String url) throws Exception {
		byte[] bytes = FileUtils.read(templateFileName);
		String template = new String(bytes);
		FileUtils.write(fileName, MessageFormat.format(template, tableName, url).getBytes());
	}

	// NEW EXAMPLE UI

	public static MessageSyncEngine createSyncEngine(
			EpiinfoSourceIdResolver sourceIdResolver, 
			PropertiesProvider propertiesProvider, 
			IMessageSyncAware[] syncAware, 
			ISmsConnectionInboundOutboundNotification[] smsAware) throws Exception {
		
		String baseDirectory = propertiesProvider.getBaseDirectory();
		int senderDelay = propertiesProvider.getDefaultSendRetryDelay();
		int receiverDelay = propertiesProvider.getDefaultReceiveRetryDelay();
		int maxMessageLenght = propertiesProvider.getInt("default.sms.max.message.lenght");
		IIdentityProvider identityProvider = propertiesProvider.getIdentityProvider();
		IMessageEncoding messageEncoding = propertiesProvider.getDefaultMessageEncoding();
		String portName = propertiesProvider.getDefaultPort();
		int baudRate = propertiesProvider.getDefaultBaudRate();
		
		Modem modem = new Modem(portName, baudRate, "", "", "", "", 0, 0);
		
		boolean emulateSync = propertiesProvider.getBoolean("emulate.sync");
		if(emulateSync){
			String inDirectory = propertiesProvider.getString("emulate.sync.file.connection.in");
			String outDirectory = propertiesProvider.getString("emulate.sync.file.connection.out");
			
			return createSyncEngineEmulator(
					sourceIdResolver,
					messageEncoding,
					identityProvider, 
					baseDirectory+"/",
					0, 
					0, 
					0, 
					0,
					maxMessageLenght,
					new SmsEndpoint(EpiInfoUITranslator.getLabelDemo()),
					smsAware, 
					syncAware, 
					false,
					inDirectory,
					outDirectory);
		} else {
			return createSyncEngine(
					sourceIdResolver, 
					modem,
					baseDirectory, 
					senderDelay, 
					receiverDelay, 
					maxMessageLenght, 
					identityProvider,
					messageEncoding,
					smsAware,
					syncAware);
		}
	}

	public static void synchronize(MessageSyncEngine syncEngine, SyncMode syncMode, EndpointMapping endpoint, DataSourceMapping dataSource, EpiinfoSourceIdResolver sourceIdResolver, PropertiesProvider propertiesProvider) throws Exception {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		IIdentityProvider identityProvider = propertiesProvider.getIdentityProvider();
		
		synchronize(syncEngine, syncMode, endpoint.getEndpoint(), dataSource.getAlias(), identityProvider, baseDirectory, sourceIdResolver);	
	}

	public static void cancelSynchronize(MessageSyncEngine syncEngine, EndpointMapping endpoint, DataSourceMapping dataSource) {
		cancelSynchronize(syncEngine, endpoint.getEndpoint(), dataSource.getAlias());
	}

	public static void sendSms(MessageSyncEngine syncEngine, String endpoint, String message) {
		((SmsChannel)syncEngine.getChannel()).send(new SmsMessage(message), new SmsEndpoint(endpoint));
	}


	public static Set<String> getTableNames(String fileName) {
		try{
			Set<String> tableNames = MsAccessHelper.getTableNames(fileName);
			return tableNames;
		}catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return new TreeSet<String>();
		}
	}
	
	public static EndpointMapping[] getEndpointMappings(PropertiesProvider propertiesProvider) {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		String fileName = baseDirectory+"/myEndpoints.properties";
		Map<String, String> myEndpoints = PropertiesUtils.getProperties(fileName);
		EndpointMapping[] result = new EndpointMapping[myEndpoints.size()];
		int i = 0;
		for (String alias : myEndpoints.keySet()) {
			result[i] = new EndpointMapping(alias, myEndpoints.get(alias));
			i = i + 1;
		}
		return result;
	}
	
	public static EndpointMapping getEndpointMapping(String endpointId, PropertiesProvider propertiesProvider) {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		String fileName = baseDirectory+"/myEndpoints.properties";
		Map<String, String> myEndpoints = PropertiesUtils.getProperties(fileName);
		for (String alias : myEndpoints.keySet()) {
			if(endpointId.equals(myEndpoints.get(alias))){
				return new EndpointMapping(alias, endpointId);
			}
		}
		return null;
	}
	
	public static void deleteEndpointMapping(EndpointMapping endpoint, PropertiesProvider propertiesProvider) {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		String fileName = baseDirectory+"/myEndpoints.properties";
		
		Map<String, String> myEndpoints = PropertiesUtils.getProperties(fileName);
		String result = myEndpoints.remove(endpoint.getAlias());
		if(result != null){
			PropertiesUtils.store(fileName, myEndpoints);
		}		
	}
	
	public static void saveOrUpdateEndpointMapping(String alias, EndpointMapping endpoint, PropertiesProvider propertiesProvider) {
		String baseDirectory = propertiesProvider.getBaseDirectory();
		String fileName = baseDirectory+"/myEndpoints.properties";
		
		Map<String, String> myEndpoints = PropertiesUtils.getProperties(fileName);
		myEndpoints.remove(alias);
		
		myEndpoints.put(endpoint.getAlias(), endpoint.getEndpoint());
		PropertiesUtils.store(fileName, myEndpoints);		
		
	}

	public static void initializeSmsConnection(MessageSyncEngine syncEngine, PropertiesProvider propertiesProvider) {
		SmsChannel smsChannel = (SmsChannel)syncEngine.getChannel();
		SmsLibAsynchronousConnection smsLibConnection = (SmsLibAsynchronousConnection)smsChannel.getSmsConnection();
		smsLibConnection.initialize("mesh4x", propertiesProvider.getDefaultPort(), propertiesProvider.getDefaultBaudRate(), "", "");
	}


}
