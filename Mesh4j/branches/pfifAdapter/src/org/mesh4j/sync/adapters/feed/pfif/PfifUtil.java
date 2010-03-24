package org.mesh4j.sync.adapters.feed.pfif;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.model.PfifModel;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIF_ENTITY;
import org.mesh4j.sync.adapters.feed.pfif.schema.PfifSchema;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class PfifUtil {

	private PfifUtil(){}
	
	
	public static boolean isPfifAtomFeed(File feedFile){
		
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(feedFile));
		} catch (FileNotFoundException e) {
			throw new MeshException(e);
		}
		Document document = null;
		SAXReader saxReader = new SAXReader();
		
		try {
			document = saxReader.read(inputStreamReader);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
		return AtomSyndicationFormat.isAtom(document);
	}
		
	public static ISyndicationFormat getPfifSyndicationFormat(File feedFile){

		if(isPfifAtomFeed(feedFile)){
			return AtomSyndicationFormat.INSTANCE;
		} else {
			return RssSyndicationFormat.INSTANCE;
		}	
	}
	
	public static Map<PFIF_ENTITY,Feed> getPfifFeed(File feedFile,ISyndicationFormat syndicationFormat){

		Feed feed = getFeedFromFile(feedFile,syndicationFormat);
		if(feed == null || 
				feed.getItems() == null){
			throw new MeshException("Entity not found,Invalid feed");
		}
		return getPfifFeed(feed);
	}
	
	public static Set<String> getFeedNames(File feedFile,ISyndicationFormat syndicationFormat){
		Map<PFIF_ENTITY,Feed> entityMap = getPfifFeed(feedFile,syndicationFormat);
		
		Set<String> entitySet = new LinkedHashSet<String>();
		for(PFIF_ENTITY pfif_entity : entityMap.keySet()){
			entitySet.add(pfif_entity.toString());
		}
		return entitySet;
	}
	
	public static Feed getFeedFromFile(File file,ISyndicationFormat syndicationFormat){
		FeedReader reader = new FeedReader(syndicationFormat, NullIdentityProvider.INSTANCE, 
				IdGenerator.INSTANCE, ContentReader.INSTANCE);
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new MeshException(e);
		}
		Feed feed = null;
		try {
			feed = reader.read(inputStreamReader);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		return feed;
	}
	
	
	
	public static  List<PfifModel> getOrCreatePersonAndNoteFileIfNecessary(String fileName,
			ISyndicationFormat syndicationFormat) throws IOException{
		
		File file = new File(fileName);
		List<PfifModel> feeFileNames = new LinkedList<PfifModel>();
		Feed feed = getFeedFromFile(file, syndicationFormat);
		Map<PFIF_ENTITY,Feed> listOfPfifFeed = getPfifFeed(feed);
		
		if( listOfPfifFeed.size() == 1){
			PFIF_ENTITY key;
			if(listOfPfifFeed.containsKey(PFIF_ENTITY.PERSON)){
				key = PFIF_ENTITY.PERSON;
			} else {
				key = PFIF_ENTITY.NOTE;
			}
			
			PfifModel model = new PfifModel(key,feed,file);
			feeFileNames.add(model);
			return feeFileNames;
		}
		
		Feed personFeed = listOfPfifFeed.get(PFIF_ENTITY.PERSON);
		Feed noteFeed = listOfPfifFeed.get(PFIF_ENTITY.NOTE);
		
		File personFeedFile = null;
		if(!personFeed.getItems().isEmpty()){
			personFeedFile = new File(getPathToWrite(file) + getFileName(file) + "_person-pfif.xml");
			writeFeedInFile(personFeed,personFeedFile,syndicationFormat);
		}
		File noteFeedFile = null;
		if(!noteFeed.getItems().isEmpty()){
			noteFeedFile = new File(getPathToWrite(file) +getFileName(file)  + "_note-pfif.xml");
			writeFeedInFile(noteFeed,noteFeedFile,syndicationFormat);
		}
		
		feeFileNames.add(new PfifModel(PFIF_ENTITY.PERSON,personFeed,personFeedFile));
		feeFileNames.add(new PfifModel(PFIF_ENTITY.NOTE,noteFeed,noteFeedFile));
		
		return feeFileNames;
	}
	
	private static String getPathToWrite(File feedFile){
		String path = "";
		String parent = feedFile.getParent();
		
		if(!parent.endsWith(File.separator)){
			path = parent + File.separator;
		} else {
			path = parent;
		}
		return path;
	}
	
	   
     private static String getFileName(File file) {
	      String fileName = file.getName();
	      int whereDot = fileName.lastIndexOf('.');
	      if ( whereDot > 0 && whereDot <= fileName.length() - 2 ) {
	    	  return fileName.substring(0, whereDot);
	      }
	      return "";
      }
	
	private static void writeFeedInFile(Feed feed,File feedFile,ISyndicationFormat syndicationFormat){
		
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(feedFile), OutputFormat.createPrettyPrint());
		} catch (IOException e) {
			throw new MeshException(e);
		}
		PfifContentWriter contentWriter = new PfifContentWriter(true,false);
		FeedWriter feedWriter = new FeedWriter(syndicationFormat, NullIdentityProvider.INSTANCE, contentWriter);
		try {
			feedWriter.write(writer, feed);
		} catch (Exception e) {
			throw new MeshException(e);
		}	
	}
	
	
	private static Item readItem(Item item ,Element entityPayload){

		String title = ((XMLContent)item.getContent()).getTitle();
		String description = ((XMLContent)item.getContent()).getDescription();
		String link = ((XMLContent)item.getContent()).getLink();
		
		Sync sync = item.getSync().clone();
		if(item.getSync() == null){
			 sync = new Sync(IdGenerator.INSTANCE.newID(),  NullIdentityProvider.INSTANCE.getAuthenticatedUser(),
					 new Date(), false);
		} 
		 Element nPayload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
		 nPayload.add(entityPayload.detach());
//		 nPayload.add(entityPayload.createCopy());
		 XMLContent modelItem = new XMLContent(sync.getId(),title , description, link, nPayload);
		 Item newItem = new Item(modelItem, sync);
		
		return newItem;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static List<Item> getNoteItems(Item item ,Element personPayload){
		
		List<Item> list = new LinkedList<Item>();
		String title = ((XMLContent)item.getContent()).getTitle();
		String description = ((XMLContent)item.getContent()).getDescription();
		String link = ((XMLContent)item.getContent()).getLink();
		
		List<Element> noteList = personPayload.elements(PfifSchema.QNAME_NOTE.getName());
		
		if(noteList != null && noteList.size() >0){//if any note occur
			for(Element noteElement :noteList){
				Element nPayload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
				 nPayload.add(noteElement.detach());
//				 nPayload.add(noteElement.createCopy());
				 Sync sync = new Sync(IdGenerator.INSTANCE.newID(),  NullIdentityProvider.INSTANCE.getAuthenticatedUser(),
						 new Date(), false);
				 XMLContent modelItem = new XMLContent(sync.getId(),title,description, link, nPayload);
				 Item newItem = new Item(modelItem, sync);
				 list.add(newItem);
			}
		}
		return list;
	}
	
	@SuppressWarnings({ "unchecked" })
	private static boolean isNestedNoteExist(Element personPayload){
		List<Element> noteList = personPayload.elements(PfifSchema.QNAME_NOTE.getName());
		if(noteList != null && noteList.size() >0){
			return true;
		}
		return false;
	}
	
	public static Map<PFIF_ENTITY,Feed> getPfifFeed(Feed rawfeed){
		
		List<Item> noteItems = new LinkedList<Item>();
		List<Item> personItems = new LinkedList<Item>();
		
		//creating different reference
		Feed aliasFeed = new Feed(rawfeed.getTitle(),rawfeed.getDescription(),rawfeed.getLink());
		for(Item item : rawfeed.getItems()){
			aliasFeed.addItem(item.clone());	
		}
	
		for(Item item :aliasFeed.getItems()){
			Element contentElement = item.getContent().getPayload();
			Element payload = null;
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(contentElement.getName())){
				payload = contentElement;
			} else {
				payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
				payload.add(contentElement.detach());
			}
			List<Element> elementList = payload.elements();
			for(Element element : elementList){
			if(element.getName().equals(PfifSchema.QNAME_PERSON.getName())){
				if(isNestedNoteExist(element)){
					noteItems.addAll( getNoteItems(item,element));
				}
				personItems.add(readItem(item,element));
			} else if(element.getName().equals(PfifSchema.QNAME_NOTE.getName())){
				noteItems.add(readItem(item,element));
			} 
		}
	 }
	return getPfifFeedMap(rawfeed,noteItems,personItems);
   }
	
	private static Map<PFIF_ENTITY,Feed> getPfifFeedMap(Feed rawfeed,List<Item> noteItems,List<Item> personItems){
		
		Map<PFIF_ENTITY,Feed> feedMap = new LinkedHashMap<PFIF_ENTITY, Feed>();
		
		Feed personFeed = new Feed(rawfeed.getTitle(),rawfeed.getDescription(),rawfeed.getLink());
		Feed noteFeed = new Feed(rawfeed.getTitle(),rawfeed.getDescription(),rawfeed.getLink());
		
		if((noteItems != null && !noteItems.isEmpty()) && 
				(personItems != null && !personItems.isEmpty())){
			personFeed.addItems(personItems);
			noteFeed.addItems(noteItems);
			feedMap.put(PFIF_ENTITY.PERSON, personFeed);
			feedMap.put(PFIF_ENTITY.NOTE, noteFeed);
		} else if((noteItems != null && !noteItems.isEmpty()) &&
				(personItems == null || personItems.isEmpty())){
			noteFeed.addItems(noteItems);
			feedMap.put(PFIF_ENTITY.NOTE, noteFeed);
		} else if((noteItems == null || noteItems.isEmpty()) &&
				(personItems != null && !personItems.isEmpty())){
			personFeed.addItems(personItems);
			feedMap.put(PFIF_ENTITY.PERSON, personFeed);
		}
		return feedMap;
	}
}
