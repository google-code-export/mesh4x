package org.mesh4j.sync.payload.schema.xform;

import org.dom4j.Element;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.model.Item;

public class HasRDFSchemaORXFormFilter implements IFilter<Item>{

	public static final HasRDFSchemaORXFormFilter INSTANCE = new HasRDFSchemaORXFormFilter();

	@Override
	public boolean applies(Item item) {
		Element element = XFormRDFSchemaContentWriter.getXForm(item);
		return element.getText() != null && !element.getText().isEmpty();
	}

}
