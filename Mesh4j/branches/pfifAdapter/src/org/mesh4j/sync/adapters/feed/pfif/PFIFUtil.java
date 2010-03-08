package org.mesh4j.sync.adapters.feed.pfif;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.pfif.mapping.PFIFModel;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class PFIFUtil {

	private PFIFUtil(){}
	
	public static String getFeedName(File feedFile,ISyndicationFormat syndicationFormat){

		FeedReader reader = new FeedReader(syndicationFormat, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentReader.INSTANCE);

		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(new FileInputStream(feedFile));
		} catch (FileNotFoundException e) {
			throw new MeshException(e);
		}
		Feed feed = null;
		try {
			feed = reader.read(inputStreamReader);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		List<Item> noteList = new LinkedList<Item>();
		List<Item> personList = new LinkedList<Item>();
		
		for(Item item :feed.getItems()){
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
			if(element.getName().equals(PFIFSchema.QNAME_PERSON.getName())){
				if(isNestedNoteExist(element)){
					noteList.addAll( getNoteItems(item,element));
				}
				personList.add(readItem(item,element));
			} else if(element.getName().equals(PFIFSchema.QNAME_NOTE.getName())){
				noteList.add(readItem(item,element));
			} else {
				//dont know
			}
			}
		}
		if((noteList != null && !noteList.isEmpty()) && 
				(personList != null && !personList.isEmpty())){
			noteList = null;
			personList = null;
			return "compound";
		} else if((noteList != null && !noteList.isEmpty()) &&
				(personList == null || personList.isEmpty())){
			noteList = null;
			personList = null;
			return PFIFSchema.QNAME_NOTE.getName();
		} else if((noteList == null || noteList.isEmpty()) &&
				(personList != null && !personList.isEmpty())){
			noteList = null;
			personList = null;
			return PFIFSchema.QNAME_PERSON.getName();
		}
		return "";
	}
	public static boolean isNoteFeed(File feedFile,ISyndicationFormat syndicationFormat){
		return getFeedName(feedFile,syndicationFormat).equals(PFIFSchema.QNAME_NOTE.getName());
	}
	public static boolean isCompoundFeed(File feedFile,ISyndicationFormat syndicationFormat){
		return getFeedName(feedFile,syndicationFormat).equals("compound");
	}
	public static boolean isPersonFeed(File feedFile,ISyndicationFormat syndicationFormat){
		return getFeedName(feedFile,syndicationFormat).equals(PFIFSchema.QNAME_PERSON.getName());
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
	
	public static  List<PFIFModel> getOrCreatePersonAndNoteFileIfNecessary(String fileName,
			ISyndicationFormat syndicationFormat) throws IOException{
		
		File file = new File(fileName);
		List<PFIFModel> feeFileNames = new LinkedList<PFIFModel>();
		if(!isCompoundFeed(file, syndicationFormat)){
			PFIFModel model = new PFIFModel(getFeedName(file, syndicationFormat),
					getFeedFromFile(file,syndicationFormat),file);
			feeFileNames.add(model);
			return feeFileNames;
		}
		
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
		
		Feed personFeed = new Feed(feed.getTitle(),feed.getDescription(),feed.getLink());
		Feed noteFeed = new Feed(feed.getTitle(),feed.getDescription(),feed.getLink());
		
		for(Item item :feed.getItems()){
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
				String title = "";
				String description = "";
				String link = "";
				if(syndicationFormat.isFeedItemTitle(element)){
					title = syndicationFormat.getFeedItemTitle(element);
				} else if(syndicationFormat.isFeedItemDescription(element)){
					description = syndicationFormat.getFeedItemDescription(element);
				}else if(syndicationFormat.isFeedItemLink(element)){
					link = syndicationFormat.getFeedItemLink(element);
				}			
				
				if(element.getName().equals(PFIFSchema.QNAME_PERSON.getName())){
					if(isNestedNoteExist(element)){
						noteFeed.addItems(getNoteItems(item,element));
					}
					personFeed.addItem(readItem(item,element));
					
				} else if(element.getName().equals(PFIFSchema.QNAME_NOTE.getName())){
					noteFeed.addItem(readItem(item,element));
				} else {
					//dont know
				}	
			}
		}
		
		File personFeedFile = null;
		
		if(!personFeed.getItems().isEmpty()){
			personFeedFile = new File(file.getParent() + getFileName(file) + "_person-pfif.xml");
			writeFeedInFile(personFeed,personFeedFile,syndicationFormat);
		}
		
		File noteFeedFile = null;
		if(!noteFeed.getItems().isEmpty()){
			noteFeedFile = new File(file.getParent() +getFileName(file)  + "_note-pfif.xml");
			writeFeedInFile(noteFeed,noteFeedFile,syndicationFormat);
		}
		
		feeFileNames.add(new PFIFModel(PFIFSchema.QNAME_PERSON.getName(),personFeed,personFeedFile));
		feeFileNames.add(new PFIFModel(PFIFSchema.QNAME_NOTE.getName(),noteFeed,noteFeedFile));
		
		return feeFileNames;
	}
	
	   
      public static String getFileName(File file) {
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
		PFIFContentWriter contentWriter = new PFIFContentWriter(true,false);
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
		 
		 XMLContent modelItem = new XMLContent(sync.getId(),title , description, link, nPayload);
		 Item newItem = new Item(modelItem, sync);
		
		return newItem;
	}
	
	
	
	private static List<Item> getNoteItems(Item item ,Element personPayload){
	
		List<Item> list = new LinkedList<Item>();
		String title = ((XMLContent)item.getContent()).getTitle();
		String description = ((XMLContent)item.getContent()).getDescription();
		String link = ((XMLContent)item.getContent()).getLink();
		
		List<Element> noteList = personPayload.elements(PFIFSchema.QNAME_NOTE.getName());
		
		if(noteList != null && noteList.size() >0){//if any note occur
			for(Element noteElement :noteList){
				Element nPayload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
				 nPayload.add(noteElement.detach());
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
		List<Element> noteList = personPayload.elements(PFIFSchema.QNAME_NOTE.getName());
		if(noteList != null && noteList.size() >0){
			return true;
		}
		return false;
	}
}
