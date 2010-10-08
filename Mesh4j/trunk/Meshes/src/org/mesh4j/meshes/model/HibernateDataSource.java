package org.mesh4j.meshes.model;

import java.sql.Driver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.dialect.Dialect;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
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
	
	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDialectClass() {
		return dialectClass;
	}

	public void setDialectClass(String dialectClass) {
		this.dialectClass = dialectClass;
	}
	
	@Override
	public void accept(MeshVisitor visitor) {
		visitor.visit(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISyncAdapter createSyncAdapter(ISchema schema, String baseDirectory, FeedRef feedRef) {
		try {
			IRDFSchema rdfSchema = null;
			if (schema instanceof IRDFSchema)
				rdfSchema = (IRDFSchema) schema;
			return HibernateSyncAdapterFactory.createHibernateAdapter(connectionURL, user, password, (Class<Driver>)Class.forName(driverClass),
					(Class<Dialect>)Class.forName(dialectClass), feedRef.getLocalName(), rdfSchema, getRdfSchemaBaseUri(), baseDirectory, new LoggedInIdentityProvider(), null);
		} catch (ClassNotFoundException e) {
			throw new MeshException(e);
		}
	}

	@Override
	public String toString() {
		return String.format("Database: %s", connectionURL);
	}
}
