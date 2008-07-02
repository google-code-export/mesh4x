package com.mesh4j.sync.message.channel.sms.core.repository.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.message.channel.sms.ISmsChannelRepository;
import com.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class FileSmsChannelRepository implements ISmsChannelRepository{

	private static final String SMS_CHANNEL_FILE_NAME = "smsChannel.xml";
	
	// MODEL VARIANBLES
	private String rootDirectory;
	
	// BUSINESS METHODS
	public FileSmsChannelRepository(String rootDirectory) {
		Guard.argumentNotNullOrEmptyString(rootDirectory, "rootDirectory");

		this.rootDirectory = rootDirectory;
	}
	
	public File getFile() {
		File file = new File(this.rootDirectory + SMS_CHANNEL_FILE_NAME);
		return file;
	}
	
	public void writeOutcomming(List<SmsMessageBatch> outcomming){
		File file = this.getFile();
		this.writeOutcomming(outcomming, file);
	}
	
	public void writeOutcomming(List<SmsMessageBatch> outcomming, File file){
		Document document = getDocument(file);
		this.writeOutcomming(outcomming, document);
		XMLHelper.write(document, file);
	}

	public void writeOutcomming(List<SmsMessageBatch> outcomming, Document document){
		Element root = this.getRootElement(document);
		this.write(outcomming, root, SmsMessageBatchFormatter.ELEMENT_OUTCOMMING);
	}
	
	public void writeIncomming(List<SmsMessageBatch> incomming){
		File file = this.getFile();
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
		File file = this.getFile();
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
		File file = this.getFile();
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
		File file = getFile();
		return readIncomming(file);
	}
	
	public List<SmsMessageBatch> readIncomming(File file){
		try {
			Document document= XMLHelper.readDocument(file);
			return readIncomming(document);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readIncomming(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_ONGOING);
		return this.read(incommingElement);
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(){
		File file = getFile();
		return readIncommingCompleted(file);
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(File file){
		try {
			Document document= XMLHelper.readDocument(file);
			return readIncommingCompleted(document);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readIncommingCompleted(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_COMPLETED);
		return this.read(incommingElement);
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(){
		File file = getFile();
		return readIncommingDicarded(file);
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(File file){
		try {
			Document document= XMLHelper.readDocument(file);
			return readIncommingDicarded(document);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<DiscardedBatchRecord> readIncommingDicarded(Document document){		
		Element incommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_INCOMMING_DISCARDED);
		return this.readDiscarded(incommingElement);
	}
	
	public List<SmsMessageBatch> readOutcomming(){
		File file = getFile();
		return readOutcomming(file);
	}
	
	public List<SmsMessageBatch> readOutcomming(File file){
		try {
			Document document= XMLHelper.readDocument(file);
			return readOutcomming(document);
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	public List<SmsMessageBatch> readOutcomming(Document document){		
		Element outcommingElement = document.getRootElement().element(SmsMessageBatchFormatter.ELEMENT_OUTCOMMING);
		return this.read(outcommingElement);
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
}
