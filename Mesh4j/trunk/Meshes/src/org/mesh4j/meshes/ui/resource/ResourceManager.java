package org.mesh4j.meshes.ui.resource;

import java.awt.Image;
import java.awt.Toolkit;

public class ResourceManager {
	
	private ResourceManager() {}
	
	public static Image getLogo() {
		Image image = Toolkit.getDefaultToolkit().getImage("images/instedd_small.png");
		return image;
	}

}
