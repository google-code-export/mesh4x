package org.mesh4j.meshes.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.Action;

import org.mesh4j.meshes.action.ExitAction;

public class MeshesTray {
	
	private final TrayIcon trayIcon;
	private final SystemTray systemTray;
	
	public MeshesTray(Action action) throws AWTException {
		systemTray = SystemTray.getSystemTray();
		
		PopupMenu popup = new PopupMenu();
		
		MenuItem defaultItem = new MenuItem("Exit");
		defaultItem.addActionListener(new ExitAction());
		popup.add(defaultItem);
		
		Image image = Toolkit.getDefaultToolkit().getImage("images/instedd_small.png");
		trayIcon = new TrayIcon(image, "Meshes", popup);
		
		trayIcon.addActionListener(action);
		trayIcon.setImageAutoSize(true);
		
		systemTray.add(trayIcon);
	}

}
