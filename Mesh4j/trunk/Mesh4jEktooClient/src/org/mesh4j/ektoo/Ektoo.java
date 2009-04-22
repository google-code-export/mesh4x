package org.mesh4j.ektoo;

import javax.swing.JFrame;

import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooUI;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class Ektoo 
{
	public Ektoo()
	{
		EktooUIController controller = new EktooUIController(new PropertiesProvider());
		JFrame thisClass = new EktooUI(controller);
		thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisClass.setVisible(true);	
	}

	public static void main(String[] args) 
	{
		Ektoo main = new Ektoo(); 
	}
}
