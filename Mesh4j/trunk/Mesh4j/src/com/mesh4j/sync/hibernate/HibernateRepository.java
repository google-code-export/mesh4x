package com.mesh4j.sync.hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import com.mesh4j.sync.AbstractRepository;
import com.mesh4j.sync.Filter;
import com.mesh4j.sync.feed.FeedReader;
import com.mesh4j.sync.feed.FeedWriter;
import com.mesh4j.sync.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.Security;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class HibernateRepository extends AbstractRepository {

	// CONSTANTS
	private final static String SYNC_INFO = "SyncInfo";
	private final static String SYNC_INFO_ATTR_SYNC_ID = "sync_id";
	private final static String SYNC_INFO_ATTR_ENTITY_ID = "entity_id";
	private final static String SYNC_INFO_ATTR_ENTITY = "entity_name";
	private final static String SYNC_INFO_ATTR_LAST_UPDATE = "last_update";
	private final static String SYNC_INFO_ATTR_SYNC_DATA = "sync_data";
	
	private final static Log Logger = LogFactory.getLog(HibernateRepository.class);

	// MODEL VARIABLE
	private SessionFactory sessionFactory;
	private Session session;
	private String entityName;
	private String entityIDAttributeName;
	
	// BUSINESS METHODS
	public HibernateRepository(String fileMappingName){
		this(new File(fileMappingName));		
	}
	
	public HibernateRepository(File fileMapping){
		
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.addFile(fileMapping);	
		
		File syncMapping = new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile());   // TODO (JMT) inject sync info mapping name -> Spring?
		hibernateConfiguration.addFile(syncMapping);
		
		this.sessionFactory = hibernateConfiguration.buildSessionFactory();
		ClassMetadata classMetadata = this.getClassMetadata();
		this.entityName = classMetadata.getEntityName();						// TODO (JMT) set node attribute value
		this.entityIDAttributeName = classMetadata.getIdentifierPropertyName();
		
		this.newSession();
	}
	
	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(){
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (String entityName : map.keySet()) {
			if(!SYNC_INFO.equals(entityName)){
				ClassMetadata classMetadata = map.get(entityName);
				return classMetadata;
			}
		}
		return null;
	}
	
	public void newSession() {
		if(this.session != null){
			session.clear();
			this.session.close();
		}
		Session session = sessionFactory.openSession();
		this.session = session.getSession(EntityMode.DOM4J);		
	}

	@Override
	public void add(Item item) {
		Transaction trx = session.beginTransaction();
		try {
			Element entityElement = ItemHibernateContent.normalizeContent(entityName, item.getContent());
			String entityID = entityElement.element(this.entityIDAttributeName).getText();
			session.save(entityName, entityElement);
			Element syncElement = this.convertSync2Element(item.getSync(), entityID);
			session.save(SYNC_INFO, syncElement);
			session.flush();
			trx.commit();
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
			trx.rollback();
		}
	}

	@Override
	public void delete(String id) {

		Element syncElement = getSyncElement(id);
		Element entityElement = getEntityElement(syncElement);
		
		Transaction trx = session.beginTransaction();
		session.delete(this.entityName, entityElement);
		session.delete(SYNC_INFO, syncElement);
		session.flush();
		trx.commit();
	}
	

	@Override
	public void update(Item item) {
		Element entityElement = ItemHibernateContent.normalizeContent(entityName, item.getContent());
		String entityID = entityElement.element(this.entityIDAttributeName).getText();
		Element syncElement;
		try {
			syncElement = this.convertSync2Element(item.getSync(), entityID);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);
			return;   // TODO (JMT) throws an exception
		}
		
		session.clear();
		Transaction trx = session.beginTransaction();
		session.saveOrUpdate(entityName, entityElement);		
		session.update(SYNC_INFO, syncElement);
		session.flush();
		trx.commit();
	}

	@Override
	public Item get(String id) {
		Element syncElement = this.getSyncElement(id);
		if(syncElement == null){
			return null;
		}
		
		Element entityElement = this.getEntityElement(syncElement); 
		if(entityElement == null){
			return null;
		}
		Item item = null;
		try {
			item = this.makeItem(syncElement, entityElement);
		} catch (DocumentException e) {// TODO (JMT) throws an exception
			Logger.error(e.getMessage(), e);
		}
		
		return item;
	}

	private Item makeItem(Element syncElement, Element entityElement) throws DocumentException {		
		Sync sync = this.convertElement2Sync(syncElement);		
		return makeItem(sync, entityElement);
	}

	private Item makeItem(Sync sync, Element entityElement) throws DocumentException {
		Content content = new ItemHibernateContent(entityElement);
		Item item = new Item(content, sync);
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<Item> getAll(Date since, Filter<Item> filter) {
		ArrayList<Item> result = new ArrayList<Item>();
		
		// TODO (JMT) improve query
		String hqlQuery ="FROM " + this.entityName;
		List<Element> entities = session.createQuery(hqlQuery).list();
 
		for (Element entityElement : entities) {
			
			String entityID = entityElement.element(this.entityIDAttributeName).getText();

			String syncQuery ="FROM " + SYNC_INFO + 
				" WHERE " + SYNC_INFO_ATTR_ENTITY + " = '" + this.entityName + "' and " + SYNC_INFO_ATTR_ENTITY_ID + " = '"+ entityID +"' ";
			Element syncElement = (Element) session.createQuery(syncQuery).uniqueResult();

			try {
				Item item;
				if(syncElement == null){
					Sync sync = saveNewSync(entityID);
					item = makeItem(sync, entityElement);
				} else {
					item = makeItem(syncElement, entityElement);
				}
				boolean dateOk = since == null || (item.getSync().getLastUpdate() == null || since.compareTo(item.getSync().getLastUpdate().getWhen()) <= 0);  // TODO (JMT) create db filter
				if(filter.applies(item) && dateOk){
					result.add(item);
				}
			} catch (DocumentException e) {
				Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
			}
		}
		return result;
	}

	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(HibernateRepository.class.getName());
	}

	@Override
	public boolean supportsMerge() {
		return false;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		return items; // Nothing to do, see HibernateRepository>>supportsMerge()
	}
	
	// TODO (JMT) verify close connection

	protected Element getSyncElement(String id) {
		Element syncInfo = (Element) session.get(SYNC_INFO, id);
		return syncInfo;
	}

	protected Element getEntityElement(Element syncElement) {
		String entityID = syncElement.element(SYNC_INFO_ATTR_ENTITY_ID).getText();
		Element entityElement = (Element) session.get(this.entityName, entityID);
		return entityElement;
	}
	
	protected Element convertSync2Element(Sync sync, String entityID) throws DocumentException {
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE);
		Element syncElementRoot = DocumentHelper.createElement(SYNC_INFO);
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_ID).addText(sync.getId());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY).addText(this.entityName);
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_ID).addText(entityID);
		syncElementRoot.addElement(SYNC_INFO_ATTR_LAST_UPDATE).addText(DateHelper.formatRFC822(new Date()));
		
		Element syncData = DocumentHelper.createElement(RssSyndicationFormat.ATTRIBUTE_PAYLOAD);
		syncData.addNamespace(RssSyndicationFormat.SX_PREFIX, RssSyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		String syncAsXML = syncData.element(RssSyndicationFormat.SX_ELEMENT_SYNC).asXML();
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_DATA).addText(syncAsXML);
		
		return syncElementRoot;
	}
	
	protected Sync convertElement2Sync(Element syncInfoElement) throws DocumentException {
		Element syncData = syncInfoElement.element(SYNC_INFO_ATTR_SYNC_DATA);
		Element syncElement = DocumentHelper.parseText(syncData.getText()).getRootElement();
		//Element syncElement = DocumentHelper.parseText(syncData.getText()).getRootElement().element(RssSyndicationFormat.SX_ELEMENT_SYNC);
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE);
		Sync sync = reader.readSync(syncElement);
		if(sync == null){
			String entityID = syncElement.element(SYNC_INFO_ATTR_ENTITY_ID).getText();
			sync = saveNewSync(entityID);
		}
		return sync;
	}

	private Sync saveNewSync(String entityID) throws DocumentException {
		Sync sync = this.makeNewSync();
		Element syncElement = this.convertSync2Element(sync, entityID);

		Transaction trx = session.beginTransaction();
		session.save(SYNC_INFO, syncElement);
		session.flush();
		trx.commit();
		return sync;
	}

	protected Sync makeNewSync() {
		return new Sync(IdGenerator.newID(), Security.getAuthenticatedUser(), new Date(), false);
	}
}
