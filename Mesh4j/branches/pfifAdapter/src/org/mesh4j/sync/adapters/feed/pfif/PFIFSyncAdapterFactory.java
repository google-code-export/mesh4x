package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPFIFToRDFMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PFIFToRDFMapping;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class PFIFSyncAdapterFactory implements ISyncAdapterFactory{

	public final static String SOURCE_TYPE = RssSyndicationFormat.INSTANCE.getName();
	
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias,
			String sourceDefinition, IIdentityProvider identityProvider)
			throws Exception {
		// TODO Auto-generated method stub
		//TODO (raju)
		return null;
	}
	public static ISyncAdapter createSyncAdapter(String pfifFile, IIdentityProvider identityProvider, 
			ISyndicationFormat syndicationFormat,IRDFSchema rdfSchema){
		
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(pfifFile,syndicationFormat,rdfSchema, new PFIFSchema());
		Feed feedSource = new Feed("PFIF " +  rdfSchema.getOntologyClassName() +" data", 
				"mesh4x sync", rdfSchema.getOntologyBaseClassUri());
		
		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter adapter = new PFIFAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		return adapter;
	}
	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}
	
	
	
	
	
	public static PFIFAdapter createPfIfSyncAdapter(String fileName, String entityName ,String rdfUrl,
			String lastUpdateColumn,IIdentityProvider identityProvider,
			ISyndicationFormat syndicationFormat) {
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		File srcFile = new File(fileName);
		if(!srcFile.exists() && srcFile.length() <= 0 ){
			Guard.throwsException("INVALID_FILE");
		}
		
		String id = "";
		if(entityName.equals(PFIFSchema.QNAME_PERSON.getName())){
			id = PFIFSchema.PFIF_ENTITY_PERSON_ID_NAME;
		} else if(entityName.equals(PFIFSchema.QNAME_NOTE.getName())){
			id = PFIFSchema.PFIF_ENTITY_NOTE_ID_NAME;
		}
		
		IRDFSchema schemaSource = PFIFToRDFMapping.extractRDFSchema(fileName, 
				syndicationFormat, 
				entityName, new String[]{id}, 
				lastUpdateColumn, rdfUrl, new PFIFSchema());
		
		IPFIFToRDFMapping mappingSource = new PFIFToRDFMapping(fileName,syndicationFormat,schemaSource, new PFIFSchema());
		Feed feedSource = new Feed("PFIF "+ entityName + " data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter pfifAdapter = new PFIFAdapter(identityProvider,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		return pfifAdapter;
	}
	

	
}
