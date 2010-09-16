package org.mesh4j.meshes.ui.resource;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourceManager {
	
	private ResourceManager() {}
	
	public static Image getImage(String path) {
		URL url = ResourceManager.class.getResource(path);
		return Toolkit.getDefaultToolkit().getImage(url);
	}
	
	public static Icon getIcon(String path) {
		URL url = ResourceManager.class.getResource(path);
		return new ImageIcon(url);
	}
	
	public static Image getLogo() {
		return getImage("instedd_small.png");
	}
	
	public static Image getTableImage() {
		return getImage("gs_plain.png");
	}
	
	public static Image getMapImage() {
		return getImage("kml_plain.png");
	}
	
	public static Image getFolderImage() {
		return getImage("zip_plain.png");
	}

}
