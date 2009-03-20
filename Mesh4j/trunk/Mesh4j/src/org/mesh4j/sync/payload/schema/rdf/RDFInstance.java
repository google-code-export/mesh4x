package org.mesh4j.sync.payload.schema.rdf;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import org.dom4j.Element;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFInstance {
	
	// MODEL VARIABLES
	private RDFSchema schema;
	
	private OntModel model;
	private Individual domainObject;
	
	// BUSINESS MODEL

	protected RDFInstance(RDFSchema schema, String id) {
		super();		
		initializeBaseModel(schema);		
		this.domainObject = model.createIndividual(id, schema.getDomainClass());
		
	}
	
	protected RDFInstance(RDFSchema schema, String id, String rdfXml) {
		super();		
		initializeBaseModel(schema);

		StringReader sr = new StringReader(rdfXml);
		model.read(sr, "");		
		this.domainObject = model.getIndividual(id);	
	}
	
	private void initializeBaseModel(RDFSchema schema) {
		this.schema = schema;
		
        this.model = ModelFactory.createOntologyModel();
		model.add(schema.getRDFModel());
		model.setNsPrefix(schema.getOntologyNameSpace(), schema.getOntologyBaseUri());
		model.removeNsPrefix("rdfs");
	}

	public void setProperty(String propertyName, Object value) {
	
		String propertyUri = this.schema.getOntologyBaseUri()+ propertyName;
		Property domainObjectProperty = this.model.getProperty(propertyUri);
		if(this.domainObject.hasProperty(domainObjectProperty)){
			RDFNode oldValue = this.domainObject.getPropertyValue(domainObjectProperty);
			this.domainObject.removeProperty(domainObjectProperty, oldValue);
		}
		
		Object propertyValue = null;
		if(value instanceof Date){
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date)value);
			propertyValue = cal;
		} else {
			propertyValue = value;
		}
		this.domainObject.addLiteral(domainObjectProperty, propertyValue);
			
	}

	public String asXML() {
		this.model.remove(this.schema.getRDFModel());
		
		StringWriter sw = new StringWriter();
		this.model.write(sw, "RDF/XML-ABBREV");
		
		this.model.add(this.schema.getRDFModel());
		return sw.toString();
	}

	public String asPlainXML(String idColumnName) throws Exception {

		boolean idWasExported = false;
		StringBuffer sb = new StringBuffer();

		sb.append("<");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");
	 
		
		StmtIterator it = this.domainObject.listProperties();
		Statement statement;
		while(it.hasNext()){
			statement = it.nextStatement();
			
			Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			
			 if(RDF.type.getURI().equals(predicate.getURI())){
				 if(!idWasExported){
					 sb.append("<");
					 sb.append(idColumnName);
					 sb.append(">");
					 sb.append(subject.getLocalName());
					 sb.append("</");
					 sb.append(idColumnName);
					 sb.append(">");
				 
					 idWasExported = true;
				 }
			 } else {
				//OntResource range = dataTypeProperty.getRange();
				 
	            String fieldName = predicate.getLocalName();
	            Literal literal = (Literal) object;
	            Object fieldValue = literal.getValue();
	            
				 sb.append("<");
				 sb.append(fieldName);
				 sb.append(">");
				 sb.append(fieldValue);
				 sb.append("</");
				 sb.append(fieldName);
				 sb.append(">");
	          }
		}
		
		sb.append("</");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");

		
		Element element = XMLHelper.parseElement(sb.toString());
		return XMLHelper.canonicalizeXML(element);
	}

	public int getPropertyCount() {
		return this.model.listDatatypeProperties().toSet().size();
	}

	public Object getPropertyValue(String propertyName) {
		String propertyUri = this.schema.getOntologyBaseUri()+ propertyName;
		Property domainObjectProperty = this.model.getProperty(propertyUri);
		Literal literal = (Literal)this.domainObject.getPropertyValue(domainObjectProperty);
		if(literal == null){
			return null;
		} else {
			Object result = literal.getValue();
			if(result instanceof XSDDateTime){
				return ((XSDDateTime) result).asCalendar().getTime();
			} else {
				return result;
			}
		}
	}

	public String getPropertyName(int index) {
		DatatypeProperty domainObjectProperty = (DatatypeProperty)this.model.listDatatypeProperties().toList().get(index);
		return domainObjectProperty.getLocalName();
	}

	public String getPropertyType(String propertyName) {
		return this.schema.getPropertyType(propertyName);
	}

}
