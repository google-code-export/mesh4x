package org.mesh4j.sync.adapters.kml.exporter;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class KMLExporter {

	private final static Log LOGGER = LogFactory.getLog(KMLExporter.class);
	
	public static void export(String fileName, String documentName, List<Item> items, IMapping mappingResolver) throws Exception {
		String kmlXml = generateKML(documentName, items, mappingResolver);
		FileUtils.write(fileName, kmlXml.getBytes());
	}
	
	public static String generateKML(String documentName, List<Item> items, IMapping mappingResolver) {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageFormat.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>{0}</name><open>1</open>", documentName));
		for (Item item : items) {
			makeElement(sb, item.getContent().getPayload(), mappingResolver);
		}
		sb.append("</Document></kml>");
		return sb.toString();
	}
	
	private static void makeElement(StringBuffer sb, Element element, IMapping mappingResolver) {
		try{
			String longitude= mappingResolver.getValue(element, "//geo.longitude");
			String latitude= mappingResolver.getValue(element, "//geo.latitude");

			if(longitude != null && longitude.trim().length() > 0 && latitude != null && latitude.trim().length() > 0){
				String name = mappingResolver.getValue(element, "//item.title");
				String description = mappingResolver.getValue(element, "//item.description");
				sb.append(MessageFormat.format("<Placemark><name>{0}</name><description><![CDATA[{1}]]></description><Point><coordinates>{2},{3}</coordinates></Point></Placemark>", name, description, longitude, latitude));
			}
		} catch (MeshException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
