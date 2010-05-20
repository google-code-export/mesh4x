package org.mesh4j.meshes.ui.resource;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class ResourceManager {
	
	private ResourceManager() {}
	
	private static Image getImage(String path) {
		URL url = ResourceManager.class.getResource(path);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		return image;
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
