package org.mesh4j.sync.payload.schema.rdf;

import java.io.InputStream;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class RDFTest {

	// SPIKE JENNA RDF AND OWL
	@Test
	public void shouldCreateRDFModel(){
		printRDF("mesh4x_schema1.rdf");
        
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("rdfs", RDFS.getURI());
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("xsd", XSD.getURI());
		model.setNsPrefix("Oswego", "http://mesh4x/Oswego#");
		
		Resource domainClass = model.createResource("http://mesh4x/Oswego#Patient");
		domainClass.addProperty(RDF.type, RDFS.Class);
		
		Resource domainProperty = model.createResource("http://mesh4x/Oswego#Code");
		domainProperty.addProperty(RDF.type, RDF.Property);
		
		model.add(domainProperty, RDFS.label, "CODE");
		model.add(domainProperty, RDFS.domain, domainClass);
		model.add(domainProperty, RDFS.range, XSD.xstring);
		
        model.write(System.out);   
        System.out.println("");
        model.write(System.out, "RDF/XML-ABBREV");
	}

	@Test
	public void shouldCreateRDFInstanceModel(){
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("Oswego", "http://mesh4x/Oswego#");
		
		Resource domainObject = model.createResource("Oswego:julian");		
		Property domainProperty = model.createProperty("http://mesh4x/Oswego#","Name");		
		Literal domainValue = model.createTypedLiteral("juan");
		
		domainObject.addProperty(domainProperty, domainValue);
		
		model.write(System.out);
	}

	@Test
	public void shouldCreateOntology(){
		OntModel schema = ModelFactory.createOntologyModel();
		schema.setNsPrefix("Oswego", "http://mesh4x/Oswego#");
		
		OntClass domainClass = schema.createClass("Oswego:Patient");

		DatatypeProperty domainProperty = schema.createDatatypeProperty("Oswego:Code");
		domainProperty.addDomain(domainClass);
		domainProperty.addRange(XSD.xstring);
		domainProperty.addLabel("CODE", "en");

		schema.write(System.out);
        System.out.println("");
        schema.write(System.out, "RDF/XML-ABBREV");
        System.out.println("");
        

	}
	
	@Test
	public void shouldCreateOntologyIndividual(){
		
		OntModel schema = ModelFactory.createOntologyModel();
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix("rdf", RDF.getURI());
		schema.setNsPrefix("xsd", XSD.getURI());
		schema.setNsPrefix("Oswego", "http://mesh4x/Oswego#");
		
		OntClass domainClass = schema.createClass("http://mesh4x/Oswego#Patient");

		DatatypeProperty domainProperty = schema.createDatatypeProperty("http://mesh4x/Oswego#Code");
		domainProperty.addDomain(domainClass);
		domainProperty.addRange(XSD.xstring);
		domainProperty.addLabel("CODE", "en");
	
        ////////////
		        
        OntModel model = ModelFactory.createOntologyModel();
		model.add(schema);
		model.setNsPrefix("Oswego", "http://mesh4x/Oswego#");
		
		OntClass domainObjectClass = model.getOntClass("http://mesh4x/Oswego#Patient");
		Property domainObjectProperty = model.getProperty("http://mesh4x/Oswego#Code");
		
		Individual domainObject = model.createIndividual("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a", domainObjectClass);
		domainObject.addLiteral(domainObjectProperty, "P35");
		
		model.remove(schema);
		model.write(System.out);
        System.out.println("");
        model.write(System.out, "RDF/XML-ABBREV");
	}
	
	private void printRDF(String fileName) {
		String inputFileName = this.getClass().getResource(fileName).getFile();
        
        Model model1 = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + inputFileName + " not found");
        }

        // read the RDF/XML file
        model1.read(in, "");

        // write it to standard out
        model1.write(System.out);    
        System.out.println("");
        model1.write(System.out, "RDF/XML-ABBREV");
        System.out.println(" ---------------");

	}
	
	// RDFSCHEMA TEST
	@Test
	public void shouldRDFSchemaCreateNewOntologyProgramaticaly(){
        RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");
        System.out.println(rdfSchema.asXML());
	}
	
	@Test
	public void shouldRDFSchemaCreateNewIndividualProgramaticaly(){
        RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");
        
        RDFInstance rdfInstance = rdfSchema.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        rdfInstance.setProperty("code", "P55");
        rdfInstance.setProperty("name", "juan");
        rdfInstance.setProperty("age", 31);
        rdfInstance.setProperty("ill", true);
        rdfInstance.setProperty("dateOnset", new Date());
        System.out.println(rdfInstance.asXML());
	}
	
	@Test
	public void shouldRDFIndividualExportPlainXML() throws Exception{
        RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");
        
        RDFInstance rdfInstance = rdfSchema.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        rdfInstance.setProperty("code", "P55");
        rdfInstance.setProperty("name", "juan");
        rdfInstance.setProperty("age", 31);
        rdfInstance.setProperty("ill", true);
        rdfInstance.setProperty("dateOnset", new Date());
        System.out.println(rdfInstance.asPlainXML("id"));
	}
	
	@Test
	public void shouldRDFSchemaCreateNewIndividualFromRDFXML(){
        RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");

        String xml = "<rdf:RDF"+
        			"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+
        			"   xmlns:Oswego=\"http://mesh4x/Oswego#\""+
        			"   xmlns:owl=\"http://www.w3.org/2002/07/owl#\""+
        			"   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">"+
        			"   <Oswego:Patient rdf:about=\"urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a\">"+
        			"     <Oswego:name rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">juan</Oswego:name>"+
        			"     <Oswego:code rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">P55</Oswego:code>"+
        			"	  <Oswego:dateOnset rdf:datatype=\"http://www.w3.org/2001/XMLSchema#datetime\">2009-10-10T09:09:00z</Oswego:dateOnset>"+
        		    "	  <Oswego:ill rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</Oswego:ill>"+
        		    "	  <Oswego:age rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">31</Oswego:age>"+
        			"   </Oswego:Patient>"+
					"</rdf:RDF>";
        
        RDFInstance rdfInstance = rdfSchema.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a", xml);
        System.out.println(rdfInstance.asXML());
	}
	
	@Test
	public void shouldRDFSchemaCreateNewIndividualFromPlainXML() throws Exception{
        RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");

        String xml = "<Patient><dateOnset>2009-03-17T19:06:06.264Z</dateOnset><ill>true</ill><age>31</age><name>juan</name><code>P55</code><id>cc11-7ba4-4594-abaf-3620fbee211a</id></Patient>";
        
        RDFInstance rdfInstance = rdfSchema.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a", xml, "id");
        System.out.println(rdfInstance.asXML());
        
	}
	
	// canonical rdf xml
	
	@Test
	public void shouldCanonicalizeRDFXML(){
        
		RDFSchema rdfSchema = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema.addStringProperty("code", "Code", "en");
        rdfSchema.addStringProperty("name", "Name", "en");
        rdfSchema.addIntegerProperty("age", "Age", "en");
        rdfSchema.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema.addDateTimeProperty("dateOnset", "DateOnset", "en");

        RDFInstance rdfInstance = rdfSchema.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        String rdfXml = rdfInstance.asXML();

        RDFSchema rdfSchema2 = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema2.addStringProperty("code", "Code", "en");
        rdfSchema2.addStringProperty("name", "Name", "en");
        rdfSchema2.addIntegerProperty("age", "Age", "en");
        rdfSchema2.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema2.addDateTimeProperty("dateOnset", "DateOnset", "en");

        RDFInstance rdfInstance2 = rdfSchema2.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        String rdfXml2 = rdfInstance2.asXML();
        
        Assert.assertEquals(rdfXml, rdfXml2);
	
        Date date = new Date();
        
        rdfInstance.setProperty("code", "Code");
        rdfInstance.setProperty("name", "Name");
        rdfInstance.setProperty("age", 30);
        rdfInstance.setProperty("ill", true);
        rdfInstance.setProperty("dateOnset", date);
        rdfXml = rdfInstance.asXML();

        rdfInstance2.setProperty("code", "Code");
        rdfInstance2.setProperty("name", "Name");
        rdfInstance2.setProperty("age", 30);
        rdfInstance2.setProperty("ill", true);
        rdfInstance2.setProperty("dateOnset", date);
        rdfXml2 = rdfInstance2.asXML();
        
        Assert.assertEquals(rdfXml, rdfXml2);
        
		RDFSchema rdfSchema3 = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema3.addStringProperty("code", "Code", "en");
        rdfSchema3.addStringProperty("name", "Name", "en");
        rdfSchema3.addIntegerProperty("age", "Age", "en");
        rdfSchema3.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema3.addDateTimeProperty("dateOnset", "DateOnset", "en");

        RDFInstance rdfInstance3 = rdfSchema3.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        rdfInstance3.setProperty("code", "Code");
        rdfInstance3.setProperty("name", "Name");
        rdfInstance3.setProperty("age", 30);
        rdfInstance3.setProperty("ill", true);
        rdfInstance3.setProperty("dateOnset", date);
        String rdfXml3 = rdfInstance.asXML();

        RDFSchema rdfSchema4 = new RDFSchema("Oswego", "http://mesh4x/Oswego#", "Patient");
        rdfSchema4.addStringProperty("code", "Code", "en");
        rdfSchema4.addStringProperty("name", "Name", "en");
        rdfSchema4.addIntegerProperty("age", "Age", "en");
        rdfSchema4.addBooleanProperty("ill", "is Ill", "en");
        rdfSchema4.addDateTimeProperty("dateOnset", "DateOnset", "en");

        RDFInstance rdfInstance4 = rdfSchema4.createNewInstance("urn:uuid:9274cc11-7ba4-4594-abaf-3620fbee211a");
        rdfInstance4.setProperty("dateOnset", date);
        rdfInstance4.setProperty("code", "Code");
        rdfInstance4.setProperty("ill", true);
        rdfInstance4.setProperty("age", 30);        
        rdfInstance4.setProperty("name", "Name");

        String rdfXml4 = rdfInstance2.asXML();
        
        Assert.assertEquals(rdfXml3, rdfXml4);
        
	}
	
	@Test
	public void shouldGenerateXML() throws Exception{
		String id = IdGenerator.INSTANCE.newID();
		String rdfId = "uri:urn:"+id;
		String name = "juan";
		String pass = "123";
		
		RDFSchema schema1 = new RDFSchema("user", "http://mesh4x/user#", "user");
		schema1.addStringProperty("id", "id", "en");
		schema1.addStringProperty("pass", "password", "en");
		schema1.addStringProperty("name", "name", "en");
		
		RDFInstance rdfInstance1 = schema1.createNewInstance(rdfId);
		rdfInstance1.setProperty("name", name);
		rdfInstance1.setProperty("pass", pass);
		rdfInstance1.setProperty("id", id);
		
		String plainXML = XMLHelper.canonicalizeXML("<user><id>"+id+"</id><name>"+name+"</name><pass>"+pass+"</pass></user>");
		RDFInstance rdfInstance2 = schema1.createNewInstance(rdfId, plainXML, "id");
		Assert.assertEquals(rdfInstance1.asXML(), rdfInstance2.asXML());
	}
}
