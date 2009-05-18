package org.mesh4j.sync.message.channel.sms.core.repository.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.ISmsReceiverRepository;
import org.mesh4j.sync.message.channel.sms.core.ISmsSenderRepository;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class FileSmsChannelRepository implements ISmsSenderRepository, ISmsReceiverRepository{

	private static final String INCOMMING_FILE_NAME = "smsChannel_incomming.xml";
	private static final String OUTCOMMING_FILE_NAME = "smsChannel_outcomming.xml";
	
	// MODEL VARIANBLES
	private String rootDirectory;
	
	// BUSINESS METHODS
	public FileSmsChannelRepository(String rootDirectory) {
		Guard.argumentNotNullOrEmptyString(rootDirectory, "rootDirectory");

		this.rootDirectory = rootDirectory;
		
		File fileDir = new File(rootDirectory);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
	}
	
	public File getIncommingFile() {
		File file = new File(this.rootDirectory + INCOMMING_FILE_NAME);
		return file;
	}
	
	public File getOutcommingFile() {
		File file = new File(this.rootDirectory + OUTCOMMING_FILE_NAME);
		return file;
	}
	
	public void writeOutcomming(List<SmsMessageBatch> outcomming){
		File file = this.getOutcommingFile();
		this.writeOutcomming(outcomming, file, SmsMessageBatchFormatter.ELEMENT_OUTCOMMING);
	}
	
	public void writeIncomming(List<SmsMessageBatch> incomming){
		File file = this.getIncommingFile();
		this.writeIncomming(incomming, file);
	}
	
	public void writeIncomming(List<SmsMessageBatch> incomming, File file){
		Document document = getDocument(file);
		this.writeIncomming(incomming, document);
		XMLHelper.write(document, file);
	}

	public void writeIncomming(List<SmsMessageBatch> incomming, Document document){
		Element root = this.getRootElement(document);
		this.write(incomming, root, SmsMessageBatchFormatter.ELEMENT_INCOMMING_ONGOING);
	}
	
	public void writeIncommingCompleted(List<SmsMessageBatch> incomming){
		File file = this.getIncommingFile();
		this.writeIncommingCompleted(incomming, file);
	}
	
	public void writeIncommingCompleted(List<SmsMessageBatch> incomming, File file){
		Document document = getDocument(file);
		this.writeIncommingCompleted(incomming, document);
		XMLHelper.write(document, file);
	}

	public void writeIncommingCompleted(List<SmsMessageBatch> incomming, Document document){
		Element root = this.getRootElement(document);
		this.write(incomming, root, SmsMessageBatchFormatter.ELEMENT_INCOMMING_COMPLETED);
	}

	public void writeIncommingDiscarded(List<DiscardedBatchRecord> incomming){
		File file = this.getIncommingFile();
		this.writeIncommingDiscarded(incomming, file);
	}
	
	public void writeIncommingDiscarded(List<DiscardedBatchRecord> incomming, File file){
		Document document = getDocument(file);
		this.writeIncommingDiscarded(incomming, document);
		XMLHelper.write(document, file);
	}

	public void writeIncommingDiscarded(List<DiscardedBatchRecord> incomming, Document document){
		Element root = this.getRootElement(document);
		Element element = root.element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_DISCARDED);
		if(element != null){
			root.remove(element);
		}
		
		element = DocumentHelper.createElement(SmsMessageBatchFormatter.ELEMENT_INCOMMING_DISCARDED);
		this.writeDiscarded(element, incomming);
		root.add(element);
	}
	
	private void write(List<SmsMessageBatch> outcomming, Element root, String elementName){
		Element element = root.element(elementName);
		if(element != null){
			root.remove(element);
		}
		
		element = DocumentHelper.createElement(elementName);
		this.write(element, outcomming);
		root.add(element);
	}

	private Document getDocument(File file) {
		Document document;
		if(file.exists()){
			try {
				document = XMLHelper.readDocument(file);
			} catch (DocumentException e) {
				throw new MeshException(e);
			}	
		} else {
			document = DocumentHelper.createDocument();
		}
		return document;
	}
	
	private Element getRootElement(Document document){
		Element root = document.getRootElement();
		if(root == null){
			root = DocumentHelper.createElement(SmsMessageBatchFormatter.ELEMENT_ROOT);
			document.add(root);
		}
		return root;
	}
	
	private void write(Element root, List<SmsMessageBatch> batches){
		for (SmsMessageBatch batch : batches) {
			Element batchElement = SmsMessageBatchFormatter.createBatchElement(batch);
			root.add(batchElement);
		}
	}
	
	private void writeDiscarded(Element root, List<DiscardedBatchRecord> discardedBatchRecors){
		for (DiscardedBatchRecord discardedBatchRecord : discardedBatchRecors) {
			Element batchElement = SmsMessageBatchFormatter.createBatchElement(discardedBatchRecord.getMessageBatch());
			root.add(batchElement);
		}
	}

	public List<SmsMessageBatch> readIncomming(){
		File file = getIncommingFile();
		return readIncomming(file);
	}
	
	public List<SmsMessageBatch> readIncomming(File file){
		try {
			if(!file.exists()){
				return new ArrayList<SmsMessageBatch>();
			}else {
				Document document= XMLHelper.readDocument(file);
				return readIncomming(document);
			}
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readIncomming(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_ONGOING);
		return this.read(incommingElement);
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(){
		File file = getIncommingFile();
		return readIncommingCompleted(file);
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(File file){
		try {
			if(!file.exists()){
				return new ArrayList<SmsMessageBatch>();
			}else {
				Document document= XMLHelper.readDocument(file);
				return readIncommingCompleted(document);
			}
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_COMPLETED);
		return this.read(incommingElement);
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(){
		File file = getIncommingFile();
		return readIncommingDicarded(file);
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(File file){
		try {
			if(!file.exists()){
				return new ArrayList<DiscardedBatchRecord>();
			}else {
				Document document= XMLHelper.readDocument(file);
				return readIncommingDicarded(document);
			}
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_DISCARDED);
		return this.readDiscarded(incommingElement);
	}
	
	public List<SmsMessageBatch> readOutcomming(){
		File file = getOutcommingFile();
		return readOutcomming(file, SmsMessageBatchFormatter.ELEMENT_OUTCOMMING);
	}
	
	@SuppressWarnings("unchecked")
	private List<SmsMessageBatch> read(Element root){
		List<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
		
		List<Element> batchElements = root.elements();
		for (Element batchElement : batchElements) {
			SmsMessageBatch batch = SmsMessageBatchFormatter.createBatch(batchElement);
			result.add(batch);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<DiscardedBatchRecord> readDiscarded(Element root){
		List<DiscardedBatchRecord> result = new ArrayList<DiscardedBatchRecord>();
		
		List<Element> batchElements = root.elements();
		for (Element batchElement : batchElements) {
			SmsMessageBatch batch = SmsMessageBatchFormatter.createBatch(batchElement);
			result.add(new DiscardedBatchRecord(batch, null));
		}
		return result;
	}

	@Override
	public void write(List<SmsMessageBatch> incomming,
			List<SmsMessageBatch> completed,
			List<DiscardedBatchRecord> discarded) {
		
		File file = this.getIncommingFile();
		Document document = getDocument(file);
		
		this.writeIncomming(incomming, document);
		this.writeIncommingCompleted(completed, document);
		this.writeIncommingDiscarded(discarded, document);
		
		XMLHelper.write(document, file);
	}

	@Override
	public List<SmsMessageBatch> readOutcommingCompleted(){
		File file = getOutcommingFile();
		return readOutcomming(file, SmsMessageBatchFormatter.ELEMENT_OUTCOMMING_COMPLETED);
	}
	
	public List<SmsMessageBatch> readOutcomming(File file, String elementName){
		try {
			if(!file.exists()){
				return new ArrayList<SmsMessageBatch>();
			}else {
				Document document= XMLHelper.readDocument(file);
				return readOutcomming(document, elementName);
			}
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readOutcomming(Document document, String elementName){		
		Element outcommingElement = document.getRootElement().element(elementName);
		return this.read(outcommingElement);
	}
	
	@Override
	public void writeOutcommingCompleted(List<SmsMessageBatch> outcomming) {
		File file = this.getOutcommingFile();
		this.writeOutcomming(outcomming, file, SmsMessageBatchFormatter.ELEMENT_OUTCOMMING_COMPLETED);		
	}
	
	public void writeOutcomming(List<SmsMessageBatch> outcomming, File file, String elementName){
		Document document = getDocument(file);
		this.writeOutcomming(outcomming, document, elementName);
		XMLHelper.write(document, file);
	}

	public void writeOutcomming(List<SmsMessageBatch> outcomming, Document document, String elementName){
		Element root = this.getRootElement(document);
		this.write(outcomming, root, elementName);
	}
}
