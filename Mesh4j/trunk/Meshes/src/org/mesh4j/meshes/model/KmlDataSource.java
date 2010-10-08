package org.mesh4j.meshes.model;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.ISchema;

public class KmlDataSource extends DataSource {
	
	public static final String FILE_NAME_PROPERTY = "kml_filename";
	
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		String oldFileName = this.fileName;
		this.fileName = fileName;
		firePropertyChange(FILE_NAME_PROPERTY, oldFileName, fileName);
	}
	
	@Override
	public ISyncAdapter createSyncAdapter(ISchema schema, String baseDirectory, FeedRef feedRef) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void accept(MeshVisitor visitor) {
		visitor.visit(this);
	}

}
