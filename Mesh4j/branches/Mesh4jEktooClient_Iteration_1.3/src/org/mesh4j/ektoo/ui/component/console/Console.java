package org.mesh4j.ektoo.ui.component.console;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Console extends JTextPane implements IConsole {
	
	private static final long serialVersionUID = -6075253103119892815L;

	public static final int ERROR = 1;
	public static final int WARNING = 2;

	// MODEL VARIABLES
	private JFrame parent = null;
	
	// BUSINESS METHODS
	
	public Console(JFrame parent) {
		this.parent = parent;
	}

	public void setMessage(String message, int messageType) {
		if (messageType == Console.ERROR) {
			setError(message);
		} else if (messageType == Console.WARNING) {
			setWarning(message);
		} else {
			setMessage(message);
		}
	}

	private void setMessage(String message) {
		setMessage(message, Color.BLACK);
	}

	private void setWarning(String message) {
		setMessage(message, Color.YELLOW);
	}

	private void setError(String message) {
		setMessage(message, Color.RED);
	}

	private void setMessage(String message, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, color);

		int len = getDocument().getLength();
		setCaretPosition(len);
		setCharacterAttributes(aset, false);
		replaceSelection(message);

	}
	
	protected JFrame getLocalParent(){
		return this.parent;
	}
}
