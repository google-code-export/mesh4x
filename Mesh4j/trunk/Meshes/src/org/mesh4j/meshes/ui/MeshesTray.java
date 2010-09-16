package org.mesh4j.meshes.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.Action;

import org.mesh4j.meshes.action.ExitAction;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class MeshesTray {
	
	private final TrayIcon trayIcon;
	private final SystemTray systemTray;
	
	public MeshesTray(Action showMainWindowAction) throws AWTException {
		systemTray = SystemTray.getSystemTray();
		
		PopupMenu popup = new PopupMenu();
		
		MenuItem showMainWindowItem = new MenuItem("Open");
		showMainWindowItem.addActionListener(showMainWindowAction);
		popup.add(showMainWindowItem);
		
		MenuItem showMeshWizardItem = new MenuItem("Create new Mesh...");
		showMeshWizardItem.addActionListener(new ShowMeshWizard());
		popup.add(showMeshWizardItem);
		
		popup.addSeparator();
		
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new ExitAction());
		popup.add(exitItem);
		
		Image image = ResourceManager.getLogo();
		
		trayIcon = new TrayIcon(image, "Meshes", popup);
		trayIcon.addActionListener(showMainWindowAction);
		trayIcon.setImageAutoSize(true);
		
		systemTray.add(trayIcon);
	}

}
