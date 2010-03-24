package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToRdfMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.Pfif;
import org.mesh4j.sync.adapters.feed.pfif.schema.PfifSchema;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class PfifRdfSyncAdapterFactory {

	
	public static ISyncAdapter createSyncAdapter(String pfifFile, IIdentityProvider identityProvider, 
			ISyndicationFormat syndicationFormat,IRDFSchema rdfSchema){
		
		IPfif pfif = new Pfif(pfifFile,rdfSchema.getOntologyClassName(),syndicationFormat);
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(pfifFile,syndicationFormat,rdfSchema, new PfifSchema());
		Feed feedSource = new Feed("PFIF " +  rdfSchema.getOntologyClassName() +" data", 
				"mesh4x sync", rdfSchema.getOntologyBaseClassUri());
		
		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		PfifAdapter adapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfif);
		
		return adapter;
	}
	
	/**
	 * this method assumes provided file is exist as because it extract
	 * schema from the provided source file.
	 * @param fileName
	 * @param entityName
	 * @param rdfUrl
	 * @param lastUpdateColumn
	 * @param identityProvider
	 * @param syndicationFormat
	 * @return
	 */
	public static ISyncAdapter createSyncAdapter(String fileName, String entityName ,String rdfUrl,
			String lastUpdateColumn,IIdentityProvider identityProvider,
			ISyndicationFormat syndicationFormat) {
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		File srcFile = new File(fileName);
		if(!srcFile.exists() && srcFile.length() <= 0 ){
			Guard.throwsException("INVALID_FILE");
		}
		
		String id = "";
		if(entityName.equals(PfifSchema.QNAME_PERSON.getName())){
			id = PfifSchema.PFIF_ENTITY_PERSON_ID_NAME;
		} else if(entityName.equals(PfifSchema.QNAME_NOTE.getName())){
			id = PfifSchema.PFIF_ENTITY_NOTE_ID_NAME;
		}
		
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(fileName, 
				syndicationFormat, 
				entityName, new String[]{id}, 
				lastUpdateColumn, rdfUrl, new PfifSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(fileName,syndicationFormat,schemaSource, new PfifSchema());
		Feed feedSource = new Feed("PFIF "+ entityName + " data", "mesh4x sync", rdfUrl);

		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		IPfif pfif = new Pfif(fileName,entityName,syndicationFormat);
		PfifAdapter pfifAdapter = new PfifAdapter(identityProvider,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfif);
		
		return pfifAdapter;
	}
	
	
	
}
