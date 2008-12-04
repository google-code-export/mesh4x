package org.mesh4j.sync.adapters.kml.timespan.decorator;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.kml.KmlNames;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

public class KMLTimeSpanDecoratorSyncAdapter implements ISyncAdapter, ISyncAware {
	
	//private final static Log LOGGER = LogFactory.getLog(KmlTimeSpanDecoratorSyncAdapter.class);

	// MODEL VARIABLES
	private File kmlFile;
	private Document document;
	private String documentName;
	private ISyncAdapter syncAdapter;
	private IKMLGenerator kmlGenerator;

	// BUSINESS METHODS
	
	public KMLTimeSpanDecoratorSyncAdapter(IKMLGenerator kmlGenerator, String documentName, String kmlFileName, ISyncAdapter syncAdapter) {
		super();
		this.kmlGenerator = kmlGenerator;
		this.kmlFile = new File(kmlFileName);
		this.documentName = documentName;
		this.syncAdapter = syncAdapter;
	}

	@Override
	public Item get(String id) {
		return this.syncAdapter.get(id);
	}
	
	@Override
	public List<Item> getAll() {
		List<Item> items = this.syncAdapter.getAll();
		refreshAll(items);
		return items;
		
	}

	@Override
	public List<Item> getAll(IFilter<Item> filter) {
		List<Item> items = this.syncAdapter.getAll(filter);
		refreshAll(items);
		return items;
	}
	
	@Override
	public List<Item> getAllSince(Date since) {
		List<Item> items = this.syncAdapter.getAllSince(since);
		refreshAll(items);
		return items;
	}
	
	@Override
	public List<Item> getAllSince(Date since, IFilter<Item> filter) {
		List<Item> items = this.syncAdapter.getAllSince(since, filter);
		refreshAll(items);
		return items;
	}
	
	@Override
	public List<Item> getConflicts() {
		return this.syncAdapter.getConflicts();
	}
	
	@Override
	public String getFriendlyName() {
		return this.syncAdapter.getFriendlyName();
	}
	
	@Override
	public void delete(String id) {
		this.syncAdapter.delete(id);		
	}
	
	@Override
	public void add(Item item) {
		this.syncAdapter.add(item);		
		this.addItemToKML(item);
	}
	
	@Override
	public void update(Item item) {
		this.syncAdapter.update(item);
		refreshKML(item);
	}
	
	@Override
	public void update(Item item, boolean resolveConflicts) {
		this.syncAdapter.update(item, resolveConflicts);
		refreshKML(item);
	}
	
	@Override
	public void beginSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).beginSync();
		}
		
		this.initializeDOM();
	}

	@Override
	public void endSync() {
		if(this.syncAdapter instanceof ISyncAware){
			((ISyncAware)this.syncAdapter).endSync();
		}
		
		this.flushKml();
	}

	private void addItemToKML(Item item) {
		this.kmlGenerator.addElement(this.document, item); 
	}
	
	private void refreshAll(List<Item> items) {
		for (Item item : items) {
			refreshKML(item);
		}		
	}
	
	private void refreshKML(Item item) {
		try{
			if(!item.isDeleted()){
				
				Element itemElement = this.kmlGenerator.getElement(this.document, item);
				if(itemElement == null){
					addItemToKML(item);
				} else {
					Element timeSpan = itemElement.element(KmlNames.KML_ELEMENT_TIME_SPAN);
					Element elementEnd = timeSpan.element(KmlNames.KML_ELEMENT_TIME_SPAN_END);
					if(this.kmlGenerator.hasItemChanged(this.document, itemElement, item)){
						elementEnd.setText(DateHelper.formatW3CDateTime(item.getLastUpdate().getWhen()));
						addItemToKML(item);
					} else { 
						elementEnd.setText(DateHelper.formatW3CDateTime(new Date()));	
					}				
				}
			}
		} catch(Exception e){
			throw new MeshException(e);
			//LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void initializeDOM() {
		try{
			SAXReader saxReader = new SAXReader();
			if(!this.kmlFile.exists()){
				this.document = this.kmlGenerator.makeDocument(this.documentName);
			} else {	
				this.document = saxReader.read(kmlFile);
			}
		} catch(Exception e){
			throw new MeshException(e);
			//LOGGER.error(e.getMessage(), e);
		}

	}

	private void flushKml() {
		try{
			File kmlFile = this.kmlFile;
			XMLHelper.write(this.document, kmlFile);
		} catch (Exception e) {
			throw new MeshException(e);
			//LOGGER.error(e.getMessage(), e);
		}
	}
}
