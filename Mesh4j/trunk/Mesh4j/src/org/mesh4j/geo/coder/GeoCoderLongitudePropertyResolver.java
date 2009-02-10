package org.mesh4j.geo.coder;

import java.util.HashMap;

import org.dom4j.Element;
import org.mesh4j.sync.payload.mappings.IPropertyResolver;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class GeoCoderLongitudePropertyResolver implements IPropertyResolver {

	// MODEL VARIABLES
	private IGeoCoder geoCoder;
	
	// BUSINESS METHODS
	
	public GeoCoderLongitudePropertyResolver(IGeoCoder geoCoder){
		Guard.argumentNotNull(geoCoder, "geoCoder");
		this.geoCoder = geoCoder;
	}
	
	@Override
	public boolean accepts(String variableTemplate) {
		return variableTemplate.startsWith("geoLongitude(") && variableTemplate.endsWith(")");
	}

	@Override
	public String getPropertyValue(Element element, String variableTemplate) {
		String variable = variableTemplate.substring(13, variableTemplate.length() -1);
		Element resultElement = XMLHelper.selectSingleNode(variable, element, new HashMap<String, String>());
		if(resultElement == null){
			return "";
		}
		
		GeoLocation geoLocation = this.geoCoder.getLocation(resultElement.getText());
		if(geoLocation != null){
			return String.valueOf(geoLocation.getLongitude());
		}
		return "";
	}

	public static String makeMapping(String address) {
		return "geoLongitude(" + address + ")";
	}

}
