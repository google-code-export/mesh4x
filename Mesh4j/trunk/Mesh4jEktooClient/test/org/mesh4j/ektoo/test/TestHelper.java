package org.mesh4j.ektoo.test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.model.Item;

public class TestHelper {

	public static String baseDirectoryForTest() {
		return "../../../tests/junit/";
	}

	public static String baseDirectoryRootForTest() {
		return "../../../tests/";
	}
	
	public static String fileName(String name) {
		return baseDirectoryForTest() + name;
	}

	public static File makeFileAndDeleteIfExists(String fileName) throws IOException{
		String myFileName = TestHelper.fileName(fileName);
		File file = new File(myFileName);
		if(file.exists()){
			file.delete();
		}
		Assert.assertFalse(file.exists());
		return file;
	}

	public static void syncAndAssert(SyncEngine syncEngine) {
		List<Item> conflicts = syncEngine.synchronize();
	
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		if(syncEngine.getSource() instanceof ISyncAware){
			((ISyncAware) syncEngine.getSource()).beginSync();
		}
		if( syncEngine.getTarget() instanceof ISyncAware){
			((ISyncAware)  syncEngine.getTarget()).beginSync();
		}
		
		List<Item> sourceItems = syncEngine.getSource().getAll();
		List<Item> targetItems = syncEngine.getTarget().getAll();
		Assert.assertEquals(sourceItems.size(), targetItems.size());
		
		for (Item sourceItem : sourceItems) {
			Item targetItem = syncEngine.getTarget().get(sourceItem.getSyncId());
			boolean isOk = sourceItem.equals(targetItem);
			if(!isOk){
				System.out.println("Source: "+ sourceItem.getContent().getPayload().asXML());
				System.out.println("Target: "+ targetItem.getContent().getPayload().asXML());
				Assert.assertEquals(sourceItem.getContent().getPayload().asXML(), targetItem.getContent().getPayload().asXML());	
			}
			Assert.assertTrue(isOk);
		}
	}

	public static Element makeElement(String xmlAsString) {
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlAsString);
		} catch (DocumentException e) {
			throw new IllegalArgumentException(e);
		}
		return doc.getRootElement();
	}

	public static Date now() {
		return new Date();
	}
}
