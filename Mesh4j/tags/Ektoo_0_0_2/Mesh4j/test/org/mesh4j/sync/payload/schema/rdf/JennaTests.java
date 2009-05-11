package org.mesh4j.sync.payload.schema.rdf;

import java.io.File;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

public class JennaTests {

	//@Test
	public void shouldUseJenna(){
		
		// some definitions
        String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;

        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // create the resource

        //   and add the properties cascading style
        model.createResource(personURI)
             .addProperty(VCARD.FN, fullName)
             .addProperty(VCARD.N, 
                      model.createResource()
                           .addProperty(VCARD.Given, givenName)
                           .addProperty(VCARD.Family, familyName));
        
        StmtIterator iter = model.listStatements();
        // print out the predicate, subject and object of each statement

        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();         // get next statement
            Resource  subject   = stmt.getSubject();   // get the subject
            Property  predicate = stmt.getPredicate(); // get the predicate
            RDFNode   object    = stmt.getObject();    // get the object
 
            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");

            model.write(System.out);       
            model.write(System.out, "RDF/XML-ABBREV");
            model.write(System.out, "N-TRIPLE");
        }
	}
	
	@Test
	public void shouldReadInstance(){
            
			String inputFileName = this.getClass().getResource("mesh4x_instance.rdf").getFile();
            
            Model model = ModelFactory.createDefaultModel();

            InputStream in = FileManager.get().open( inputFileName );
            if (in == null) {
                throw new IllegalArgumentException( "File: " + inputFileName + " not found");
            }

            // read the RDF/XML file
            model.read(in, "");

            // write it to standard out
            model.write(System.out);       
            model.write(System.out, "RDF/XML-ABBREV");
            model.write(System.out, "N-TRIPLE");
            model.write(System.out, "N3");
            model.write(System.out, "TURTLE");
	}
	
	@Test
	public void shouldReadSchema(){
            
			String inputFileName = this.getClass().getResource("mesh4x_schema.rdf").getFile();
            
            Model model = ModelFactory.createDefaultModel();

            InputStream in = FileManager.get().open( inputFileName );
            if (in == null) {
                throw new IllegalArgumentException( "File: " + inputFileName + " not found");
            }

            // read the RDF/XML file
            model.read(in, "");

            // write it to standard out
            model.write(System.out);       
            model.write(System.out, "RDF/XML-ABBREV");
            model.write(System.out, "N-TRIPLE");
            model.write(System.out, "N3");
            model.write(System.out, "TURTLE");
	}
	
	@Test
	public void shouldParseXML() throws Exception{
		String inputFileName = this.getClass().getResource("mesh4x_instance.rdf").getFile();
		
		Document document = XMLHelper.readDocument(new File(inputFileName));
		
		Element root = document.getRootElement();
		
		String xml = root.asXML();
		System.out.println(xml);
		System.out.println(" ");
		
		String canonicalXml = XMLHelper.canonicalizeXML(root);
		System.out.println(canonicalXml);
		System.out.println(" ");
		
		Element rdfElement = XMLHelper.parseElement(canonicalXml);
		System.out.println(rdfElement.asXML());
		System.out.println(" ");
	}
	
	@Test
	public void shouldReadOntology(){
            
			String inputFileName = this.getClass().getResource("mesh4x_ontology.owl").getFile();
            
            Model model = ModelFactory.createOntologyModel();

            InputStream in = FileManager.get().open( inputFileName );
            if (in == null) {
                throw new IllegalArgumentException( "File: " + inputFileName + " not found");
            }

            // read the RDF/XML file
            model.read(in, "");

            // write it to standard out
            model.write(System.out, "");
	}
	
	@Test
	public void shouldReadOntologyIndividual(){
            
			String inputFileName = this.getClass().getResource("mesh4x_ontology_individual.owl").getFile();
			String schemaFileName = this.getClass().getResource("mesh4x_ontology.owl").getFile();
			
			OntModel model = ModelFactory.createOntologyModel();
            OntDocumentManager dm = model.getDocumentManager();
            dm.addAltEntry( "http://mesh4x/oswego",
                            "file:" + schemaFileName    );
            
            InputStream in = FileManager.get().open( inputFileName );
            if (in == null) {
                throw new IllegalArgumentException( "File: " + inputFileName + " not found");
            }

            // read the RDF/XML file
            model.read(in, "");

            // write it to standard out
            model.write(System.out, "");
	}
}
