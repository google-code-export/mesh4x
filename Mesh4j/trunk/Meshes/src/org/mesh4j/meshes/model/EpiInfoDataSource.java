package org.mesh4j.meshes.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.epiinfo.EpiInfoSyncAdapterFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

@XmlRootElement(name = "epiInfoDataSource")
@XmlAccessorType(XmlAccessType.NONE)
public class EpiInfoDataSource extends MsAccessDataSource {
	
	@Override
	public ISyncAdapter createSyncAdapter(DataSet dataSet, String baseDirectory) {
		return EpiInfoSyncAdapterFactory.createSyncAdapter(getFileName(), getTableName(), dataSet.getAbsoluteServerFeedUrl(), baseDirectory, new LoggedInIdentityProvider());
	}
	
	@Override
	public String toString() {
		return getFileName() + ":" + getTableName();
	}
	
	@Override
	public void accept(MeshVisitor visitor) {
		visitor.visit(this);
	}

}
