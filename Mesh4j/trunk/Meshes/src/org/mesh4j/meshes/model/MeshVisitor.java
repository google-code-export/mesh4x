package org.mesh4j.meshes.model;

public abstract class MeshVisitor {
	
	public boolean visit(Mesh mesh) {
		return true;
	}
	
	public boolean visit(DataSet dataSet) {
		return true;
	}
	
	public boolean visit(FeedDataSource dataSource) {
		return true;
	}
	
	public boolean visit(GSSheetDataSource dataSource) {
		return true;
	}
	
	public boolean visit(KmlDataSource dataSource) {
		return true;
	}
	
	public boolean visit(MsAccessDataSource dataSource) {
		return true;
	}
	
	public boolean visit(MsExcelDataSource dataSource) {
		return true;
	}
	
	public boolean visit(EpiInfoDataSource dataSource) {
		return true;
	}
	
	public boolean visit(HibernateDataSource dataSource) {
		return true;
	}

}
