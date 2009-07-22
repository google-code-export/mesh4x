package org.mesh4j.sync.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.mesh4j.sync.validations.MeshException;

import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer20010315WithComments;

public class XMLHelper {

	private final static Log Logger = LogFactory.getLog(XMLHelper.class);

	public static String formatXML(Element element, OutputFormat format)
			throws IOException {

		element.normalize();

		StringWriter sw = new StringWriter();

		XMLWriter writer = new XMLWriter(sw, format);
		writer.write(element);

		sw.flush();
		String formatXML = sw.toString();
		return formatXML;
	}

	public static String formatXML(Document document, OutputFormat format)
			throws IOException {
		document.normalize();

		StringWriter sw = new StringWriter();

		XMLWriter writer = new XMLWriter(sw, format);
		writer.write(document);

		sw.flush();
		String formatXML = sw.toString();
		return formatXML;
	}

	public static void write(Document document, File file) {
		write(document, file, OutputFormat.createPrettyPrint());
	}

	public static void write(Document document, File file, OutputFormat format) {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
		}
	}

	public static void write(String xml, File file) {
		Document document;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
		write(document, file);
	}

	@SuppressWarnings("unchecked")
	public static List<Element> selectElements(String xpathExpression,
			Element root, Map<String, String> namespaces) {
		try {
			List<Element> elements = new ArrayList<Element>();
			Dom4jXPath xpath = new Dom4jXPath(xpathExpression);
			xpath.setNamespaceContext(new SimpleNamespaceContext(namespaces));

			elements = xpath.selectNodes(root);
			return elements;
		} catch (JaxenException e) {
			throw new MeshException(e);
		}
	}

	public static Element selectSingleNode(String xpathExpression,
			Element root, Map<String, String> namespaces) {
		try {
			Dom4jXPath xpath = new Dom4jXPath(xpathExpression);
			xpath.setNamespaceContext(new SimpleNamespaceContext(namespaces));
			return (Element) xpath.selectSingleNode(root);
		} catch (JaxenException e) {
			throw new MeshException(e);
		}
	}

	public static Document readDocument(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(file);
	}

	public static Document readDocument(File file, Charset charset)
			throws DocumentException {
		SAXReader reader = new SAXReader();
		reader.setEncoding(charset.name());
		return reader.read(file);
	}

	public static String canonicalizeXML(String xml) {
		try {
			Canonicalizer20010315WithComments c = new Canonicalizer20010315WithComments();
			byte[] result = c.engineCanonicalize(xml.getBytes("UTF-8"));
			return new String(result);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	final static Comparator<Element> ELEMENT_COMPARATOR = new Comparator<Element>(){
		@Override
		public int compare(Element e0, Element e1) {
			return e0.getName().compareTo(e1.getName());
		}
		
	};
	@SuppressWarnings("unchecked")
	private static Element sortElements(Element element) {
		Element copyElement = element.createCopy();
		TreeSet<Element> sortedElements = new TreeSet<Element>(ELEMENT_COMPARATOR);
		
		List<Element> elements = copyElement.elements();
		
		for (Element e : elements) {
			sortedElements.add(sortElements(e)); 
			copyElement.remove(e);
		}
		
		for (Element e : sortedElements) {
			copyElement.add(e);
		}
		return copyElement;
	}
	
	public static String canonicalizeXML(Element element) {
		try {
			Element sortedElement = sortElements(element);
			
			String xml = formatXML(sortedElement, OutputFormat.createCompactFormat());
			xml = xml.trim();
			xml = canonicalizeXML(xml);
			return xml.trim();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}


	public static String canonicalizeXML(Document document) {
		try {
			String xml = formatXML(document, OutputFormat.createCompactFormat());
			return canonicalizeXML(xml);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public static Element parseElement(String xml) {
		try {
			return DocumentHelper.parseText(xml).getRootElement();
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}

	public static String makeXml(String elementName, String elementValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("<");
		sb.append(elementName);
		sb.append(">");
		sb.append(elementValue);
		sb.append("</");
		sb.append(elementName);
		sb.append(">");
		return sb.toString();
	}

	public static int calculateHashCode(Element element){
//	    int h = hash;
//	    if (h == 0) {
//	    	int off = offset;
//	    	char val[] = value;
//	    	int len = count;
//	    	for (int i = 0; i < len; i++) {
//	    		h = 31*h + val[off++];
//	    	}
//	    	hash = h;
//	    } 
//	    return h;
//	    
//	    int hash = 0;
//	    for (int i = 0; i < length(); i++) {
//	      hash = 32 * hash + charAt(i);
//	    }
//	    return hash;

		String xml = canonicalizeXML(element);
	    int hash = 0;
	    for (int i = 0; i < xml.length(); i++) {
	      hash = (hash << 5) - hash + xml.charAt(i);
	    }
	    return hash;


	}
}