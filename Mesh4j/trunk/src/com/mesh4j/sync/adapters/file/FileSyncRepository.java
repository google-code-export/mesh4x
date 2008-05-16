package com.mesh4j.sync.adapters.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.compound.ISyncRepository;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class FileSyncRepository implements ISyncRepository{

	private static final String ATTRIBUTE_ID = "ID";
	private final static Log Logger = LogFactory.getLog(FileSyncRepository.class);
	
	// MODEL VARIABLES
	private File syncInfoFile;
	private Document syncDocument;
	private SyncInfoParser syncInfoParser;

	// BUSINESS METHODS
	public FileSyncRepository(String syncInfoFileName, ISecurity security){
		this(new File(syncInfoFileName), security);
	}
	
	public FileSyncRepository(File syncInfoFile, ISecurity security){

		Guard.argumentNotNull(syncInfoFile, "syncInfoFile");
		Guard.argumentNotNull(security, "security");

		this.syncInfoFile = syncInfoFile;
		
		this.initializeFile(syncInfoFile);
		
		SAXReader saxReader = new SAXReader();
		try {
			this.syncDocument = saxReader.read(this.syncInfoFile);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
		
		this.syncInfoParser = new SyncInfoParser(RssSyndicationFormat.INSTANCE, security);
	}

	private void initializeFile(File file) {
		if(!file.exists()){
			Element rootElement = DocumentHelper.createElement("Syncs");
			Document document = DocumentHelper.createDocument(rootElement);
			XMLHelper.write(document, file);
		}		
	}
	
	@Override
	public SyncInfo get(String syncId) {
		Element syncInfoElement = syncDocument.elementByID(syncId);
		if(syncInfoElement == null){
			return null;
		} else {
			try {
				return syncInfoParser.convertElement2SyncInfo(syncInfoElement);
			} catch (DocumentException e) {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SyncInfo> getAll(Date since, String entityName) {
		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>(); 
		
		List<Element> syncInfoElements = syncDocument.getRootElement().elements();
		for (Element syncInfoElement : syncInfoElements) {
			try {
				SyncInfo syncInfo = syncInfoParser.convertElement2SyncInfo(syncInfoElement);
				result.add(syncInfo);
			} catch (DocumentException e) {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
		}
		return result;
	}

	@Override
	public String newSyncID(IContent content) {
		return content.getId();
	}

	@Override
	public void save(SyncInfo syncInfo) {
		Element syncInfoElement;
		try {
			syncInfoElement = syncInfoParser.convertSyncInfo2Element(syncInfo);
			String id = syncInfoElement.attributeValue(ATTRIBUTE_ID);
			if(id == null){
				syncInfoElement.addAttribute(ATTRIBUTE_ID, syncInfo.getSyncId());
			}
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
		
		Element original = this.syncDocument.elementByID(syncInfo.getSyncId());
		if(original != null){
			this.syncDocument.getRootElement().remove(original);
		}
		this.syncDocument.getRootElement().add(syncInfoElement);
		flush();
	}

	private void flush() {
		XMLHelper.write(this.syncDocument, this.syncInfoFile);
	}
}