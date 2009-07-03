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
		rdfSchema.addBooleanProperty("ill", "ill", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDateTimeProperty("dateOnset", "dateOnSet", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDecimalProperty("decimal", "decimal", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addDoubleProperty("AgeDouble", "ageDouble", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addIntegerProperty("AgeInt", "ageInt", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addLongProperty("AgeLong", "ageLong", IRDFSchema.DEFAULT_LANGUAGE);
		rdfSchema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
	
		return rdfSchema;
	
	}
	
}
