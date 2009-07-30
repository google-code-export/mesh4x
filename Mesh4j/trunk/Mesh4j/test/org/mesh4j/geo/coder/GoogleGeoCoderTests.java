package org.mesh4j.geo.coder;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.payload.mappings.Mapping;

public class GoogleGeoCoderTests {

	@Test
	public void shouldGetLocation(){
		GoogleGeoCoder geoCoder = new GoogleGeoCoder("ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg");
		
		GeoLocation geoLocation = geoCoder.getLocation("Buenos Aires");
		Assert.assertNotNull(geoLocation);
		
		System.out.println(geoLocation.toString());
	}
	
	@Test
	public void shouldGetLatLong() throws DocumentException{
		GoogleGeoCoder geoCoder = new GoogleGeoCoder("ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg");

		GeoCoderLatitudePropertyResolver propertyResolver = new GeoCoderLatitudePropertyResolver(geoCoder);
		
		String variableTemplate = "geoLatitude(address)";
		Assert.assertTrue(propertyResolver.accepts(GeoCoderLatitudePropertyResolver.MAPPING_NAME, variableTemplate));

		Element element = DocumentHelper.parseText("<patient><name>jose</name><address>Buenos Aires</address></patient>").getRootElement();
		Assert.assertEquals("-34.6084175", propertyResolver.getPropertyValue(element, variableTemplate));
		
		
		GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
		String variableTemplateLon = "geoLongitude(address)";
		Assert.assertTrue(propertyResolverLon.accepts(GeoCoderLongitudePropertyResolver.MAPPING_NAME, variableTemplateLon));

		Assert.assertEquals("-58.3731613", propertyResolverLon.getPropertyValue(element, variableTemplateLon));
		
	}
	
	@Test
	public void shouldSchemaResolver() throws DocumentException{
		GoogleGeoCoder geoCoder = new GoogleGeoCoder("ABQIAAAAjpkAC9ePGem0lIq5XcMiuhT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTS6gjckBmeABOGXIUiOiZObZESPg");

		GeoCoderLatitudePropertyResolver propertyResolverLat = new GeoCoderLatitudePropertyResolver(geoCoder);
		GeoCoderLongitudePropertyResolver propertyResolverLon = new GeoCoderLongitudePropertyResolver(geoCoder);
		GeoCoderLocationPropertyResolver propertyResolverLoc = new GeoCoderLocationPropertyResolver(geoCoder);

		Element mappingsElement = DocumentHelper.parseText("<mappings><geo.latitude>{geoLatitude(patient/address)}</geo.latitude><geo.longitude>{geoLongitude(patient/address)}</geo.longitude><geo.location>{geoLocation(patient/address)}</geo.location></mappings>").getRootElement();
		Mapping mappings = new Mapping(mappingsElement, propertyResolverLat, propertyResolverLon, propertyResolverLoc);

		Element element = DocumentHelper.parseText("<patient><name>jose</name><address>Buenos Aires</address></patient>").getRootElement();
		Assert.assertEquals("-34.6084175", mappings.getValue(element, "geo.latitude"));
		Assert.assertEquals("-58.3731613", mappings.getValue(element, "geo.longitude"));
		Assert.assertEquals("-58.373,-34.608", mappings.getValue(element, "geo.location"));
		
		mappingsElement = DocumentHelper.parseText("<mappings><geo.latitude>{patient/lat}</geo.latitude><geo.longitude>{patient/lon}</geo.longitude><geo.location>{geoLocation(patient/address)}</geo.location></mappings>").getRootElement();
		mappings = new Mapping(mappingsElement, propertyResolverLat, propertyResolverLon, propertyResolverLoc);

		element = DocumentHelper.parseText("<patient><name>jose</name><lat>-34.6084175</lat><lon>-58.3731613</lon><address>Buenos Aires</address></patient>").getRootElement();
		Assert.assertEquals("-34.6084175", mappings.getValue(element, "geo.latitude"));
		Assert.assertEquals("-58.3731613", mappings.getValue(element, "geo.longitude"));
		Assert.assertEquals("-58.373,-34.608", mappings.getValue(element, "geo.location"));
	}
	
}