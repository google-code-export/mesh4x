package org.mesh4j.ektoo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mesh4j.ektoo.controller.KmlUIController;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class KmlUI extends JPanel
{
	private JLabel labelUri = null;
	private JTextField txtUri= null;
	private Object controller;

	public KmlUI() {
		super();
		initialize();
	}

	public KmlUI(String uri)
	{
		super();
		initialize();
		labelUri.setText(uri);

	}

	private void initialize()
	{
		this.setController(new KmlUIController(new PropertiesProvider()));		
		this.setSize(300, 135);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(300, 95));
		this.setBackground(new Color(106, 237, 238));

		this.add(getUriLabel(), null);
		this.add(getUriText(), null);
	}
	private JLabel getUriLabel() 
	{
		if (labelUri == null) 
		{
			labelUri = new JLabel();
			labelUri.setText(EktooUITranslator.getKmlUriLabel());
			labelUri.setSize(new Dimension(85, 16));
			labelUri.setPreferredSize(new Dimension(85, 16));
			labelUri.setLocation(new Point(8, 9));
		}
		return labelUri;
	}

	private JTextField getUriText() {
		if (txtUri == null) {
			txtUri = new JTextField();
			txtUri.setBounds(new Rectangle(101, 5, 183, 20));
		}
		return txtUri;
	}
	
	public String getUri()
	{
		return getUriText().getText();
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Object getController() {
		return controller;
	}
}  //  @jve:decl-index=0:visual-constraint="-4,-28"
