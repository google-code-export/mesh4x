package org.mesh4j.ektoo.controller;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.ektoo.model.PFIFModel;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.PfifAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class PFIFUIController extends FeedUIController{

	
	public static String ENTITY_NAMES = "EntityNames";
	public static final String FILE_NAME_PROPERTY = "FileName";
	public static final String SYNDICATION_FORMAT = "SyndicationFormat";
	
	
	

	public PFIFUIController( boolean acceptsCreateDataset) {
		super( acceptsCreateDataset);
	}
	
	public void changeEntityNames(String[] entityNames) {
		setModelProperty(ENTITY_NAMES, entityNames);
	}
	
	public void changeSyndicationFormat(ISyndicationFormat syndicationFormat){
		setModelProperty(SYNDICATION_FORMAT, syndicationFormat instanceof AtomSyndicationFormat);
	}

	
	@Override
	public ISyncAdapter createAdapter() {
		PFIFModel model = (PFIFModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String feedFileName = model.getFileName();
		if (feedFileName == null || feedFileName.trim().length() == 0){
			return null;
		}
		return getAdapterBuilder().createPfifSyncAdapter(model.getFileName(), 
				model.getEntityNames()[0], model.getSyndicationFormat());
	}

	@Override
	public ISyncAdapter createAdapter(List<IRDFSchema> schemas) {
		PFIFModel model = (PFIFModel) this.getModel();
		if (model == null){
			return null;
		}
		
		String feedFileName = model.getFileName();
		if (feedFileName == null || feedFileName.trim().length() == 0){
			return null;
		}
		return getAdapterBuilder().createPfifSyncAdapter(model.getFileName(),model.getSyndicationFormat(),schemas.get(0));
	}

	@Override
	public List<IRDFSchema> fetchSchema(ISyncAdapter adapter) {
		ArrayList<IRDFSchema> schemas = new ArrayList<IRDFSchema>();
		IRDFSchema schema = (IRDFSchema)((PfifAdapter)adapter).getSchema();
		schemas.add(schema);
		return schemas;
	}

}
