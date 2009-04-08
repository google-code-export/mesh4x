package org.mesh4j.sync.utils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.NullPreviewHandler;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGeneratorFactory;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapter;
import org.mesh4j.sync.adapters.kml.timespan.decorator.KMLTimeSpanDecoratorSyncAdapterFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.filter.CompoundFilter;
import org.mesh4j.sync.filter.NonDeletedFilter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
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
import org.mesh4j.sync.message.channel.sms.connection.file.watcher.FileWatcherSmsConnection;
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
import org.mesh4j.sync.message.protocol.IItemEncoding;
import org.mesh4j.sync.message.protocol.ItemEncoding;
import org.mesh4j.sync.message.protocol.ItemEncodingFixedBlock;
import org.mesh4j.sync.message.protocol.MessageSyncProtocolFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.ui.SyncSessionsFrame.CloudSyncSessionWrapper;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.mesh4j.sync.validations.MeshException;

public class SyncEngineUtil {

	// TODO (JMT) Add number of GET/MERGE and ACKs to client session target values
	// TODO (JMT) Add items added/updated/deleted to client session target values
	// TODO (JMT) Add sinceDate in cloud sync
	
	private final static Log Logger = LogFactory.getLog(SyncEngineUtil.class);
	
	public static List<Item> synchronize(String url, String sourceAlias, PropertiesProvider propertiesProvider, SourceIdMapper sourceIdMapper, SyncMode syncMode) {

		IIdentityProvider identityProvider = propertiesProvider.getIdentityProvider();
		String baseDirectory = propertiesProvider.getBaseDirectory();
		Date start = new Date();
		try{
			// create HTTPSyncAdapter
			MSAccessDataSourceMapping dataSourceMapping = sourceIdMapper.getDataSource(sourceAlias);
			ISchema schema = getSchema(dataSourceMapping, propertiesProvider);
			IMapping mappings = getMappings(sourceAlias, propertiesProvider);
			SchemaInstanceContentReadWriter contentReadWriter = new SchemaInstanceContentReadWriter(schema, mappings, true);
			ISyncAdapter httpAdapter = new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, identityProvider, 
					IdGenerator.INSTANCE, contentReadWriter, contentReadWriter);
			
			// create MSAccessAdapter
			String sourceDefinition = sourceIdMapper.getSourceDefinition(sourceAlias);
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(baseDirectory, propertiesProvider.getMeshSyncServerURL()+"/"+propertiesProvider.getMeshID());
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
	
			// sync
			SyncEngine syncEngine = new SyncEngine(syncAdapter, httpAdapter);
			List<Item> conflicts = syncEngine.synchronize(NullPreviewHandler.INSTANCE, syncMode.getBehavior());
			
			traceCloudSynchronization(url, syncMode, start, new Date(), conflicts, false, sourceAlias, identityProvider, baseDirectory);
			
			return conflicts;
		} catch (Exception e) {
			traceCloudSynchronization(url, syncMode, start, new Date(), null, true, sourceAlias, identityProvider, baseDirectory);
			throw new MeshException(e);
		}
	}
		
	public static FeedAdapter getCloudSyncTraceAdapter(String sourceAlias, IIdentityProvider identityProvider, String baseDirectory) {
		try{
			String fileName = baseDirectory + "/" + sourceAlias + "_cloudSync.xml";
			Feed feed = new Feed(sourceAlias, "Cloud synchronizations", "");
			FeedAdapter adapter = new FeedAdapter(fileName, identityProvider, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE, feed);
			return adapter;
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	
	public static void updateCloudSyncWrapper(CloudSyncSessionWrapper cloud, Item item){

		Element cloudSyncElement = item.getContent().getPayload();
		String start = cloudSyncElement.attributeValue("start");
		Date startDate = DateHelper.parseDateYYYYMMDDHHMMSS(start, TimeZone.getDefault());
		if(cloud.getStartDate() == null || cloud.getStartDate().getTime() < startDate.getTime()){
			String end = cloudSyncElement.attributeValue("end");
			int conflicts = Integer.valueOf(cloudSyncElement.attributeValue("conflicts"));
			boolean error = Boolean.parseBoolean(cloudSyncElement.attributeValue("error"));
			String syncMode = cloudSyncElement.attributeValue("syncMode");
			
			cloud.setSyncMode(syncMode);
			cloud.setStart(start);
			cloud.setEnd(end);
			cloud.setConflicts(conflicts);
			cloud.setError(error);
		}	
	}
	
	private static void traceCloudSynchronization(String url, SyncMode syncMode, Date start, Date end, List<Item> conflicts, boolean error, String sourceAlias, IIdentityProvider identityProvider, String baseDirectory) {
		try{
			FeedAdapter adapter = getCloudSyncTraceAdapter(sourceAlias, identityProvider, baseDirectory);
			
			String syncId = IdGenerator.INSTANCE.newID();
			
			Element root = DocumentHelper.createElement("cloudSync");
			root.addAttribute("start", DateHelper.formatDateYYYYMMDDHHMMSS(start, "/", ":", "", TimeZone.getDefault()));
			root.addAttribute("end", DateHelper.formatDateYYYYMMDDHHMMSS(end, "/", ":", "", TimeZone.getDefault()));
			root.addAttribute("conflicts", (conflicts == null || conflicts.isEmpty()) ? "0" : String.valueOf(conflicts.size()));
			root.addAttribute("error", error ? "true" : "false");
			root.addAttribute("syncMode", syncMode.name());
			
			XMLContent content = new XMLContent(syncId, "Cloud sync", "Cloud synchronization", url, root);
			Sync sync = new Sync(syncId, identityProvider.getAuthenticatedUser(), end, false);
			Item item = new Item(content, sync);
			adapter.add(item);
		} catch (Throwable e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public static void synchronize(MessageSyncEngine syncEngine, SyncMode syncMode, String toPhoneNumber, String sourceAlias, IIdentityProvider identityProvider, String baseDirectory, SourceIdMapper sourceIdMapper) throws Exception {

		IMessageSyncAdapter adapter = syncEngine.getSource(sourceAlias);
		if(adapter == null){
			String sourceDefinition = sourceIdMapper.getSourceDefinition(sourceAlias);
			ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(baseDirectory, null);
			ISyncAdapter syncAdapter = syncFactory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
			adapter = new MessageSyncAdapter(sourceAlias, syncFactory.getSourceType(), identityProvider, syncAdapter, syncFactory);
		}
		syncEngine.synchronize(adapter, new SmsEndpoint(toPhoneNumber), true, syncMode.shouldSendChanges(), syncMode.shouldReceiveChanges());
	}

	public static void cancelSynchronize(MessageSyncEngine syncEngine,String phoneNumber, String sourceAlias) {
		SmsEndpoint target = new SmsEndpoint(phoneNumber);
		syncEngine.cancelSync(sourceAlias, target);
	}

	public static MessageSyncEngine createSyncEngineEmulator(SourceIdMapper sourceIdMapper,
			IMessageEncoding encoding, IIdentityProvider identityProvider, IItemEncoding itemEncoding,
			String baseDirectory, int senderDelay, int receiverDelay,
			int maxMessageLenght, SmsEndpoint target,
			ISmsConnectionInboundOutboundNotification[] smsAware, IMessageSyncAware[] syncAware,
			boolean isOpaque, String inDir, String outDir, String endpointId) {
		
		ISmsConnection smsConnection = new FileWatcherSmsConnection(endpointId, inDir, outDir, maxMessageLenght, encoding, smsAware);
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(baseDirectory, null);

		MessageSyncAdapterFactory messageSyncAdapterFactory;
		if(isOpaque){
			messageSyncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, new OpaqueFeedSyncAdapterFactory(baseDirectory), false);
		} else {
			messageSyncAdapterFactory = new MessageSyncAdapterFactory(sourceIdMapper, null, false, syncAdapterFactory);
		}
		
		IFilter<String> protocolFilter = MessageSyncProtocolFactory.getProtocolMessageFilter();
		IChannel channel = SmsChannelFactory.createChannelWithFileRepository(smsConnection, senderDelay, receiverDelay, baseDirectory, protocolFilter);
		
		IMessageSyncProtocol syncProtocol = MessageSyncProtocolFactory.createSyncProtocolWithFileRepository(itemEncoding, baseDirectory, channel, identityProvider, syncAware, SmsEndpointFactory.INSTANCE, messageSyncAdapterFactory);		
		
		MessageSyncEngine syncEngineEndPoint = new MessageSyncEngine(syncProtocol, channel); 

		return syncEngineEndPoint;
	}

	private static ISyncAdapterFactory makeSyncAdapterFactory(String baseDirectory, String baseRDFUri) {
		MsAccessSyncAdapterFactory msAccessSyncFactory = new MsAccessSyncAdapterFactory(baseDirectory, baseRDFUri);
		return msAccessSyncFactory;
	}
	
	public static void addDataSource(SourceIdMapper sourceIdResolver, String fileName, String tableName) {
		File file = new File(fileName);
		if(file.exists()){
			String sourceAlias = tableName;
			sourceIdResolver.saveDataSourceMapping(new MSAccessDataSourceMapping(sourceAlias, file.getName(), tableName, fileName));
		}
	}
	
	public static String getMDBName(String fileName) {
		File file = new File(fileName);
		return file.getName();
	}

	public static MessageSyncEngine createSyncEngine(
			SourceIdMapper sourceIdMapper, Modem modem,
			String baseDirectory, int senderDelay, int receiverDelay, int maxMessageLenght, 
			IIdentityProvider identityProvider,
			IItemEncoding itemEncoding,
			IMessageEncoding messageEncoding,
			ISmsConnectionInboundOutboundNotification[] smsAware,
			IMessageSyncAware[] syncAware) {
		
		ISyncAdapterFactory syncAdapterFactory = makeSyncAdapterFactory(baseDirectory, null);
		
		return SmsLibMessageSyncEngineFactory.createSyncEngine(
			modem, baseDirectory + "/", senderDelay, receiverDelay, maxMessageLenght,
			identityProvider, itemEncoding, messageEncoding, sourceIdMapper, smsAware, syncAware, syncAdapterFactory);
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
	public static File generateKML(String geoCoderKey, String templateFileName, String mdbFileName, String dataSourceAlias, String baseDirectory, SourceIdMapper sourceIdResolver, IIdentityProvider identityProvider) throws Exception{
		
//		String mappingsFileName = baseDirectory + "/" + dataSourceAlias + "_mappings.xml";
//		
//		File mappingsFile = new File(mappingsFileName);
//		if(!mappingsFile.exists()){
//			throw new IllegalArgumentException(MeshUITranslator.getErrorKMLMappingsNotFound());
//		}

		String sourceDefinition = sourceIdResolver.getSourceDefinition(dataSourceAlias);
		ISyncAdapterFactory syncFactory = makeSyncAdapterFactory(baseDirectory, null);

		IKMLGeneratorFactory kmlGeneratorFactory = new KmlGeneratorFactory(baseDirectory, templateFileName, geoCoderKey);
		KMLTimeSpanDecoratorSyncAdapterFactory kmlDecSyncFactory = new KMLTimeSpanDecoratorSyncAdapterFactory(baseDirectory, syncFactory, kmlGeneratorFactory);
		
		KMLTimeSpanDecoratorSyncAdapter syncAdapter = kmlDecSyncFactory.createSyncAdapter(dataSourceAlias, sourceDefinition, identityProvider);
		syncAdapter.beginSync();
		
		CompoundFilter filter = new CompoundFilter(NonDeletedFilter.INSTANCE);
		syncAdapter.getAll(filter);
		syncAdapter.endSync();
			
		return syncAdapter.getKmlFile();
	}

	public static void makeKMLWithNetworkLink(String templateFileName, String fileName, String docName, String url) throws Exception {
		byte[] bytes = FileUtils.read(templateFileName);
		String template = new String(bytes);
		FileUtils.write(fileName, MessageFormat.format(template, docName, url).getBytes());
	}

	public static MessageSyncEngine createSyncEngine(
			SourceIdMapper sourceIdResolver, 
			PropertiesProvider propertiesProvider, 
			IMessageSyncAware[] syncAware, 
			ISmsConnectionInboundOutboundNotification[] smsAware) throws Exception {
		
		String baseDirectory = propertiesProvider.getBaseDirectory();
		int senderDelay = propertiesProvider.getDefaultSendRetryDelay();
		int receiverDelay = propertiesProvider.getDefaultReceiveRetryDelay();
		int maxMessageLenght = propertiesProvider.getDefaultMaxMessageLenght();
		IIdentityProvider identityProvider = propertiesProvider.getIdentityProvider();
		IMessageEncoding messageEncoding = propertiesProvider.getDefaultMessageEncoding();
		String portName = propertiesProvider.getDefaultPort();
		int baudRate = propertiesProvider.getDefaultBaudRate();
		
		IItemEncoding itemEncoding = null;
		
		int itemEncodingBlockSize = propertiesProvider.getItemEncodingBlockSize();
		if(propertiesProvider.mustUseItemEncodingFixedBlock()){
			itemEncoding = new ItemEncodingFixedBlock(itemEncodingBlockSize);
		} else {
			itemEncoding = new ItemEncoding(itemEncodingBlockSize);
		}
		
		Modem modem = new Modem(portName, baudRate, "", "", "", "", 0, 0);
		
		boolean emulateSync = propertiesProvider.isEmulationModeActive();
		if(emulateSync){
			String inDirectory = propertiesProvider.getEmulationInFolder();
			String outDirectory = propertiesProvider.getEmulationOutRootFolder();
			String endpointId = propertiesProvider.getEmulationEndpointId();
			return createSyncEngineEmulator(
					sourceIdResolver,
					messageEncoding,
					identityProvider, 
					itemEncoding,
					baseDirectory+"/",
					0, 
					0,
					maxMessageLenght,
					new SmsEndpoint(MeshUITranslator.getLabelDemo()),
					smsAware, 
					syncAware, 
					false,
					inDirectory,
					outDirectory,
					endpointId);
		} else {
			return createSyncEngine(
					sourceIdResolver, 
					modem,
					baseDirectory, 
					senderDelay, 
					receiverDelay, 
					maxMessageLenght, 
					identityProvider,
					itemEncoding,
					messageEncoding,
					smsAware,
					syncAware);
		}
	}

	public static void synchronize(MessageSyncEngine syncEngine, SyncMode syncMode, EndpointMapping endpoint, DataSourceMapping dataSource, SourceIdMapper sourceIdResolver, PropertiesProvider propertiesProvider) throws Exception {
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

	public static void initializeSmsConnection(MessageSyncEngine syncEngine, PropertiesProvider propertiesProvider) {
		SmsChannel smsChannel = (SmsChannel)syncEngine.getChannel();
		SmsLibAsynchronousConnection smsLibConnection = (SmsLibAsynchronousConnection)smsChannel.getSmsConnection();
		smsLibConnection.initialize("mesh4x", propertiesProvider.getDefaultPort(), propertiesProvider.getDefaultBaudRate(), "", "");
	}

	public static void uploadMeshDefinition(MSAccessDataSourceMapping dataSource, String description, PropertiesProvider propertiesProvider) throws Exception {
		
		String meshSourceId = propertiesProvider.getMeshID(dataSource.getAlias());
		
		// schema
		IRDFSchema rdfSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(
			dataSource.getFileName(), 
			dataSource.getTableName(), 
			dataSource.getAlias(),
			propertiesProvider.getMeshSyncServerURL()+"/"+meshSourceId+"#");
		
		String fileName = getSchemaFileName(dataSource.getAlias(), propertiesProvider);
		FileUtils.write(fileName, rdfSchema.asXML().getBytes());
		
		// mappings
		IMapping mapping = getMappings(dataSource.getAlias(), propertiesProvider);
		
		// upload
		String by = propertiesProvider.getIdentityProvider().getAuthenticatedUser();
		HttpSyncAdapter.uploadMeshDefinition(propertiesProvider.getMeshSyncServerURL(), propertiesProvider.getMeshID(), RssSyndicationFormat.INSTANCE.getName(), propertiesProvider.getMeshID(), null, null, by);
		HttpSyncAdapter.uploadMeshDefinition(propertiesProvider.getMeshSyncServerURL(), meshSourceId, RssSyndicationFormat.INSTANCE.getName(), description, rdfSchema, mapping, by);
	}

	private static String getSchemaFileName(String dataSource, PropertiesProvider propertiesProvider) {
		return propertiesProvider.getBaseDirectory() + "/" + dataSource + "_schema.xml";
	}
	
	public static IRDFSchema getSchema(MSAccessDataSourceMapping dataSource, PropertiesProvider propertiesProvider) throws Exception{
		String fileName = getSchemaFileName(dataSource.getAlias(), propertiesProvider);
		return MsAccessRDFSchemaGenerator.readSchema(fileName);
	}

	public static void downloadSchema(String url, String dataSource, PropertiesProvider propertiesProvider) throws Exception {
		String xmlSchema = HttpSyncAdapter.getSchema(url);

		String fileName = getSchemaFileName(dataSource, propertiesProvider);
		FileUtils.write(fileName, xmlSchema.getBytes());
	}
	
	public static IMapping getMappings(String dataSource, PropertiesProvider propertiesProvider) throws Exception{
		String mappingsFileName = getMappingsFileName(dataSource, propertiesProvider);
		IMapping mapping = new Mapping(XMLHelper.readDocument(new File(mappingsFileName)).getRootElement());
		return mapping;
	}

	private static String getMappingsFileName(String dataSource, PropertiesProvider propertiesProvider) {
		return propertiesProvider.getBaseDirectory() + "/" + dataSource + "_mappings.xml";
	}
	
	public static void downloadMappings(String url, String dataSource, PropertiesProvider propertiesProvider) throws Exception {
		String xmlMappings = HttpSyncAdapter.getMappings(url);
		
		String fileName = getMappingsFileName(dataSource, propertiesProvider);
		FileUtils.write(fileName, xmlMappings.getBytes());
	}

	public static void saveMappings(String dataSource, PropertiesProvider propertiesProvider, String title, String description, String location, String latitude, String longitud, String ill, String updateTimestamp) throws IOException {
		String templateFileName = propertiesProvider.getDefaultMappingsTemplateFileName();
		byte[] templateBytes = FileUtils.read(templateFileName);
		String template = new String(templateBytes, "UTF-8");		
		String xml = MessageFormat.format(
				template, 
				title, 
				description, 
				location,
				longitud,
				latitude,
				ill, 
				updateTimestamp);
		String fileName = getMappingsFileName(dataSource, propertiesProvider);
		FileUtils.write(fileName, xml.getBytes());
	}
}
