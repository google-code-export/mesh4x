package org.mesh4j.sync.payload.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.utils.XMLHelper;

// TODO (JMT) RDF Schema
public class SchemaResolver implements ISchemaResolver {

	// MODEL VARIABLES
	private Element schema;
	private List<IPropertyResolver> propertyResolvers = new ArrayList<IPropertyResolver>();
	
	// BUSINESS METHODS
	
	public SchemaResolver(IPropertyResolver... allPropertyResolvers) {
		this(DocumentHelper.createElement(ELEMENT_SCHEMA), allPropertyResolvers);
	}

	public SchemaResolver(Element schema, IPropertyResolver... allPropertyResolvers) {
		if(schema == null){
			this.schema = DocumentHelper.createElement(ELEMENT_SCHEMA);
		} else {
			this.schema = schema;	
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
		Element propElement = XMLHelper.selectSingleNode(propertyName, this.schema, new HashMap<String, String>());
		
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
	public Element getSchema() {
		return this.schema;
	}
	
}
