package org.mesh4j.ektoo.ui.component.treetable.xmltreetable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class TreeElementProvider{ 
	
	 public static Document createDocument(InputSource is) throws Exception{ 
	        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
	        saxFactory.setNamespaceAware(true);
	        SAXParser parser = saxFactory.newSAXParser(); 
	        XMLReader reader = new XMLTrimFilter(parser.getXMLReader()); 
	 
	        TransformerFactory factory = TransformerFactory.newInstance();
	        
	        Transformer transformer = factory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "no"); 
	        DOMResult result = new DOMResult(); 
	        transformer.transform(new SAXSource(reader, is), result); 
	        

	        return (Document)result.getNode(); 
	    } 

 
}  
