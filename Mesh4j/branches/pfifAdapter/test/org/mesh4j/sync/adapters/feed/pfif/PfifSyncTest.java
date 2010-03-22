package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToRdfMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.Pfif;
import org.mesh4j.sync.adapters.feed.pfif.schema.PfifSchema;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

public class PfifSyncTest {

	public final static String NS_PFIF_URI = "http://zesty.ca/pfif/1.2";
	public final static String NS_PFIF_PREFIX = "pfif";
	
	
	
	
	@Test
	public void shouldSyncTwoPfifWithoutRdf(){
		String sourceFile = "c://pfif-srouce.xml";
		
		IPfifToPlainXmlMapping mappingSource = new PfifToPlainXmlMapping("person",
				PfifSchema.PFIF_ENTITY_PERSON_ID_NAME,null,null);
		
		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);
		
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", "http://localhost:8080/feed");
		
		IPfif pfifSource = new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE);
		PfifAdapter sourceAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfifSource);
		
		
		String targetFile = "c://pfif-target.xml";
		IPfifToPlainXmlMapping mappingTarget = new PfifToPlainXmlMapping("person",
				PfifSchema.PFIF_ENTITY_PERSON_ID_NAME,null,null);
		
		PfifContentReader contentReaderTarget = new PfifContentReader(mappingTarget);
		PfifContentWriter contentWriterTarget = new PfifContentWriter(null,mappingTarget,true,false);
		
		Feed feedTarget = new Feed("PFIF person data", "mesh4x sync", "http://localhost:8080/feed");
		
		IPfif pfifTarget = new Pfif(targetFile,"person",AtomSyndicationFormat.INSTANCE);
		PfifAdapter targetAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedTarget,
				contentReaderTarget,contentWriterTarget,mappingTarget,pfifTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
	}
	
	@Test
	public void syncTwoPfifFileByRdf(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		String targetFile = "c://pfif-target.xml";
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PfifSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PfifSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PfifSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfifSource = new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE);
		
		PfifAdapter sourceAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfifSource);
		

		IPfifToPlainXmlMapping mappingTarget = new PfifToRdfMapping(targetFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PfifSchema());
		Feed feedTarget = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PfifContentReader contentReaderTarget = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterTarget = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfifTarget = new Pfif(targetFile,"person",AtomSyndicationFormat.INSTANCE);
		
		PfifAdapter targetAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedTarget,
				contentReaderTarget,contentWriterTarget,mappingTarget,pfifTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
	}
	
	
	@Test
	public void shouldSyncPfifAndMsExcel(){
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PfifSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PfifSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PfifSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfifSource = new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE);
		
		PfifAdapter sourceAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfifSource);
		
		File targetContentFile = new File("c://pfiif-rss.xls");
		
		MsExcel excel = new MsExcel(targetContentFile.getAbsolutePath());
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel,
				"person"+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(schemaSource);
		
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
		SplitAdapter splitAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,splitAdapter);
		engine.synchronize();
	}

	
	
	@Test
	public void shouldSyncMsExcelAndPfif(){
		
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		File targetContentFile = new File("c://pfiif-rss.xls");
		
		
		MsExcel excel = new MsExcel(targetContentFile.getAbsolutePath());
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel,
				"person"+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, "person", new String[]{PfifSchema.PFIF_ENTITY_PERSON_ID_NAME}, null,  rdfUrl);
		
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
		SplitAdapter sourceAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		String sourceFile = "c://pfif-srouce.xml";
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PfifSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfifTarget = new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE);
		
		PfifAdapter targetAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfifTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}

	@Test
	public void shouldSyncMsExcelAndPfifCreateTarget(){
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		File targetContentFile = new File("c://pfiif-rss.xls");

		MsExcel excel = new MsExcel(targetContentFile.getAbsolutePath());
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel,
				"person"+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, "person", new String[]{PfifSchema.PFIF_ENTITY_PERSON_ID_NAME}, null,  rdfUrl);
		
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
		SplitAdapter sourceAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		String sourceFile = "c://target.xml";
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PfifSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfSchema.getOntologyBaseClassUri());

		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfifTarget = new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE);
		
		PfifAdapter targetAdapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfifTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
	}

	
	
	
}
