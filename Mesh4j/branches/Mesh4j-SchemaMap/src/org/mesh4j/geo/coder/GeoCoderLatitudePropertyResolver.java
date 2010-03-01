package org.mesh4j.geo.coder;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.mesh4j.sync.payload.mappings.IPropertyResolver;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class GeoCoderLatitudePropertyResolver implements IPropertyResolver {

	public static final String MAPPING_NAME = "geo.latitude";
	
	// MODEL VARIABLES
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS
	
	public GeoCoderLatitudePropertyResolver(IGeoCoder geoCoder){
		Guard.argumentNotNull(geoCoder, "geoCoder");
		this.geoCoder = geoCoder;
	}
	
	@Override
	public boolean accepts(String mappingName, String variableTemplate) {
		//return variableTemplate.startsWith("geoLatitude(") && variableTemplate.endsWith(")");
		return MAPPING_NAME.equals(mappingName);
	}

	@Override
	public String getPropertyValue(Element element, String variableTemplate) {
		String variable = getPropertyName(variableTemplate);
		Element resultElement = XMLHelper.selectSingleNode(variable, element, new HashMap<String, String>());
		if(resultElement == null){
			return "";
		}
		
		if(isAddress(variableTemplate)){
			GeoLocation geoLocation = this.geoCoder.getLocation(resultElement.getText());
			if(geoLocation != null){
				return String.valueOf(geoLocation.getLatitude());
			}
			return "";
		} else {
			return resultElement.getText();
		}
	}

	public static String getPropertyName(String mapping) {
		if(isAddress(mapping)){
			return StringUtils.substringBetween(mapping, "geoLatitude(", ")");
		} else {
			return mapping;
		}
	}

	private static boolean isAddress(String mapping) {
		return mapping.startsWith("geoLatitude(") && mapping.endsWith(")");
	}
	
	public static String makeMapping(String address, boolean isAddress) {
		if(isAddress){
			return "geoLatitude(" + address + ")";
		} else{
			return address;
		}
	}
}
