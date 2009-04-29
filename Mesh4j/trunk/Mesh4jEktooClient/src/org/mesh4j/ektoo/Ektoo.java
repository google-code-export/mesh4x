package org.mesh4j.ektoo;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.translator.EktooMessageTranslator;

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
    initLookAndFeel();
    setUIFont( 
              new FontUIResource( 
                                   EktooMessageTranslator.translate("EKTOO_DEFAULT_UNICODE_FONT_NAME"), 
                                   Integer.parseInt(EktooMessageTranslator.translate("EKTOO_DEFAULT_UNICODE_FONT_STYLE")),
                                   Integer.parseInt(EktooMessageTranslator.translate("EKTOO_DEFAULT_UNICODE_FONT_SIZE"))
                                )
             );
    Ektoo main = new Ektoo(); 
	}
	
	// TODO (NBL) make it configurable from properties file
	private static void initLookAndFeel()
	{
    try
    { 
      //newly incorporated look & feel in J2SE 6.0
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      //native look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException e)
    { 
      e.printStackTrace();
    }
    catch (InstantiationException e)
    { 
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    { 
      e.printStackTrace();
    }
    catch (UnsupportedLookAndFeelException e)
    { 
      e.printStackTrace();
    }
  }	
	
	public static void setUIFont (javax.swing.plaf.FontUIResource f)
	{
    //
    // sets the default font for all Swing components.
    // ex. 
    //  setUIFont (new javax.swing.plaf.FontUIResource
    //   ("Serif",Font.ITALIC,12));
    //
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) 
    {
      Object key = keys.nextElement();
      Object value = UIManager.get (key);
      if (value instanceof javax.swing.plaf.FontUIResource)
        UIManager.put (key, f);
      }
    }    	
	
}
