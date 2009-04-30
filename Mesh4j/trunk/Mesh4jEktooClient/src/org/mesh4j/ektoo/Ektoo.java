package org.mesh4j.ektoo;

import java.awt.EventQueue;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.EktooUIController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.EktooUI;
import org.mesh4j.translator.EktooMessageTranslator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class Ektoo {
	
	private final static Log LOGGER = LogFactory.getLog(Ektoo.class);
	
	public static void main(String[] args) {
		initLookAndFeel();
		setUIFont(new FontUIResource(EktooMessageTranslator
				.translate("EKTOO_DEFAULT_UNICODE_FONT_NAME"), Integer
				.parseInt(EktooMessageTranslator
						.translate("EKTOO_DEFAULT_UNICODE_FONT_STYLE")),
				Integer.parseInt(EktooMessageTranslator
						.translate("EKTOO_DEFAULT_UNICODE_FONT_SIZE"))));
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Ektoo();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
	}

	public Ektoo() {
		EktooUIController controller = new EktooUIController(new PropertiesProvider());
		JFrame thisClass = new EktooUI(controller);
		thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thisClass.setVisible(true);
	}
	
	// TODO (NBL) make it configurable from properties file
	private static void initLookAndFeel() {
		try {
			// newly incorporated look & feel in J2SE 6.0
			// UIManager.setLookAndFeel(
			// "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			// native look and feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} 
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		//
		// sets the default font for all Swing components.
		// ex.
		// setUIFont (new javax.swing.plaf.FontUIResource
		// ("Serif",Font.ITALIC,12));
		//
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}

}
