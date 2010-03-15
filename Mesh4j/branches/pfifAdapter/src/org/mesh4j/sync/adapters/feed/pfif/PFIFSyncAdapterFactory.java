package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToRdfMapping;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class PFIFSyncAdapterFactory implements ISyncAdapterFactory{

	public final static String SOURCE_TYPE_ATOM = AtomSyndicationFormat.INSTANCE.getName();
	public final static String SOURCE_TYPE_RSS = RssSyndicationFormat.INSTANCE.getName();
	
	
	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return sourceDefinition != null && 
		(sourceDefinition.startsWith(SOURCE_TYPE_ATOM) ||
				sourceDefinition.startsWith(SOURCE_TYPE_RSS))&&
		sourceDefinition.toUpperCase().endsWith(".XML");
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias,
			String sourceDefinition, IIdentityProvider identityProvider)
			throws Exception {
		String[] elements = sourceDefinition.substring(sourceDefinition.indexOf(":"), 
				sourceDefinition.length()).split("@");
		String pfifFileName = elements[0];
		String entityName = elements[1];
		String rdfUrl = elements[2];
		ISyndicationFormat syndicationFormat = getSyndicationFormat(elements[3]);
		
		return createSyncAdapter(pfifFileName, entityName, rdfUrl, "", identityProvider, syndicationFormat);
	}
	
	private static ISyndicationFormat getSyndicationFormat(String name){
		if(name.equals(SOURCE_TYPE_ATOM)){
			return AtomSyndicationFormat.INSTANCE;
		} else if(name.equals(SOURCE_TYPE_RSS)){
			return RssSyndicationFormat.INSTANCE;
		}
		return null;
	}
	
	public static String createAtomSourceDefinition(String pfifFile, String entityName, String idColumn,String rdfUrl){
		File file = new File(pfifFile);
		String sourceDefinition = SOURCE_TYPE_ATOM + ":" + file.getName() + "@" + entityName + "@" + rdfUrl;
		return sourceDefinition;
	}
	
	public static String createRssSourceDefinition(String pfifFile, String entityName, String idColumn,String rdfUrl){
		File file = new File(pfifFile);
		String sourceDefinition = SOURCE_TYPE_RSS + ":" + file.getName() + "@" + entityName +"@" + rdfUrl;
		return sourceDefinition;
	}
	
	
	public static ISyncAdapter createSyncAdapter(String pfifFile, IIdentityProvider identityProvider, 
			ISyndicationFormat syndicationFormat,IRDFSchema rdfSchema){
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(pfifFile,syndicationFormat,rdfSchema, new PFIFSchema());
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
		return SOURCE_TYPE_ATOM;
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
	public static PFIFAdapter createSyncAdapter(String fileName, String entityName ,String rdfUrl,
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
		
		IRDFSchema schemaSource = PfifToRdfMapping.extractRDFSchema(fileName, 
				syndicationFormat, 
				entityName, new String[]{id}, 
				lastUpdateColumn, rdfUrl, new PFIFSchema());
		
		IPfifToPlainXmlMapping mappingSource = new PfifToRdfMapping(fileName,syndicationFormat,schemaSource, new PFIFSchema());
		Feed feedSource = new Feed("PFIF "+ entityName + " data", "mesh4x sync", rdfUrl);

		PFIFContentReader contentReaderSource = new PFIFContentReader(mappingSource);
		PFIFContentWriter contentWriterSource = new PFIFContentWriter(null,mappingSource,true,false);

		PFIFAdapter pfifAdapter = new PFIFAdapter(identityProvider,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource);
		
		return pfifAdapter;
	}
	

	
}
