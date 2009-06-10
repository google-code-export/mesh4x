package org.mesh4j.ektoo;

import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.validations.Guard;

// TODO (JMT) refactoring: move idea to core
public class UISchema {

	// MODEL VARIABLES
	private String idNode;
	private IRDFSchema rdfSchema;
	
	// BUSINESS METHODS
	public UISchema(IRDFSchema rdfSchema, String idNode) {
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		Guard.argumentNotNullOrEmptyString(idNode, "idNode");
		
		this.idNode = idNode;
		this.rdfSchema = rdfSchema;
	}

	public IRDFSchema getRDFSchema() {
		return this.rdfSchema;
	}

	public String getIdNode() {
		return this.idNode;
	}	
}
