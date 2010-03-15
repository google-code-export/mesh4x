package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToRdfMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.Pfif;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

public class TestPFIFAdapter {

	public final static String NS_PFIF_URI = "http://zesty.ca/pfif/1.2";
	public final static String NS_PFIF_PREFIX = "pfif";
	
	
	
	@Test
	public void shouldSyncTwoPfifWithoutRdf(){
		String sourceFile = "c://pfif-srouce.xml";
		
		IPfifToPlainXmlMapping mappingSource = new PfifToPlainXmlMapping("person",
				PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME,null,null,
				new Pfif(sourceFile,"person",AtomSyndicationFormat.INSTANCE));
		
		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);
		
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", "http://localhost:8080/feed");
		
		PFIFAdapter sourceAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		
		String targetFile = "c://pfif-target.xml";
		IPfifToPlainXmlMapping mappingTarget = new PfifToPlainXmlMapping("person",
				PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME,null,null,
				new Pfif(targetFile,"person",AtomSyndicationFormat.INSTANCE));
		
		PFIFContentReader contentReaderTarget = new PFIFContentReader(mappingTarget);
		PFIFContentWriter contentWriterTarget = new PFIFContentWriter(null,mappingTarget,true,false);
		
		Feed feedTarget = new Feed("PFIF person data", "mesh4x sync", "http://localhost:8080/feed");
		
		PFIFAdapter targetAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedTarget,
				contentReaderTarget,contentWriterTarget,mappingTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
		
	}
	
	@Test
	public void shouldSyncTwoPfif(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		String entityName = "person";
		
		ISyncAdapter sourcAdapter = PFIFSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				"", NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		ISyncAdapter targetAdapter = PFIFSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				"", NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
	}
	
	@Test
	public void shouldSyncPfifAndMsExcel(){
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PFIFSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PFIFSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter sourceAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
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
	public void syncTwoPfifFileByRdf(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		String targetFile = "c://pfif-target.xml";
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PFIFSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PFIFSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter sourceAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		

		IPfifToPlainXmlMapping mappingTarget = new PfifToRdfMapping(targetFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PFIFSchema());
		Feed feedTarget = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderTarget = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterTarget = new PFIFContentWriter(null,mappingSource,true,false);

		
		
		PFIFAdapter targetAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedTarget,
				contentReaderTarget,contentWriterTarget,mappingTarget);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
	}
	
	@Test
	public void shouldSyncMsExcelAndPfif(){
		
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		File targetContentFile = new File("c://person.xls");
		
		
		MsExcel excel = new MsExcel(targetContentFile.getAbsolutePath());
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel,
				"person"+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, "person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, null,  rdfUrl);
		
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
		SplitAdapter sourceAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		String sourceFile = "c://source.xml";
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PFIFSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		
		PFIFAdapter targetAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
		
	}

	@Test
	public void shouldSyncMsExcelAndPfifCreateTarget(){
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		File targetContentFile = new File("c://pfiif-rss.xls");

		MsExcel excel = new MsExcel(targetContentFile.getAbsolutePath());
		MsExcelSyncRepository syncRepo =  new MsExcelSyncRepository(excel,
				"person"+"_sync", NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		IRDFSchema rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, "person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, null,  rdfUrl);
		
		MsExcelToRDFMapping mappings = new MsExcelToRDFMapping(rdfSchema);
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, mappings);
		SplitAdapter sourceAdapter = new SplitAdapter(syncRepo, contentAdapter, NullIdentityProvider.INSTANCE);
		
		String sourceFile = "c://target.xml";
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PFIFSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfSchema.getOntologyBaseClassUri());

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter targetAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		engine.synchronize();
	}

	@Test
	public void seperateTwoFile(){
		try {
			PFIFUtil.getOrCreatePersonAndNoteFileIfNecessary("c://source.xml", AtomSyndicationFormat.INSTANCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
