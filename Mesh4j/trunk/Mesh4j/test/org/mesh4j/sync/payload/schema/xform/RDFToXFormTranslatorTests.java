package org.mesh4j.sync.payload.schema.xform;

import org.junit.Test;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class RDFToXFormTranslatorTests {

	
	@Test
	public void should(){
		String xmlXForm = SchemaToXFormTranslator.translate(getDefaultRDFSchema());
		
		System.out.println(xmlXForm);
	}

	private IRDFSchema getDefaultRDFSchema() {
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
		rdfSchema.addBooleanProperty("ill", "ill", "en");
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", "en");
		rdfSchema.addDecimalProperty("decimal", "decimal", "en");
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", "en");
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", "en");
		rdfSchema.addLongProperty("AgeLong", "ageLong", "en");
		rdfSchema.addStringProperty("name", "name", "en");
	
		return rdfSchema;
	
	}
	
}
