package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public abstract class AbstractUI extends JPanel
{
	private static final long serialVersionUID = 7515686485315720840L;
	
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
}
