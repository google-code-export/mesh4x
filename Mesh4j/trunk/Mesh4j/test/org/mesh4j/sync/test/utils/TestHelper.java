package org.mesh4j.sync.test.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.kml.KmlNames;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;


public class TestHelper {

	private static long LAST_ID = 0;
	private static Random ID_GENERATOR = new Random();

	public synchronized static String newID() {
		int currentID = random();
		while (LAST_ID == currentID) {
			currentID = random();
		}
		LAST_ID = currentID;
		return String.valueOf(LAST_ID);
	}

	private static int random() {
		int i = ID_GENERATOR.nextInt();
		if (i < 0) {
			i = i * -1;
		}
		return i;
	}
	
	public static String newText(int length)
	{
		String result = "";

		while (result.length() < length)
			result += System.nanoTime();

		result = result.substring(0, length);
		return result;
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

	public static Date nowSubtractMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, (-1 * i));
		return cal.getTime();
	}

	public static Date nowSubtractHours(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, (-1 * i));
		return cal.getTime();
	}
	
	public static Date nowAddHours(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, i);
		return cal.getTime();
	}

	public static Date nowAddMinutes(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, i);
		return cal.getTime();
	}

	public static Date nowAddDays(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	public static Date nowAddSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, i);
		return cal.getTime();
	}

	public static Date nowSubtractSeconds(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, (-1 * i));
		return cal.getTime();
	}

	public static Date makeDate(int year, int month, int day, int hour,
			int minute, int second, int millisecond) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, millisecond);
		cal.set(Calendar.AM_PM, hour <= 12 ? Calendar.AM : Calendar.PM);
		return cal.getTime();
	}

	public static Date nowSubtractDays(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, (-1 * i));
		return cal.getTime();
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static File makeNewXMLFile(String xml) {
		return makeNewXMLFile(xml, ".xml");
	}
	
	public static File makeNewXMLFile(String xml, String fileExtension) {
		File file = new File(TestHelper.fileName(IdGenerator.INSTANCE.newID() + fileExtension));
		XMLHelper.write(xml, file);
		return file;
	}

	public static String readFileContent(String fileName) throws IOException {

		StringBuffer contents = new StringBuffer();

		BufferedReader input = new BufferedReader(new FileReader(new File(
				fileName)));
		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} finally {
			input.close();
		}
		return contents.toString();

	}
	

	public static void writeFile(String fileName, String contents) throws IOException{
		Writer output = new BufferedWriter(new FileWriter(new File(fileName)));
		try {
			output.write(contents);
		} finally {
			output.close();
		}
	}

	public static File makeNewKMZFile(String xml) {
		File file = new File(fileName(IdGenerator.INSTANCE.newID() + ".kmz"));
		try {
			ZipUtils.write(file, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, xml);
		} catch (IOException e) {
			throw new MeshException(e);
		}
		return file;
	}

	public static Document readKMZDocument(File file) {
		try{
			String xml = ZipUtils.getTextEntryContent(file, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
			return DocumentHelper.parseText(xml);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

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
	
	public static void assertSync(SyncEngine syncEngine) {
		List<Item> conflicts = syncEngine.synchronize();	
		assertSyncResult(syncEngine, conflicts);
		
	}

	public static void assertSyncResult(SyncEngine syncEngine, List<Item> conflicts) {
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		ISyncAdapter source = syncEngine.getSource();
		ISyncAdapter target = syncEngine.getTarget();
		
		if(source instanceof ISyncAware){
			((ISyncAware) source).beginSync();
		}
		if(target instanceof ISyncAware){
			((ISyncAware) target).beginSync();
		}
		
		List<Item> sourceItems = source.getAll();
		List<Item> targetItems = target.getAll();
		Assert.assertEquals(sourceItems.size(), targetItems.size());
		
		if(target instanceof ISupportMerge){
			for (Item targetItem : targetItems) {
				assertItem(targetItem, source);
			}
		} else {
			for (Item sourceItem : sourceItems) {
				assertItem(sourceItem, target);
			}
		}

		if(source instanceof ISyncAware){
			((ISyncAware) source).endSync();
		}
		if(target instanceof ISyncAware){
			((ISyncAware) target).endSync();
		}
	}
	
	public static void assertItem(Item sourceItem, ISyncAdapter... adapters) {
		assertItem(null,sourceItem, adapters);
	}
	public static void assertItem(String msg, Item sourceItem, ISyncAdapter... adapters) {
		for (ISyncAdapter syncAdapter : adapters) {
			Item targetItem = syncAdapter.get(sourceItem.getSyncId());
			boolean isOk = sourceItem.equals(targetItem);
			if(!isOk){
				System.out.println("Source: "+ sourceItem.getContent().getPayload().asXML());
				System.out.println("Target: "+ targetItem.getContent().getPayload().asXML());
				if(msg == null){
					Assert.assertEquals(XMLHelper.canonicalizeXML(sourceItem.getContent().getPayload()), XMLHelper.canonicalizeXML(targetItem.getContent().getPayload()));
				}else{
					Assert.assertEquals(msg, XMLHelper.canonicalizeXML(sourceItem.getContent().getPayload()), XMLHelper.canonicalizeXML(targetItem.getContent().getPayload()));
				}
			}
			Assert.assertTrue(isOk);
		}
	}
}
