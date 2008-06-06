package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.utils.XMLHelper;

// TODO (JMT) High: Test

public class KMLDocumentExtendedDataViewElementTests {

	@Test
	public void shouldGetAll() throws DocumentException{
		
		File file = new File(this.getClass().getResource("kmlWithExtendedDataToSync.kml").getFile());
		
		Document document = XMLHelper.readDocument(file);
		
		KMLDocumentExtendedDataViewElement viewElement = new KMLDocumentExtendedDataViewElement();
		List<Element> elements = viewElement.getAllElements(document);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(5, elements.size());
		
	}

	@Test
	public void shouldAdd() throws DocumentException{
		
		File file = new File(this.getClass().getResource("kmlWithExtendedDataToSync.kml").getFile());
		Document document = XMLHelper.readDocument(file);
		
		KMLDocumentExtendedDataViewElement viewElement = new KMLDocumentExtendedDataViewElement();
		List<Element> elements = viewElement.getAllElements(document);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(5, elements.size());
		
		File fileEmpty = new File(this.getClass().getResource("kmlDummyForSync.kml").getFile());
		Document newDoc = XMLHelper.readDocument(fileEmpty);
		
		for (Element newElement : elements) {
			viewElement.add(newDoc, newElement.createCopy());	
		}
		
		elements = viewElement.getAllElements(newDoc);
		
		Assert.assertNotNull(elements);
		Assert.assertEquals(5, elements.size());
		
	}

}
