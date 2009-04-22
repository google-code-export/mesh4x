package org.mesh4j.ektoo.model;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class CloudModel extends AbstractModel
{
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
}
