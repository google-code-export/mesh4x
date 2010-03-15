package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

public class PFIFSyncTest {

	@Test
	public void shouldSyncTwoPfif(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String entityName = "person";
		String sourceFile = getTestDir() + "pfif-srouce.xml";
		String targetFile = getTestDir() + "pfif-target.xml";
		
		ISyncAdapter sourcAdapter = PFIFSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				"", NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		ISyncAdapter targetAdapter = PFIFSyncAdapterFactory.createSyncAdapter(targetFile, entityName, rdfUrl, 
				"", NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		SyncEngine engine = new SyncEngine(sourcAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	@Test
	public void shouldSyncTwoPfifAndCreateTarget(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = getTestDir() + "pfif-srouce.xml";
		String targetFile = getTestDir() + IdGenerator.INSTANCE.newID() + ".xml";
		String entityName = "person";
		
		ISyncAdapter sourcAdapter = PFIFSyncAdapterFactory.createSyncAdapter(sourceFile, entityName, rdfUrl, 
				"", NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE);
		
		IRDFSchema rdfSchema = (IRDFSchema)((PFIFAdapter)sourcAdapter).getSchema();
		
		ISyncAdapter targetAdapter = PFIFSyncAdapterFactory.createSyncAdapter(targetFile, 
				NullIdentityProvider.INSTANCE, AtomSyndicationFormat.INSTANCE,rdfSchema);
		
		SyncEngine engine = new SyncEngine(sourcAdapter,targetAdapter);
		List<Item> conflicts = engine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}
	
	@Test
	public void shouldSyncTwoPfifAndCreateTarget2(){
		String rdfUrl = "http://localhost:8080/mesh4x/feeds";
		String sourceFile = "pfif-srouce.xml";
		
		String entityName = "person";
		
		String pkPath =  PFIFSyncTest.class.getPackage().getName().replace('.', new Character('/'));
		String modPath = "src" +"/"+ pkPath + "/" + sourceFile;
		File file = new File(modPath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(file.getAbsolutePath());
	}
	
	
	public static String getTestDir(){
		return "testData/";
	}
	
	 	
	
}
