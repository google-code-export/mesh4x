package org.mesh4j.sync.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.parsers.ognl.OgnlCondition;
import org.mesh4j.sync.payload.schema.ISchema;

/**
 * FilterQuery is a IFilter<Item> based on a dynamic conditional expression.
 * All item content properties can be used in the expression.
 *  
 * If expression is null or empty then the evaluation of the filter is true by default, 
 * otherwise the expression is evaluated with the Item.getContent().getPayload(). 
 * The context map is filled with each element property defined in the schema. 
 * If the schema is null, the context map is filled with text plain xml (sub elements).
 *  
 * @author jmt
 */
public class FilterQuery implements IFilter<Item> {
	
	// MODEL VARIABLES
	private OgnlCondition condition;
	private ISchema schema;
	
	// BUSINESS METHODs
	/** 
	 * If conditionExpression is null or empty then the evaluation of the filter is true by default, 
	 * otherwise the expression is evaluated with the element. 
	 * The context map is filled with each element property defined in the schema. 
	 * If the schema is null, the context map is filled with text plain xml (sub elements).
	 * 
	 * @param filterQuery the conditional expression
	 * @param schema an instance of ISchema, used to obtains properties values from Item content
	 */
	public FilterQuery(String conditionExpression, ISchema schema) {		
		if(conditionExpression != null && conditionExpression.length() > 0){
			this.condition = new OgnlCondition(conditionExpression);
		} 
		this.schema = schema;
	}

	/**
	 * Evaluate the filter
	 * @param obj The item to verify the filter
	 * @return true if the item applies the filter, otherwise false
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean applies(Item obj) {
		if(obj == null){
			return false;
		}
		
		if(this.condition == null){
			return true;
		} else {
			Element element = obj.getContent().getPayload();
			Map context = getPropertiesAsLexicalFormMap(element);
			return this.condition.eval(context);
		}
	}
	
	/**
	 * Obtains the map with all element property values
  	 * @param element
	 * @return the map with propertyName/propertyValue as lexical form associations
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getPropertiesAsLexicalFormMap(Element element) {
		if(this.schema == null){
			HashMap<String, String> result = new HashMap<String, String>();
			
			List<Element> elements = element.elements();
			for (Element ele : elements) {
				String propertyName = ele.getName();
				String propertyValue = ele.getText();
				result.put(propertyName, propertyValue);
			}
			return result;
		} else {
			return this.schema.getPropertiesAsLexicalFormMap(element);
		}
	}
}
