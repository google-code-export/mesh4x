package org.mesh4j.ektoo.model;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class CloudModel extends AbstractModel
{
  private final static String baseUri = "http://localhost:8080/mesh4x/feeds";  
	private String meshName = null;
	private String datasetName = null;
	
	public CloudModel()
	{
	}
	
	public void setMeshName(String mesh) 
	{
		firePropertyChange("meshName", this.meshName, this.meshName = mesh);
	}

	public String getMeshName() 
	{
		return meshName;
	}
	
	public void setDatasetName(String dataset) 
	{
		firePropertyChange("datasetName", this.datasetName, this.datasetName = dataset);
	}
	
	public String getDatasetName() 
	{
		return datasetName;
	}
	
	public String getUri()
	{
	  return baseUri + "/" + (getMeshName() != null? getMeshName() : "" ) + "/" + (getDatasetName()!= null ? getDatasetName() : "");
	}
}
