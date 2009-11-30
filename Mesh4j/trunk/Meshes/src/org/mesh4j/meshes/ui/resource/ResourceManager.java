package org.mesh4j.meshes.ui.resource;

import java.awt.Image;
import java.awt.Toolkit;

public class ResourceManager {
	
	private ResourceManager() {}
	
	public static Image getLogo() {
		Image image = Toolkit.getDefaultToolkit().getImage("images/instedd_small.png");
		return image;
	}
	
	public static Image getTableImage() {
		Image image = Toolkit.getDefaultToolkit().getImage("images/gs_plain.png");
		return image;
	}
	
	public static Image getMapImage() {
		Image image = Toolkit.getDefaultToolkit().getImage("images/kml_plain.png");
		return image;
	}
	
	public static Image getFolderImage() {
		Image image = Toolkit.getDefaultToolkit().getImage("images/zip_plain.png");
		return image;
	}

}
