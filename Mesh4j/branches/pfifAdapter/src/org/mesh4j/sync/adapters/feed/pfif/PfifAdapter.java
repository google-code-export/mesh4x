package org.mesh4j.sync.adapters.feed.pfif;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.model.IPfif;
import org.mesh4j.sync.adapters.feed.pfif.model.PfifModel;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class PfifAdapter extends FeedAdapter implements ISyncAware{

	private IIdentityProvider identityProvider;
	private boolean dirty = false;
	private IPfifToPlainXmlMapping mapping;
	private IPfif pfif ;

	public PfifAdapter(IIdentityProvider identityProvider,
			IIdGenerator idGenerator, ISyndicationFormat syndicationFormat,
			Feed defaultFeed, IContentReader contentReader,
			IContentWriter contentWriter,IPfifToPlainXmlMapping mapping,IPfif pfif) {
		
		super(pfif.getSourceFile(),identityProvider,idGenerator,
				syndicationFormat,defaultFeed,contentReader,contentWriter);
		
		Guard.argumentNotNull(mapping, "mapping");
		this.mapping = mapping;
		this.identityProvider = identityProvider;
		this.pfif = pfif;
		}

	
	
	@Deprecated
	private  String getIdFromPayload(IRDFSchema rdfSchema, Element rdfElement, String defaultId) {
		RDFInstance rdfInstance = rdfSchema.createNewInstanceFromRDFXML(rdfElement);
		return rdfInstance.getId();
	}

	
	@Override
	public void add(Item item) {
		if(item.isDeleted()){
				update(item);
			return ;
		}
		
		Item oldItem = getItem(item);
		if(oldItem == null){
			this.getFeed().addItem(item);
		} else {
			this.getFeed().deleteItem(oldItem);
			this.getFeed().addItem(item);
		}
		this.setDirty();
	}

	
	private Item getItem(Item item){
		if(this.getFeed().getItems().size() == 0){
			return null;
		}
		IContent content = item.getContent();
		String id  = getId(content);
		for(int i = 0;i<this.getFeed().getItems().size(); i ++){
			Item itemFromFeed = this.getFeed().getItems().get(i);
			String idLoaded  = getId(itemFromFeed.getContent());
			if(idLoaded.equals(id)){
				return itemFromFeed;
			} 
		}
		return null;
	}
	
	
	@Deprecated
	private Item getItemByRDF(Item item){
		if(this.getFeed().getItems().size() == 0){
			return null;
		}
		IContent content = item.getContent();
		IRDFSchema schema = (IRDFSchema)this.mapping.getSchema();
		String id  = getIdFromPayload(schema, content.getPayload(),item.getContent().getId());
		for(int i = 0;i<this.getFeed().getItems().size(); i ++){
			Item itemFromFeed = this.getFeed().getItems().get(i);
			String idRDF  = getIdFromPayload(schema, itemFromFeed.getContent().getPayload(),item.getContent().getId());
			if(idRDF.equals(id)){
				return itemFromFeed;
			} 
		}
		return null;
	}
	
	
	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		List<Item> resultOfRef = new LinkedList<Item>(this.getFeed().getItems());
		
		for(int i = 0;i<resultOfRef.size(); i ++){
			Item item = resultOfRef.get(i);
			boolean dateOk = SinceLastUpdateFilter.applies(item, since);
			if(filter.applies(item) && dateOk){
				Item cloneItem = item.clone();
				updateSyncIfContentChanged(cloneItem);
				result.add(cloneItem);
			}
		}
		return result;
	}
	
	private void updateSyncIfContentChanged(Item item) {
		if(item.isDeleted()){
			item.getSync().update(this.getAuthenticatedUser(), new Date(), item.getSync().isDeleted());
			updateContent(item);
			return ;
		}
		
		if(!(item.getContent() instanceof XMLContent)){
			updateContent(item);
			return ;
//			IdentifiableContent identifiableContent = (IdentifiableContent)item.getContent();
//			identifiableContent.getVersion()
//			identifiableContent.
		}
		
		XMLContent content = (XMLContent) item.getContent();
		Sync sync = item.getSync();

			if(!content.getDescription().contains("Version:")){
				content.setDescription(getDefaultDescription(item));
				updateContent(item);
			}

			if(content.getVersion() != Integer.parseInt(content.getDescription().substring(content.getDescription().indexOf("Version: ")+9))){
				sync.update(this.getAuthenticatedUser(), new Date(), sync.isDeleted());
				content.setDescription(getDefaultDescription(item));
				updateContent(item);
			}
			
		}

	private String getDefaultDescription(Item item) {
		return "Id: " + item.getContent().getId() + " Version: " + item.getContent().getVersion();
	}

	
	private String getId(IContent content){
		return mapping.getId(content.getPayload());
	}


	
	public void updateContent(Item item) {
		Guard.argumentNotNull(item, "item");
		Item originalItem = null;
		for(int i = 0;i<this.getFeed().getItems().size(); i ++){
			Item item1 = this.getFeed().getItems().get(i);
			if(item1.getSyncId().equals(item.getSyncId())){
				originalItem = item1.clone();
			}
		}
		
		if(originalItem != null){
			this.getFeed().deleteItem(originalItem);
		
			Item newItem;
			if (item.getSync().isDeleted()){
				newItem = new Item(new NullContent(item.getSyncId()), item.getSync().clone());
			}else{
				newItem = item.clone();
			}
			
			this.getFeed().addItem(newItem);
			this.setDirty();
		}
	}
	
	@Override
	public Item get(String id) {
		for (Item item : this.getFeed().getItems()) {
			if(item.getSyncId().equals(id)){
				Item cloneItem = item.clone();
				updateSyncIfContentChanged(cloneItem);
				return cloneItem;
			}
		}
		return null;
	}


	@Override
	public void delete(String id) {
		Item item = this.get(id);
		if(item != null){
			this.getFeed().deleteItem(item);
			this.setDirty();
		}		
	}


	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}


	@Override
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}

	public ISchema getSchema() {
		return mapping.getSchema();
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");

		Item originalItem = get(item.getSyncId());
		if(originalItem != null){
			this.getFeed().deleteItem(originalItem);
		
			Item newItem;
			if (item.getSync().isDeleted()){
				newItem = new Item(new NullContent(item.getSyncId()), item.getSync().clone());
			}else{
				newItem = item.clone();
			}
			this.getFeed().addItem(newItem);
			this.setDirty();
		}
	}


	public IPfif getPfif(){
		return this.pfif;
	}
	@Override
	public void beginSync() {
		// TODO Auto-generated method stub
	}


	@Override
	public void endSync() {
		if(this.dirty){
			this.flush();
			this.dirty = false;
		}	
		//do clean up operation if necessary.
		cleanUp();
	}
	
	private void cleanUp(){
		if(this.getPfif() != null && 
				this.getPfif().getPfifModels() != null){
			File feedSourceFile = new File(getFeedSourceFile());
			for(PfifModel model :this.getPfif().getPfifModels()){
				if(!isEqual(model.getFile(),feedSourceFile)){
					model.getFile().delete();
				}
			}
		}
		
	}
	
	
	private boolean isEqual(File source,File target){
		if(!source.exists() || !target.exists() ){
			Guard.throwsException("INVALID_FILE");
		}
		return source.getName().equals(target.getName()) && 
				source.getParent().equals(target.getParent());
	}
	
	private String getFeedSourceFile(){
		String sourceFile = "";
		if(this.getPfif() == null){
			sourceFile = this.getFile().getAbsolutePath();
		} else {
			sourceFile = this.getPfif().getPfifFeedSourceFile();
		}
		return sourceFile;
	}
	
	public void flush() {
		//adding items which where not participated in sync session.
		if(this.getPfif() != null){
			List<Item> nonParticipantItems = this.getPfif().getNonParticipantItems();
			if(nonParticipantItems != null){
				this.getFeed().addItems(nonParticipantItems);	
			}
		} 
		
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(getFeedSourceFile()), OutputFormat.createPrettyPrint());
			this.getFeedWriter().write(writer, this.getFeed());
		}  catch (Exception e) {
			throw new MeshException(e);
		} 
	 }
	
	public void setDirty(){
		this.dirty = true;
	}
	
}
