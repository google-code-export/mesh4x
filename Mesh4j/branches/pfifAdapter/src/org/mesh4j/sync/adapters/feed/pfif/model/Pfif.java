package org.mesh4j.sync.adapters.feed.pfif.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.PfifUtil;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class Pfif  implements IPfif{

	private ISyndicationFormat syndicationFormat = null;
	private List<PfifModel> models = null;
	private String pifiFeedSourceFile;
	private String entityName  ;
	
	public Pfif(String sourceFile,String entityName,ISyndicationFormat syndicationFormat){
		
		Guard.argumentNotNullOrEmptyString(sourceFile, "sourceFile");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		
		this.entityName = entityName;
		this.syndicationFormat = syndicationFormat;
		this.pifiFeedSourceFile = sourceFile;
		
		File pfifFile = new File(sourceFile);
		if(!pfifFile.exists() || 
				pfifFile.length() == 0){
			return ;
		}
		try {
			models = PfifUtil.getOrCreatePersonAndNoteFileIfNecessary(sourceFile, syndicationFormat);
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}
	
	public List<PfifModel> getPfifModels(){
		return models;
	}
	
	public String getPfifFeedSourceFile(){
		return pifiFeedSourceFile;
	}
	public String getSourceFile(){
		
		if(models == null || models.isEmpty()){
			return this.pifiFeedSourceFile;
		}
		for(PfifModel model : models){
			if(model.getEntityName().equals(entityName)){
				return  model.getFile().getAbsolutePath();
			}
		}
		return null;
	}
	
	public List<Item> getNonParticipantItems(){
		if(models != null && !models.isEmpty()){
			for(PfifModel model :this.models){
				if(!model.getEntityName().equals(entityName)){
					return model.getFeed().getItems();
				}
			}	
		}
		return null;
	}

	@Override
	public ISyndicationFormat getSyndicationFormat() {
		return this.syndicationFormat;
	}
	
}
