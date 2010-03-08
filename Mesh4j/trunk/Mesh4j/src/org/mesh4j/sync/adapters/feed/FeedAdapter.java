package org.mesh4j.sync.adapters.feed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


// TODO (JMT) Streaming xml.
public class FeedAdapter extends AbstractSyncAdapter implements ISyncAware{

	// MODEL VARIABLES
	private File feedFile;
	private Feed feed;
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	private IIdentityProvider identityProvider;
	private boolean dirty = false;
	
	// BUSINESS METHODS
	public FeedAdapter(String fileName, IIdentityProvider identityProvider, IIdGenerator idGenerator, ISyndicationFormat syndicationFormat, Feed defaultFeed){
		Guard.argumentNotNull(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");

		this.identityProvider = identityProvider;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, ContentReader.INSTANCE);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, ContentWriter.INSTANCE);
		
		this.feedFile = new File(fileName);
		if(!this.feedFile.exists()){
			this.feed = defaultFeed;
			try{
				if(!this.feedFile.getParentFile().exists()){
					this.feedFile.getParentFile().mkdirs();
				}
				this.feedFile.createNewFile();
				this.flush();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		} else {
			if(this.feedFile.length() == 0){
				this.feed = defaultFeed;
			} else {
				SAXReader reader = new SAXReader();
				Document document;
				try {
					document = reader.read(this.feedFile);
					this.feed = this.feedReader.read(document);
				} catch (Exception e) {
					throw new MeshException(e);
				}
			}
		}
	}
	
	
	/**
	 * added for generic purpose,any rss/atom data srouce wish to behave as feed
	 * can extend FeedAdapter and provide implementation of IContentReader
	 * and IContentWriter
	 * 
	 * @param fileName,the file name of the rss/atom feed source
	 * @param identityProvider
	 * @param idGenerator
	 * @param syndicationFormat
	 * @param defaultFeed
	 * @param contentReader,it reads specific rss/atom data source according to custom schema. 
	 * @param contentWriter,it writes specific rss/atom data according to custom schema. 
	 */
	public FeedAdapter(String fileName, IIdentityProvider identityProvider, 
			IIdGenerator idGenerator, ISyndicationFormat syndicationFormat, 
			Feed defaultFeed,IContentReader contentReader,IContentWriter contentWriter){
		
		Guard.argumentNotNull(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(contentReader, "contentReader");
		Guard.argumentNotNull(contentWriter, "contentWriter");

		this.identityProvider = identityProvider;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, contentReader);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, contentWriter);
		
		this.feedFile = new File(fileName);
		if(!this.feedFile.exists()){
			this.feed = defaultFeed;
			try{
				if(!this.feedFile.getParentFile().exists()){
					this.feedFile.getParentFile().mkdirs();
				}
				this.feedFile.createNewFile();
				this.flush();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		} else {
			if(this.feedFile.length() == 0){
				this.feed = defaultFeed;
			} else {
				
				InputStreamReader inputStreamReader = null;
				try {
					inputStreamReader = new InputStreamReader(new FileInputStream(this.feedFile));
				} catch (FileNotFoundException e) {
					throw new MeshException(e);
				}
				
				SAXReader reader = new SAXReader();
				Document document;
				try {
					document = reader.read(inputStreamReader);
					this.feed = this.feedReader.read(document);
				} catch (Exception e) {
					e.printStackTrace();
					throw new MeshException(e);
				}
			}
		}
	}
	
	
	public FeedAdapter(String fileName, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		Guard.argumentNotNull(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");

		this.feedFile = new File(fileName);
		
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(this.feedFile);
	
			ISyndicationFormat syndicationFormat = RssSyndicationFormat.INSTANCE;
			if(AtomSyndicationFormat.isAtom(document)){
				syndicationFormat = AtomSyndicationFormat.INSTANCE;
			}
			
			this.identityProvider = identityProvider;
			this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, ContentReader.INSTANCE);
			this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, ContentWriter.INSTANCE);
			
			this.feed = this.feedReader.read(document);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public FeedAdapter(File file, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		
		Guard.argumentNotNull(file, "file");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.feedFile = file;
		this.identityProvider = identityProvider;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, ContentReader.INSTANCE);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, ContentWriter.INSTANCE);
		try {
			this.feed = this.feedReader.read(file);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public FeedAdapter(File file, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IIdGenerator idGenerator, Feed feed){
		
		Guard.argumentNotNull(file, "file");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(feed, "feed");
		
		this.feedFile = file;
		this.identityProvider = identityProvider;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator, ContentReader.INSTANCE);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider, ContentWriter.INSTANCE);
		this.feed = feed;
		
		if(!file.exists()){
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			this.flush();
		}
	}
	
	@Override
	public void add(Item item) {
		this.feed.addItem(item);
		this.setDirty();
	}

	@Override
	public void delete(String id) {
		Item item = this.get(id);
		if(item != null){
			this.feed.deleteItem(item);
			this.setDirty();
		}		
	}

	@Override
	public Item get(String id) {
		for (Item item : this.feed.getItems()) {
			if(item.getSyncId().equals(id)){
				return item;
			}
		}
		return null;
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		ArrayList<Item> result = new ArrayList<Item>();
		for (Item item : this.feed.getItems()) {
			boolean dateOk = SinceLastUpdateFilter.applies(item, since);
			if(filter.applies(item) && dateOk){
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public String getFriendlyName() {
		return MessageTranslator.translate(this.getClass().getName());
	}
	
	@Override
	public void update(Item item) {
		Guard.argumentNotNull(item, "item");

		Item originalItem = get(item.getSyncId());
		if(originalItem != null){
			this.feed.deleteItem(originalItem);
		
			Item newItem;
			if (item.getSync().isDeleted()){
				newItem = new Item(new NullContent(item.getSyncId()), item.getSync().clone());
			}else{
				newItem = item.clone();
			}
			
			this.feed.addItem(newItem);
			this.setDirty();
		}
	}
	
	public void flush() {
		try {
			XMLWriter writer = new XMLWriter(new FileWriter(this.feedFile), OutputFormat.createPrettyPrint());
			this.feedWriter.write(writer, this.feed);	
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public Feed getFeed(){
		return this.feed;
	}

	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}

	public FeedWriter getFeedWriter() {
		return this.feedWriter;
	}
	public FeedReader getFeedReader() {
		return this.feedReader;
	}
	
	public void refresh(){
		if(this.feedFile.length() == 0){
			this.feed = new Feed();
		} else {
			SAXReader reader = new SAXReader();
			Document document;
			try {
				document = reader.read(this.feedFile);
				this.feed = this.feedReader.read(document);
			} catch (Exception e) {
				throw new MeshException(e);
			}			
		}
	}

	public File getFile(){
		return this.feedFile;
	}

	@Override
	public void beginSync() {
		// nothing to do
	}

	@Override
	public void endSync() {
		if(this.dirty){
			this.flush();
			this.dirty = false;
		}		
	}
	
	public void setDirty(){
		this.dirty = true;
	}
}
