package org.mesh4j.demos;

import java.io.IOException;

import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;

public class EpiinfoDemoTest {

	
	@Test
	public void shouldConfigureServerWithoutSchema() throws IOException{
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		//String serverUrl = "http://sync.staging.instedd.org:8080/mesh4x/feeds";
		
		String mdbFileName = TestHelper.makeFileAndDeleteIfExists("epiinfo.mdb").getCanonicalPath(); 
		
		String sourceFileName = this.getClass().getResource("epiinfo.mdb").getFile();
		FileUtils.copyFile(sourceFileName, mdbFileName);
		SplitAdapter adapter = MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(mdbFileName, "Oswego", null, TestHelper.baseDirectoryForTest(), new LoggedInIdentityProvider());
		
		String xml = "<mappings>"+
			"<item.title>patient name: {Oswego/Name}</item.title>"+
			"<item.description>adress: {Oswego/Address}</item.description>"+
			"<geo.location>{geoLocation(Oswego/Address)}</geo.location>"+
			"<geo.longitude>{geoLongitude(Oswego/Address)}</geo.longitude>"+
			"<geo.latitude>{geoLatitude(Oswego/Address)}</geo.latitude>"+
			"<patient.ill>{Oswego/ILL}</patient.ill>"+
			"<patient.updateTimestamp>{Oswego/DateOnset}</patient.updateTimestamp>"+ 
			"</mappings>";
		Element element = XMLHelper.parseElement(xml);
		IMapping mappings = new Mapping(element);
		
		HttpSyncAdapter.uploadMeshDefinition(serverUrl, "InSTEDDLab", RssSyndicationFormat.NAME, "my mesh", null, null, LoggedInIdentityProvider.getUserName());
		
		HttpSyncAdapter.uploadMeshDefinition(serverUrl, "InSTEDDLab/OswegoNoRDFSchema", RssSyndicationFormat.NAME, "my mesh", null, mappings, LoggedInIdentityProvider.getUserName());
		
		String url = serverUrl + "/InSTEDDLab/OswegoNoRDFSchema";
		HttpSyncAdapter httpAdapter = HttpSyncAdapterFactory.createSyncAdapter(url, new LoggedInIdentityProvider());
		
		SyncEngine syncEngine = new SyncEngine(httpAdapter, adapter);
		TestHelper.assertSync(syncEngine);
		
	}
	
	@Test
	public void shouldConfigureServer() throws IOException{
		String serverUrl = "http://localhost:8080/mesh4x/feeds";
		//String serverUrl = "http://sync.staging.instedd.org:8080/mesh4x/feeds";
		
		String mdbFileName = TestHelper.makeFileAndDeleteIfExists("epiinfo.mdb").getCanonicalPath(); 
		
		String sourceFileName = this.getClass().getResource("epiinfo.mdb").getFile();
		FileUtils.copyFile(sourceFileName, mdbFileName);
		SplitAdapter adapter = MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(mdbFileName, "Oswego", serverUrl, TestHelper.baseDirectoryForTest(), new LoggedInIdentityProvider());
		ISchema schema = ((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		
		String xml = "<mappings>"+
			"<item.title>patient name: {Oswego/Name}</item.title>"+
			"<item.description>adress: {Oswego/Address}</item.description>"+
			"<geo.location>{geoLocation(Oswego/Address)}</geo.location>"+
			"<geo.longitude>{geoLongitude(Oswego/Address)}</geo.longitude>"+
			"<geo.latitude>{geoLatitude(Oswego/Address)}</geo.latitude>"+
			"<patient.ill>{Oswego/ILL}</patient.ill>"+
			"<patient.updateTimestamp>{Oswego/DateOnset}</patient.updateTimestamp>"+ 
			"</mappings>";
		Element element = XMLHelper.parseElement(xml);
		IMapping mappings = new Mapping(element);
		
		HttpSyncAdapter.uploadMeshDefinition(serverUrl, "InSTEDDLab", RssSyndicationFormat.NAME, "my mesh", null, null, LoggedInIdentityProvider.getUserName());
		
		HttpSyncAdapter.uploadMeshDefinition(serverUrl, "InSTEDDLab/Oswego", RssSyndicationFormat.NAME, "my mesh", schema, mappings, LoggedInIdentityProvider.getUserName());
		
		String url = serverUrl + "/InSTEDDLab/Oswego";
		HttpSyncAdapter httpAdapter = HttpSyncAdapterFactory.createSyncAdapter(url, new LoggedInIdentityProvider());
		
		SyncEngine syncEngine = new SyncEngine(httpAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
	}
}
