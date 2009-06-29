package org.mesh4j.ektoo.ui.component.treetable.xmltreetable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Node;


public class JXmlTreeTableCellRenderer extends DefaultTreeCellRenderer{
    
	Color elementColor = new Color(0, 0, 128);
    Color attributeColor = new Color(0, 128, 0);
 
    public JXmlTreeTableCellRenderer(){
        setOpenIcon(null);
        setClosedIcon(null);
        setLeafIcon(null);
        
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
        super.getTreeCellRendererComponent(tree, buff, sel, expanded, leaf, row, hasFocus);
        if(!selected){
            switch(node.getNodeType()){
                case Node.ELEMENT_NODE:
                    setForeground(elementColor);
                    break;
                case Node.ATTRIBUTE_NODE:
                    setForeground(attributeColor);
                    break;
            }
        }
        return this;
    }
}



