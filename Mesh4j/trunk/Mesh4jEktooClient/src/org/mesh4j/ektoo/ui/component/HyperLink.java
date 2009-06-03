package org.mesh4j.ektoo.ui.component;

import java.awt.Cursor;

import javax.swing.JLabel;

public class HyperLink extends JLabel {

	private static final long serialVersionUID = 1941291683152038599L;

	public HyperLink(String linlText){
		
		super("<HTML><FONT size=3 color=\"#000099\"><U>" +
				linlText +
				"</U></FONT></HTML>");
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
	}

		
}
