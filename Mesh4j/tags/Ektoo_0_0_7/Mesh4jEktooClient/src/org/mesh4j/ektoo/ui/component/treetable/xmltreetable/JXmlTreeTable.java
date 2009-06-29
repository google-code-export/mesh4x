package org.mesh4j.ektoo.ui.component.treetable.xmltreetable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import org.mesh4j.ektoo.ui.component.treetable.JTreeTable;
import org.mesh4j.ektoo.ui.component.treetable.TreeTableModel;

 
public class JXmlTreeTable extends JTreeTable{ 
 
    public JXmlTreeTable(TreeTableModel  treeTableModel){ 
        super(treeTableModel); 
        setShowGrid(true); 
        setGridColor(new Color(234, 234, 234)); 
 
        setIntercellSpacing(new Dimension(1, 1)); 
        new TableColumnResizer(this); 
        getTree().setRootVisible(true); 
        getTree().setShowsRootHandles(true);
    } 
 
    //mouse press intended for resize shouldn't change row/col/cell celection
    @Override
    public void changeSelection(int row, int column, boolean toggle, boolean extend) { 
        if(getCursor()==TableColumnResizer.resizeCursor) 
            return; 
        super.changeSelection(row, column, toggle, extend); 
    } 

    
    //overriden to make the height of scroll match viewpost height if smaller
    @Override
    public boolean getScrollableTracksViewportHeight() { 
        return getPreferredSize().height < getParent().getHeight(); 
    } 

    
    public boolean editCellAt(int row, int column, EventObject e){ 
        if(e instanceof MouseEvent){ 
            MouseEvent me = (MouseEvent)e; 
            // If the modifiers are not 0 (or the left mouse button), 
            // tree may try and toggle the selection, and table 
            // will then try and toggle, resulting in the 
            // selection remaining the same. To avoid this, we 
            // only dispatch when the modifiers are 0 (or the left mouse 
            // button). 
            if(me.getModifiers()==0 || 
                    me.getModifiers()==InputEvent.BUTTON1_MASK){ 
                for(int counter = getColumnCount()-1; counter>= 0; 
                    counter--){ 
                    if(getColumnClass(counter)==TreeTableModel.class){ 
                        MouseEvent newME = new MouseEvent 
                                (tree, me.getID(), 
                                        me.getWhen(), me.getModifiers(), 
                                        me.getX()-getCellRect(0, counter, true).x, 
                                        me.getY(), me.getClickCount(), 
                                        me.isPopupTrigger()); 
                        tree.dispatchEvent(newME); 
                        break; 
                    } 
                } 
            } 
            return false; 
        } 
        return super.editCellAt(row, column, e); 
    } 
}

