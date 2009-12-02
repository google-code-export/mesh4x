package org.mesh4j.meshes.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class AbstractView extends JFrame {
	
    public abstract void modelPropertyChange(PropertyChangeEvent evt);
    
}
