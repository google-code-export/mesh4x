package org.mesh4j.meshes.io;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.mesh4j.meshes.model.SyncLog;

public class LogMarshaller {

	public static void save(List<SyncLog> items, File logFile) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SyncLogList.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(new SyncLogList(items), logFile);
		} catch (JAXBException e) {
			System.out.println(e.toString());
			throw new Error(e);
		}
	}
	
	public static List<SyncLog> load(File logFile) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(SyncLogList.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			SyncLogList list = (SyncLogList) unmarshaller.unmarshal(logFile);
			return list.getItems();
		} catch (JAXBException e) {
			throw new Error(e);
		}
	}
	
	@XmlRootElement(name = "log")
	@XmlAccessorType(XmlAccessType.FIELD)
	private static class SyncLogList {
		
		@XmlElement(name = "item")
		private List<SyncLog> items;
		
		@SuppressWarnings("unused")
		public SyncLogList() {
		}
		
		public SyncLogList(List<SyncLog> items) {
			this.items = items;
		}
		
		public List<SyncLog> getItems() {
			return items;
		}
	}
}
