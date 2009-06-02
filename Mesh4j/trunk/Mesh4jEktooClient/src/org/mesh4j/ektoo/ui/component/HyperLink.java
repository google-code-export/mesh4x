package org.mesh4j.ektoo.ui.component;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

public class HyperLink extends JLabel {

	public HyperLink(String linlText){
		
		super("<HTML><FONT size=3 color=\"#000099\"><U>" +
				linlText +
				"</U></FONT></HTML>");
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
	}

		
}
