package org.mesh4j.sync.utils;

import java.io.File;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.GoogleGeoCoder;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.ISourceIdResolver;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.exporter.KMLExporter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.NonDeletedFilter;
import org.mesh4j.sync.message.IChannel;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.channel.sms.SmsChannelFactory;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.InMemorySmsConnection;
import org.mesh4j.sync.message.channel.sms.connection.SmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.channel.sms.connection.smslib.ModemHelper;
import org.mesh4j.sync.message.channel.sms.connection.smslib.SmsLibMessageSyncEngineFactory;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsEndpointFactory;
import org.mesh4j.sync.message.core.LoggerMessageSyncAware;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.message.core.repository.MessageSyncAdapterFactory;
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

	public static List<Item> synchronize(String url, String mdbFileName, String mdbTableName, IIdentityProvider identityProvider, String baseDirectory, ISourceIdResolver fileNameResolver) {
		
		try{
			ISyncAdapter httpAdapter = HttpSyncAdapterFactory.INSTANCE.createSyncAdapter(url, identityProvider);
			
			String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(fileNameResolver, baseDirectory);
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceID, identityProvider);
	
			SyncEngine syncEngine = new SyncEngine(syncAdapter, httpAdapter);
			List<Item> conflicts = syncEngine.synchronize();
			return conflicts;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public static void synchronize(MessageSyncEngine syncEngine, String toPhoneNumber, String mdbFileName, String mdbTableName, IIdentityProvider identityProvider, String baseDirectory, ISourceIdResolver fileNameResolver) throws Exception {
		
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
		IMessageSyncAdapter adapter = syncEngine.getSource(sourceID);
		if(adapter == null){
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(fileNameResolver, baseDirectory);
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceID, identityProvider);
			adapter = new MessageSyncAdapter(sourceID, identityProvider, syncAdapter);
		}
		syncEngine.synchronize(adapter, new SmsEndpoint(toPhoneNumber), true);
	}

	public static void cancelSynchronize(MessageSyncEngine syncEngine,String phoneNumber, String mdbFileName, String mdbTableName) {
		String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
		SmsEndpoint target = new SmsEndpoint(phoneNumber);
		syncEngine.cancelSync(sourceID, target);
	}

	public static MessageSyncEngine createEmulator(FileNameResolver fileNameResolver, ISmsConnectionInboundOutboundNotification smsConnectionNotification, 
			IMessageSyncAware syncAware, String smsFrom, IMessageEncoding encoding, 
			IIdentityProvider identityProvider, String baseDirectory, 
			int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) throws Exception {

		SmsEndpoint target = new SmsEndpoint(smsFrom);
		
		MessageSyncEngine syncEngine = createSyncEngineEmulator(
				fileNameResolver, smsFrom, encoding, identityProvider, baseDirectory+"/",
				senderDelay, receiverDelay, readDelay, channelDelay,
				maxMessageLenght, target, smsConnectionNotification, syncAware);
	
		return syncEngine;
	}

	public static void registerNewEndpointToEmulator(MessageSyncEngine syncEngine, String smsTo, IMessageEncoding encoding, 
			IIdentityProvider identityProvider, String baseDirectory, 
			int senderDelay, int receiverDelay, int readDelay, int channelDelay, int maxMessageLenght) {

		String targetDirectory = baseDirectory + "/" +smsTo +"/";
		
		SmsChannel foregroundChannel = (SmsChannel)syncEngine.getChannel();
		InMemorySmsConnection foregroundSmsConnection = (InMemorySmsConnection) foregroundChannel.getSmsConnection(); 
		
		SmsEndpoint backgroundTarget = new SmsEndpoint(smsTo);
		if(!foregroundSmsConnection.hasEndpointConnection(backgroundTarget)){
		
			FileNameResolver fileNameResolver = new FileNameResolver(targetDirectory+"myFiles.properties");
			MessageSyncEngine backgroundSyncEngine = createSyncEngineEmulator(fileNameResolver,
					smsTo, encoding, identityProvider, targetDirectory,
					senderDelay, receiverDelay, readDelay, channelDelay,
					maxMessageLenght, backgroundTarget, new SmsConnectionInboundOutboundNotification(), new LoggerMessageSyncAware());

			SmsChannel backgroundChannel = (SmsChannel)backgroundSyncEngine.getChannel();
			InMemorySmsConnection backgroundSmsConnection = (InMemorySmsConnection) backgroundChannel.getSmsConnection(); 
			
			foregroundSmsConnection.addEndpointConnection(backgroundSmsConnection);
			backgroundSmsConnection.addEndpointConnection(foregroundSmsConnection);
		}
	}

	private static MessageSyncEngine createSyncEngineEmulator(FileNameResolver fileNameResolver, String smsTarget,
			IMessageEncoding encoding, IIdentityProvider identityProvider,
			String baseDirectory, int senderDelay, int receiverDelay,
			int readDelay, int channelDelay, int maxMessageLenght, SmsEndpoint target,
			ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification, IMessageSyncAware syncAware) {
		
		InMemorySmsConnection smsConnection = new InMemorySmsConnection(encoding, maxMessageLenght, readDelay, target, channelDelay);
		smsConnection.setSmsConnectionOutboundNotification(smsConnectionInboundOutboundNotification);
		
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(fileNameResolver, baseDirectory);
		
		MessageSyncAdapterFactory messageSyncAdapterFactory = new MessageSyncAdapterFactory(syncAdapterFactory, false);
		
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, baseDirectory);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(100, baseDirectory, identityProvider, syncAware, SmsEndpointFactory.INSTANCE, messageSyncAdapterFactory);		
		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel); 

		return syncEngineEndPoint;
	}

	private static ISyncAdapterFactory makeSyncAdapterFactory(ISourceIdResolver sourceIdResolver, String baseDirectory) {
		MsAccessSyncAdapterFactory msAccessSyncFactory = new MsAccessSyncAdapterFactory(baseDirectory, sourceIdResolver);
//		IKMLGeneratorFactory kmlGeneratorFactory = new EpiInfoKmlGeneratorFactory(baseDirectory);
//		return new KMLTimeSpanDecoratorSyncAdapterFactory(baseDirectory, msAccessSyncFactory, kmlGeneratorFactory);
		return msAccessSyncFactory;
	}
	
	public static void addDataSource(FileNameResolver fileNameResolver, String fileName) {
		File file = new File(fileName);
		if(file.exists()){
			fileNameResolver.putSource(file.getName(), fileName);
			fileNameResolver.store();
		}
	}

	public static MessageSyncEngine createSyncEngine(
			FileNameResolver fileNameResolver, Modem modem,
			String baseDirectory, int senderDelay, int receiverDelay, int maxMessageLenght, 
			IIdentityProvider identityProvider,
			IMessageEncoding messageEncoding,
			ISmsConnectionInboundOutboundNotification smsConnectionInboundOutboundNotification,
			IMessageSyncAware messageSyncAware) {
		
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(fileNameResolver, baseDirectory);
		
		return SmsLibMessageSyncEngineFactory.createSyncEngine(
			modem, baseDirectory + "/", senderDelay, receiverDelay, maxMessageLenght,
			identityProvider, messageEncoding, smsConnectionInboundOutboundNotification, messageSyncAware, syncAdapterFactory);
	}
	
	public static Modem[] getAvailableModems(IProgressMonitor progressMonitor, Modem demoModem) {
		List<Modem> availableModems = ModemHelper.getAvailableModems(progressMonitor);
		if(availableModems.isEmpty()){
			availableModems.add(demoModem);
		}
		return availableModems.toArray(new Modem[0]);
	}

	public static void saveDefaults(Modem modem, String defaultPhoneNumber, String defaultDataSource, String defaultTableName, String defaultURL) {
		PropertiesProvider propertiesProvider = new PropertiesProvider();
		propertiesProvider.setDefaults(modem, defaultPhoneNumber, defaultDataSource, defaultTableName, defaultURL);
		propertiesProvider.store();
	}


	@SuppressWarnings("unchecked")
	public static void generateKML(String geoCoderKey, String templateFileName, String fromPhoneNumber, String mdbFileName, String mdbTableName, String baseDirectory, ISourceIdResolver fileNameResolver, IIdentityProvider identityProvider) throws Exception{
		
		String mappingsFileName = baseDirectory + "/" + mdbTableName + "_mappings.xml";
		
		IMappingResolver mappingResolver = null;
		File mappingsFile = new File(mappingsFileName);
		if(!mappingsFile.exists()){
			throw new IllegalArgumentException(EpiInfoUITranslator.getErrorKMLMappingsNotFound());
		}

		GoogleGeoCoder geoCoder = new GoogleGeoCoder(geoCoderKey);

		String sourceID = MsAccessSyncAdapterFactory.createSourceId(mdbFileName, mdbTableName);
		ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(fileNameResolver, baseDirectory);

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

}
