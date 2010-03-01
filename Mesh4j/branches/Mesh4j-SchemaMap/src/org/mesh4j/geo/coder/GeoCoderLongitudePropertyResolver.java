package org.mesh4j.geo.coder;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.mesh4j.sync.payload.mappings.IPropertyResolver;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class GeoCoderLongitudePropertyResolver implements IPropertyResolver {

	public static final String MAPPING_NAME = "geo.longitude";
	
	// MODEL VARIABLES
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS
	
	public GeoCoderLongitudePropertyResolver(IGeoCoder geoCoder){
		Guard.argumentNotNull(geoCoder, "geoCoder");
		this.geoCoder = geoCoder;
	}
	
	@Override
	public boolean accepts(String mappingName, String variableTemplate) {
		//return variableTemplate.startsWith("geoLongitude(") && variableTemplate.endsWith(")");
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
				return String.valueOf(geoLocation.getLongitude());
			}
			return "";
		} else {
			return resultElement.getText();
		}
	}

	public static String getPropertyName(String mapping) {
		if(isAddress(mapping)){
			return StringUtils.substringBetween(mapping, "geoLongitude(", ")");
		} else {
			return mapping;
		}
	}

	private static boolean isAddress(String mapping) {
		return mapping.startsWith("geoLongitude(") && mapping.endsWith(")");
	}
		
	public static String makeMapping(String propertyName, boolean isAddress) {
		if(isAddress){
			return "geoLongitude(" + propertyName + ")";
		} else {
			return propertyName;
		}
	}

}
