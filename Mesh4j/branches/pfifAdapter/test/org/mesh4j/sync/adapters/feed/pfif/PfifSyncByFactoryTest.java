package org.mesh4j.sync.adapters.feed.pfif;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.Pfif;
import org.mesh4j.sync.adapters.feed.pfif.schema.PfifSchema;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

public class PfifSyncByFactoryTest {

	@Test
	public void shouldSyncTwoPfifBySyncAdatperFactory() throws Exception{
		
		String entityName = "person";
		String sourceFile = getTestDir() + "pfif-srouce.xml";
		String targetFile = getTestDir() + "pfif-target.xml";
		
		PfifSyncAdapterFactory sourcAdapter = new PfifSyncAdapterFactory();
		String sourceDefinition = sourcAdapter.createAtomSourceDefinition(sourceFile, entityName, 
				PfifSchema.PFIF_ENTITY_PERSON_ID_NAME);
		
		ISyncAdapter sourceAdapter = sourcAdapter.createSyncAdapter("person", sourceDefinition, NullIdentityProvider.INSTANCE);
		
		String targetDefinition = sourcAdapter.createAtomSourceDefinition(targetFile, entityName, 
				PfifSchema.PFIF_ENTITY_PERSON_ID_NAME);
		
		ISyncAdapter targetAdapter = sourcAdapter.createSyncAdapter("person", targetDefinition, NullIdentityProvider.INSTANCE);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	@Test
	public void shouldSyncTwoPfifByRdfSyncAdatperFactory() throws Exception{
		
		String entityName = "person";
		String sourceFile = getTestDir() + "pfif-srouce.xml";
		String targetFile = getTestDir() + "pfif-target.xml";
		String rdfUrl = "http://localhost:8080/mesh4x/feed";
		
		
		ISyncAdapter sourceAdapter = PfifRdfSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				null, NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		IRDFSchema schema = (IRDFSchema)((PfifAdapter)sourceAdapter).getSchema();
		
		ISyncAdapter targetAdapter = PfifRdfSyncAdapterFactory.createSyncAdapter(targetFile,NullIdentityProvider.INSTANCE, 
				AtomSyndicationFormat.INSTANCE,
				schema);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	
	@Test
	public void shouldSyncTwoPfiByRdfSyncAdapterFacytoryfAndCreateTarget(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = getTestDir() + "pfif-srouce.xml";
		String targetFile = getTestDir() + IdGenerator.INSTANCE.newID() + ".xml";
		String entityName = "person";
		
		ISyncAdapter sourceAdapter = PfifRdfSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				null, NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		IRDFSchema rdfSchema = (IRDFSchema)((PfifAdapter)sourceAdapter).getSchema();
		
		ISyncAdapter targetAdapter = PfifRdfSyncAdapterFactory.createSyncAdapter(targetFile,NullIdentityProvider.INSTANCE, 
				AtomSyndicationFormat.INSTANCE,
				rdfSchema);
		
		SyncEngine engine = new SyncEngine(sourceAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	
	@Test
	public void shouldSyncMultiFileMode(){
		
		String source1 = "c://pfif-srouce1.xml";
		String source2 = "c://pfif-srouce2.xml";
		
		IPfif pfif = new Pfif(source1,"person",AtomSyndicationFormat.INSTANCE);
		List<IPfif> listSource = new LinkedList<IPfif>();
		listSource.add(pfif);
		pfif = new Pfif(source2,"person",AtomSyndicationFormat.INSTANCE);
		listSource.add(pfif);
		
		InMemorySyncAdapter adapterOpaqueSource = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterSource = PfifSyncAdapterFactory.createSyncAdapterForMultiFiles(listSource, NullIdentityProvider.INSTANCE, adapterOpaqueSource);
		
		
		String target1 = "c://pfif-target1.xml";
		String target2 = "c://pfif-target2.xml";
		List<IPfif> listTarget = new LinkedList<IPfif>();
		pfif = new Pfif(target1,"person",AtomSyndicationFormat.INSTANCE);
		listTarget.add(pfif);
		pfif = new Pfif(target2,"person",AtomSyndicationFormat.INSTANCE);
		listTarget.add(pfif);
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		ISyncAdapter adapterTarget = PfifSyncAdapterFactory.createSyncAdapterForMultiFiles(listTarget, NullIdentityProvider.INSTANCE, adapterOpaqueTarget);
	}
	
	
	public static String getTestDir(){
		return "testData/";
	}
	
	 	
	
}
