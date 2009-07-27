package org.mesh4j.sync.midlet;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class Mesh4xDemoMidlet extends MIDlet implements CommandListener, Runnable {
	
	// MODEL VARIABLES
	private Display display;

	// BUSINESS METHODS
	public Mesh4xDemoMidlet() {
		
		this.display = Display.getDisplay(this);
		
	}

	public void startApp() {
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	public void commandAction(Command c, Displayable s) {
	}
	
	public void run() {
	}
}
