package org.mesh4j.sync.adapters.kml.exporter;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.geo.coder.GeoCoderLatitudePropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLocationPropertyResolver;
import org.mesh4j.geo.coder.GeoCoderLongitudePropertyResolver;
import org.mesh4j.geo.coder.GoogleGeoCoder;
import org.mesh4j.geo.coder.IGeoCoder;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.utils.XMLHelper;

public class KMLExporterTests {

	@Test
	public void shouldCreatePlacemark(){
		
		String mappingXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
			"<mappings>"+
			"	<item.title>Patient: {Patient/Name}</item.title>"+
			"	<item.description>Adress: {Patient/Address}</item.description>"+
			"	<geo.location>{geoLocation(Patient/Address)}</geo.location>"+
			"	<geo.longitude>{geoLongitude(Patient/Address)}</geo.longitude>"+
			"	<geo.latitude>{geoLatitude(Patient/Address)}</geo.latitude>"+
			"</mappings>";
		
		Element mappingElement = XMLHelper.parseElement(mappingXML);
		
		IGeoCoder geoCoder = new GoogleGeoCoder("ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg");
		GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
		GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
		GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
		Mapping mapping = new Mapping(mappingElement, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
	
		
		String dataXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
			"<Patient>"+
			"	<Name>Juan</Name>"+
			"	<Address>Buenos Aires</Address>"+
			"</Patient>";
		Element element = XMLHelper.parseElement(dataXML);
		
		String placemark = KMLExporter.makePlacemark(element, mapping);
		Assert.assertEquals("<Placemark><name>Patient: Juan</name><description><![CDATA[Adress: Buenos Aires]]></description><Point><coordinates>-58.3731613,-34.6084175</coordinates></Point></Placemark>", placemark);
	}
	
	@Test
	public void shouldNoCreatePlacemarkWhenGeoLocationIsNull(){
		
		String mappingXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
			"<mappings>"+
			"	<item.title>Patient: {Patient/Name}</item.title>"+
			"	<item.description>Adress: {Patient/Address}</item.description>"+
			"	<geo.location>{geoLocation(Patient/Address)}</geo.location>"+
			"	<geo.longitude>{geoLongitude(Patient/Address)}</geo.longitude>"+
			"	<geo.latitude>{geoLatitude(Patient/Address)}</geo.latitude>"+
			"</mappings>";
		
		Element mappingElement = XMLHelper.parseElement(mappingXML);
		
		IGeoCoder geoCoder = new GoogleGeoCoder("ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg");
		GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
		GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
		GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);
		Mapping mapping = new Mapping(mappingElement, propertyResolverLat, propertyResolverLon, propertyResolverLoc);
	
		
		String dataXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
			"<Patient>"+
			"	<Name>Juan</Name>"+
			"	<Address>klklklkl</Address>"+
			"</Patient>";
		Element element = XMLHelper.parseElement(dataXML);
		
		Assert.assertNull(KMLExporter.makePlacemark(element, mapping));

	}
}
