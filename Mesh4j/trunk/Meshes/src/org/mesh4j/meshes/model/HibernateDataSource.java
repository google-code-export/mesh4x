package org.mesh4j.meshes.model;

import java.sql.Driver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.dialect.Dialect;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

@XmlRootElement(name = "hibernateDataSource")
@XmlAccessorType(XmlAccessType.FIELD)
public class HibernateDataSource extends DataSource {

	private String connectionURL;
	private String user;
	private String password;
	private String driverClass;
	private String dialectClass;
	private String tableName;
	
	@Override
	public void accept(MeshVisitor visitor) {
		visitor.visit(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISyncAdapter createSyncAdapter(DataSet dataSet, String baseDirectory) {
		try {
			return HibernateSyncAdapterFactory.createHibernateAdapter(connectionURL, user, password, (Class<Driver>)Class.forName(driverClass),
					(Class<Dialect>)Class.forName(dialectClass), tableName, dataSet.getAbsoluteServerFeedUrl(), baseDirectory, new LoggedInIdentityProvider(), null);
		} catch (ClassNotFoundException e) {
			throw new MeshException(e);
		}
	}

}
