package org.mesh4j.ektoo.ui.component.treetable.xmltreetable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class JXmlComparerTreeTableCellRenderer extends DefaultTreeCellRenderer{
   
	private static final long serialVersionUID = 1L;
	Color elementColor = new Color(0, 0, 128);
    Color attributeColor = new Color(0, 128, 0);
    private Color conflictColor = Color.RED;
    private Color newItemOrAttributeColor = Color.GREEN;
    
    
    TreeModel baseModel;
    private static String OWL_DatatypeProperty = "owl:DatatypeProperty";
	private static String OWL_Class = "owl:Class";
	private static String XML_NS = "xmlns";
	 
	
    public JXmlComparerTreeTableCellRenderer(TreeModel baseModel){
        setOpenIcon(null);
        setClosedIcon(null);
        setLeafIcon(null);
        this.baseModel = baseModel;
    }
 
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
    	
        Node node = (Node)value;
        StringBuffer buff = new StringBuffer();
        switch(node.getNodeType()){
            case Node.ELEMENT_NODE:
              buff.append(node.getNodeName());
                break;
            case Node.ATTRIBUTE_NODE:
                buff.append("@");
                buff.append(" ");
                buff.append(node.getNodeName());
                break;
            case Node.TEXT_NODE:
                buff.append("# text");
                break;
            case Node.COMMENT_NODE:
              buff.append("# comment");
                break;
            case Node.DOCUMENT_TYPE_NODE:
                buff.append("# doctype");
                break;
            default:
             buff.append(node.getNodeName());
        }
        super.getTreeCellRendererComponent(tree, buff, sel, true, leaf, row, hasFocus);
        
        	
            switch(node.getNodeType()){
                case Node.ELEMENT_NODE:
                	processNode(node);
                    break;
                case Node.ATTRIBUTE_NODE:
                	//setForeground(attributeColor);
                	if(!selected){
                		processAttribute(node);	
                	}
                    break;
            }
        return this;
    }
    
    
    private void processAttribute(Node nodeTobeProcessed){
    	String attributeName = nodeTobeProcessed.getNodeName();
    	if(attributeName.startsWith(XML_NS)){
    		
    	
//    	String attributeValue = nodeTobeProcessed.getNodeValue();
    	Node sourceAttributeNode = getAttribute(attributeName);
    	if(sourceAttributeNode == null){
    		//attribute not found, means this is new attribute
    		setForeground(newItemOrAttributeColor);
    	} else {
    		if(nodeTobeProcessed.isEqualNode(sourceAttributeNode)){
    			setForeground(attributeColor);
    		} else {
    			setForeground(conflictColor);
    		}
    	}
    } else {
    	setForeground(attributeColor);
    }
    }
    
    
    private void processNode(Node nodeTobeProcessed){ 
    	String nodeName = nodeTobeProcessed.getNodeName();
    	if(nodeName.equals(OWL_DatatypeProperty) || 
    			nodeName.equals(OWL_Class)){
    		String propName = nodeTobeProcessed.getAttributes().getNamedItem("rdf:about").getNodeValue();
    		Node sourceNode = getNode(propName,nodeName);
    		if(sourceNode == null){//no equvalent node found in source,
    			// means this is new attribute
    			setForeground(newItemOrAttributeColor);
    		} else {
    			if(nodeTobeProcessed.isEqualNode(sourceNode)){
    				setForeground(elementColor);
    			} else {//not equal, means conflict
    				setForeground(conflictColor);
    			}
    		}
    	} else {
    		setForeground(elementColor);
    	}
	}
    
    
    private Node getAttribute(String attributeName){
    	 NamedNodeMap nodeMap = ((Node)baseModel.getRoot()).getAttributes();
    	 if(nodeMap != null){
    		int size = nodeMap.getLength();
    		for(int i = 0 ; i<size; i++){
    			Node attributeNode = nodeMap.item(i);
    			if(attributeNode.getNodeName().equals(attributeName)){
    				return attributeNode;
    			}
    		}
    	 } 
    	 return null;
    }
    /**
     * 
     * @param value
     * @param nodeName
     * @return
     */
    private Node getNode(String value,String nodeName){
    	Node baseNodes = ((Node)baseModel.getRoot());
    	int size = baseNodes.getChildNodes().getLength();
		for(int i=0 ; i<size; i++){
			Node node = baseNodes.getChildNodes().item(i);
			if(node.getNodeType() == Node.ATTRIBUTE_NODE){
				//never invoked
			} else if(node.getNodeType() == Node.ELEMENT_NODE){
				String thisNodeName = node.getNodeName();
				if(thisNodeName.equals(nodeName)){
					String propName =  node.getAttributes().getNamedItem("rdf:about").getNodeValue();
					if(propName.equals(value)){
						return node;
					}
				} 
			}
		}
		return null;
    }
    
   
}



