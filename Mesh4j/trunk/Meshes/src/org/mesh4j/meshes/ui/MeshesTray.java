package org.mesh4j.meshes.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MeshesTray {
	
	private final TrayIcon trayIcon;
	final SystemTray systemTray;
	
	public MeshesTray() throws AWTException {
		systemTray = SystemTray.getSystemTray();
		
		ActionListener exitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				systemTray.remove(trayIcon);
				System.exit(0);
			}
		};
		
		PopupMenu popup = new PopupMenu();
		
		MenuItem defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(exitListener);
		popup.add(defaultItem);
		
		Image image = Toolkit.getDefaultToolkit().getImage("images/instedd_small.png");
		trayIcon = new TrayIcon(image, "Meshes", popup);
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trayIcon.displayMessage("Action Event",
						"An Action Event Has Been Performed!",
						TrayIcon.MessageType.INFO);
			}
		};
		
		trayIcon.addActionListener(actionListener);
		trayIcon.setImageAutoSize(true);
		
		systemTray.add(trayIcon);
	}

}
