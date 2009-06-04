package org.mesh4j.sync.payload.mappings;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.utils.XMLHelper;

public class Mapping implements IMapping {

	// MODEL VARIABLES
	private Element mappings;
	private List<IPropertyResolver> propertyResolvers = new ArrayList<IPropertyResolver>();
	
	// BUSINESS METHODS

	public Mapping(Element mappings, IPropertyResolver... allPropertyResolvers) {
		if(mappings == null){
			this.mappings = DocumentHelper.createElement(ELEMENT_MAPPING);
		} else {
			if(ELEMENT_MAPPING.equals(mappings.getName())){
				this.mappings = mappings.createCopy();
			} else {
				this.mappings = DocumentHelper.createElement(ELEMENT_MAPPING);
				this.mappings.add(mappings.createCopy());
			}
		}
		
		for (IPropertyResolver propertyResolver : allPropertyResolvers) {
			this.propertyResolvers.add(propertyResolver);		
		}
	}

	@Override
	public String getValue(Element element, String propertyName) {
		
		Element payload = null;
		if(!ELEMENT_PAYLOAD.equals(element.getName())){
			payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
			payload.add(element.createCopy());
		} else {
			payload = element;
		}
		
		String template = "";
		//Element propElement = schema.element(propertyName);
		Element propElement = XMLHelper.selectSingleNode(propertyName, this.mappings, new HashMap<String, String>());
		
		if(propElement != null){
			template = propElement.getText();
			ArrayList<String> variables = getVariables(template);
			for (String variable : variables) {
				String value = getElementValue(payload, variable);

				String fullVariable = "{"+ variable + "}";
				template = StringUtils.replace(template, fullVariable, value);
			}
		}
		return template;
	}

	private String getElementValue(Element element, String variable) {
		
		IPropertyResolver propertyResolver = getPropertyResolver(variable);
		if(propertyResolver != null){
			return propertyResolver.getPropertyValue(element, variable);
		} else {
			Element resultElement = XMLHelper.selectSingleNode(variable, element, new HashMap<String, String>());
			if(resultElement == null){
				return null;
			}
			return resultElement.getText();
		}
		
//		Element resultElement = element;
//		String[] properties = variable.split("@");
//		for (String property : properties) {
//			resultElement = resultElement.element(property);
//			if(resultElement == null){
//				return "";
//			}
//		}
//		return resultElement.getText();
	}

	private IPropertyResolver getPropertyResolver(String variable) {
		for (IPropertyResolver propertyResolver : this.propertyResolvers) {
			if(propertyResolver.accepts(variable)){
				return propertyResolver;
			}
		}
		return null;
	}

	private ArrayList<String> getVariables(String template){
		ArrayList<String> result = new ArrayList<String>();
		String text = template;
		String var = StringUtils.substringBetween(text, "{", "}");
		while(var != null){
			result.add(var);
			int index = text.indexOf("}");
			text = text.substring(index+1, text.length());
			var = StringUtils.substringBetween(text, "{", "}");
		}
		return result;
	}

	@Override
	public String getMapping(String mappingName) {
		Element propElement = XMLHelper.selectSingleNode(mappingName, this.mappings, new HashMap<String, String>());
		if(propElement == null && !mappingName.startsWith("//")){
			propElement = XMLHelper.selectSingleNode("//"+mappingName, this.mappings, new HashMap<String, String>());
		}
		String template = "";
		if(propElement != null){
			template = propElement.getText();
		}
		return template;
	}
	
	public String getAttribute(String mappingName) {
		String mapping = getMapping(mappingName);
		return StringUtils.substringBetween(mapping, "{", "}");
	}

	@Override
	public String asXML() {
		return this.mappings.asXML();
	}

	@SuppressWarnings("unchecked")
	public String asXMLText() {
		StringBuffer sbMappings = new StringBuffer();
		List<Element> mappingElements = this.mappings.elements();
		for (Element mapElement : mappingElements) {
			sbMappings.append(mapElement.asXML());
			sbMappings.append("\n");
		}
		return sbMappings.toString();
	}
	
}
