package org.mesh4j.sync.adapters.kml.exporter;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class KMLExporter {

	private final static Log LOGGER = LogFactory.getLog(KMLExporter.class);
	
	public static void export(String fileName, String documentName, List<Item> items, ISchema schema, IMapping mapping) throws Exception {
		String kmlXml = generateKML(documentName, items, schema, mapping);
		FileUtils.write(fileName, kmlXml.getBytes());
	}
	
	public static String generateKML(String documentName, List<Item> items, ISchema schema, IMapping mapping) {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageFormat.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>{0}</name><open>1</open>", documentName));
		for (Item item : items) {
			Element element = null;
			if(schema == null){
				element = item.getContent().getPayload().createCopy();
			} else {
				element = schema.asInstancePlainXML(item.getContent().getPayload(), ISchema.EMPTY_FORMATS);
			}
			String xml = makePlacemark(element, mapping);
			if(xml != null){
				sb.append(xml);
			}
		}
		sb.append("</Document></kml>");
		return sb.toString();
	}
	
	public static String makePlacemark(Element element, IMapping mapping) {
		try{
			if(mapping.hasMapping(GeoCoderLocationPropertyResolver.MAPPING_NAME)){
				String location = mapping.getValue(element, GeoCoderLocationPropertyResolver.MAPPING_NAME);
				if(location != null && location.trim().length() > 0){
					String name = mapping.getValue(element, ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
					String description = mapping.getValue(element, ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
					return MessageFormat.format("<Placemark><name>{0}</name><description><![CDATA[{1}]]></description><Point><coordinates>{2}</coordinates></Point></Placemark>", name, description, location);
				}
			} else if (mapping.hasMapping(GeoCoderLatitudePropertyResolver.MAPPING_NAME) && mapping.hasMapping(GeoCoderLongitudePropertyResolver.MAPPING_NAME)){
				String longitude = mapping.getValue(element, GeoCoderLongitudePropertyResolver.MAPPING_NAME);
				String latitude = mapping.getValue(element, GeoCoderLatitudePropertyResolver.MAPPING_NAME);
				
				if(longitude != null && longitude.trim().length() > 0 && latitude != null && latitude.trim().length() > 0){
					String name = mapping.getValue(element, ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
					String description = mapping.getValue(element, ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
					return MessageFormat.format("<Placemark><name>{0}</name><description><![CDATA[{1}]]></description><Point><coordinates>{2},{3}</coordinates></Point></Placemark>", name, description, longitude, latitude);
				}
			}
		} catch (MeshException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public static void export(String fileName, ISyncAdapter adapter, IRDFSchema rdfSchema, Mapping mappings) throws Exception {
		if(adapter instanceof HttpSyncAdapter){
			String template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>{0}</name><open>1</open><NetworkLink><name>{0}</name><visibility>0</visibility><open>0</open><refreshVisibility>0</refreshVisibility><flyToView>0</flyToView><Link><href>{1}</href></Link></NetworkLink></Document></kml>";
			String url = HttpSyncAdapter.makeKmlURL((((HttpSyncAdapter)adapter).getURL()));
			FileUtils.write(fileName, MessageFormat.format(template, rdfSchema.getOntologyClassName(), url).getBytes());
		} else {
			export(fileName, rdfSchema.getOntologyClassName(), adapter.getAll(), rdfSchema, mappings);
		}
		
	}

}
