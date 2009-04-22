package org.mesh4j.ektoo.ui;

import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public abstract class AbstractUI extends JPanel
{
	public abstract void modelPropertyChange(PropertyChangeEvent evt);
}
