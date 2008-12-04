package org.mesh4j.sync.utils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.kml.KmlNames;
import org.mesh4j.sync.adapters.kml.timespan.decorator.IKMLGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.MeshException;

public class EpiInfoKmlGenerator implements IKMLGenerator{

	protected static String makePlacemark(String id, String name, String description, String longitude, String latitude, String styleUrl, Date start, Date end) {

		return MessageFormat.format(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document>"+
			"<Placemark xml:id={0}>"+
			"	<name>{1}</name>"+
			"	<description><![CDATA[{2}]]></description>"+
			"	<styleUrl>{3}</styleUrl>"+
			"	<Point>"+
			"		<coordinates>{4},{5}</coordinates>"+
			"	</Point>"+
			"   <TimeSpan>"+
			"		<begin>{6}</begin>"+
			"		<end>{7}</end>"+
			"	</TimeSpan>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>", id, name, description, styleUrl, longitude, latitude, DateHelper.formatW3CDateTime(start), DateHelper.formatW3CDateTime(end));
	}
	
	
	private String getValueIll(Element payload) {
		Element ill = payload.element("ILL");
		if(ill == null || ill.getText() == null){
			return "0";
		}
		return ill.getText();
	}

	
	// IKMLGenerator methods
	@Override
	public void addElement(Document document, Item item) {
		try{
			if(!item.isDeleted()){
				Date start = item.getLastUpdate().getWhen();
				Date end = new Date();
				String xml = makePlacemark("'"+item.getSyncId()+"'", "name", item.getContent().getPayload().asXML(), "1", "1", ("0".equals(getValueIll(item.getContent().getPayload())) ? "#msn_ylw-pushpin0" : "#msn_ylw-pushpin"), start, end);

				Element itemElement = DocumentHelper.parseText(xml).getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).element(KmlNames.KML_ELEMENT_PLACEMARK);; 
				document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).add(itemElement.createCopy());
			}
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	public Element getElement(Document document, Item item) {
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		
		List<Element> items = XMLHelper.selectElements("//*[@xml:id='"+item.getSyncId()+"']", document.getRootElement(), namespaces);
		if(items.isEmpty()){
			return null;
		} else {
			return items.get(items.size()-1);
		}
	}

	@Override
	public boolean hasItemChanged(Document document, Element itemElement, Item item) {
		try{
			String itemIll = getValueIll(item.getContent().getPayload());
			String actualXML = itemElement.element(KmlNames.KML_ELEMENT_DESCRIPTION).getText();
			String actualItemIll = getValueIll(DocumentHelper.parseText(actualXML).getRootElement());
			return !itemIll.equals(actualItemIll);
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	public Document makeDocument(String documentName) {
		try{
			return DocumentHelper.parseText(readTemplateXML(documentName));
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	protected static String readTemplateXML(String documentName) throws Exception {		
		String templateFileName = "template.kml";
		byte[] templateBytes = FileUtils.read(templateFileName);
		String template = new String(templateBytes, "UTF-8");		
		return MessageFormat.format(template, documentName, "");
	}
}
