package org.mesh4j.ektoo.model;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class KmlModel extends AbstractModel 
{
	private String kmlUri = null;

	public void setKmlUri(String kmlUri) 
	{
		firePropertyChange("kmlUri", this.kmlUri, this.kmlUri = kmlUri); 
	}

	public String getKmlUri() 
	{
		return kmlUri;
	}
}
