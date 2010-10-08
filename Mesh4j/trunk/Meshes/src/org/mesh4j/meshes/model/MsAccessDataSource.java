package org.mesh4j.meshes.model;

import javax.xml.bind.annotation.XmlElement;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.ISchema;

public class MsAccessDataSource extends DataSource {
	
	public static final String FILE_NAME_PROPERTY = "access_filename";
	public static final String TABLE_NAME_PROPERTY = "access_tablename";
	
	@XmlElement
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
	
	@Override
	public DataSource copy() {
		MsAccessDataSource copy = (MsAccessDataSource) super.copy();
		copy.fileName = fileName;
		return copy;
	}
}
