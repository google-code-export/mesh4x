package org.mesh4j.sync.adapters.feed.pfif;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.Pfif;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;

public class PfifSyncAdapterFactory implements ISyncAdapterFactory{

	public final static String SOURCE_TYPE_ATOM = AtomSyndicationFormat.INSTANCE.getName();
	public final static String SOURCE_TYPE_RSS = RssSyndicationFormat.INSTANCE.getName();
	
	
	public PfifSyncAdapterFactory(){
		super();
	}
	
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
		String[] elements = sourceDefinition.substring(sourceDefinition.indexOf(":") + 1, 
													sourceDefinition.length()).split("@");
		
		String format = sourceDefinition.substring(0, sourceDefinition.indexOf(":"));
		String pfifFile = elements[0];
		String entityName = elements[1];
		String entityId = elements[2];
		ISyndicationFormat syndicationFormat = getSyndicationFormat(format);
		return createSyncAdapter(pfifFile,entityName,entityId,identityProvider,syndicationFormat);
		
	}
	
	private static ISyndicationFormat getSyndicationFormat(String name){
		if(name.equals(SOURCE_TYPE_ATOM)){
			return AtomSyndicationFormat.INSTANCE;
		} else if(name.equals(SOURCE_TYPE_RSS)){
			return RssSyndicationFormat.INSTANCE;
		}
		return null;
	}
	
	public static String createAtomSourceDefinition(String pfifFile, String entityName, String idColumn){

		String sourceDefinition = SOURCE_TYPE_ATOM + ":" + pfifFile + "@" + entityName + "@" + idColumn;
		return sourceDefinition;
	}
	
	public static String createRssSourceDefinition(String pfifFile, String entityName, String idColumn){
		String sourceDefinition = SOURCE_TYPE_RSS + ":" + pfifFile + "@" + entityName + "@" + idColumn;;
		return sourceDefinition;
	}
	
	
	
	private  ISyncAdapter createSyncAdapter(String pfifFile, String entityName,String entityId,
			IIdentityProvider identityProvider, ISyndicationFormat syndicationFormat){
		
		IPfif pfif = new Pfif(pfifFile,entityName,syndicationFormat);
		IPfifToPlainXmlMapping mappingSource = new PfifToPlainXmlMapping(entityName,entityId,null,null);
		Feed feedSource = new Feed("PFIF " +  entityName +" data", "mesh4x sync", "");
		
		PfifContentReader contentReaderSource = new PfifContentReader(mappingSource);
		PfifContentWriter contentWriterSource = new PfifContentWriter(null,mappingSource,true,false);

		PfifAdapter adapter = new PfifAdapter(NullIdentityProvider.INSTANCE,
				IdGenerator.INSTANCE,AtomSyndicationFormat.INSTANCE,feedSource,
				contentReaderSource,contentWriterSource,mappingSource,pfif);
		
		return adapter;
	}
	
	//TODO this source type should return the type of pfif format(atom or rss)
	@Override
	public String getSourceType() {
		return "Pfif";
	}
	
	
	
	
	

	
}
