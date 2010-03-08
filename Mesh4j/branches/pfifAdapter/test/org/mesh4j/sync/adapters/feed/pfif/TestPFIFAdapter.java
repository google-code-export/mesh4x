package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPFIFToRDFMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PFIFToRDFMapping;
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
	public void shouldSyncPfifAndMsExcel(){
		
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		IRDFSchema schemaSource = PFIFToRDFMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PFIFSchema());
		
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
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
	public void syncTwoPfifFile(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "c://pfif-srouce.xml";
		String targetFile = "c://pfif-target.xml";
		IRDFSchema schemaSource = PFIFToRDFMapping.extractRDFSchema(sourceFile, 
				AtomSyndicationFormat.INSTANCE, 
				"person", new String[]{PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME}, 
				"test", rdfUrl, new PFIFSchema());
		
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(sourceFile,AtomSyndicationFormat.INSTANCE,
				schemaSource, new PFIFSchema());
		Feed feedSource = new Feed("PFIF person data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter sourceAdapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		

		IPFIFToRDFMapping mappingTarget = new PFIFToRDFMapping(targetFile,AtomSyndicationFormat.INSTANCE,
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
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PFIFSchema());
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
		
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(sourceFile,AtomSyndicationFormat.INSTANCE,rdfSchema, new PFIFSchema());
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
