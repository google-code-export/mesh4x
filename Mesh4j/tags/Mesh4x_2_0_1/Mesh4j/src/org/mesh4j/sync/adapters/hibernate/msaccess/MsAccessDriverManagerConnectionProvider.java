package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.util.PropertiesHelper;
import org.hibernate.util.ReflectHelper;

public class MsAccessDriverManagerConnectionProvider implements ConnectionProvider {

	private static final Log LOGGER = LogFactory.getLog(MsAccessDriverManagerConnectionProvider.class);
	
	// MODEL VARIABLES
	private String url;
	private Properties connectionProps;
	private Integer isolation;
	private Connection connection;
	private boolean autocommit;

	// BUSINESS METHODS
	
	@Override
	public void configure(Properties props) throws HibernateException {

		String driverClass = props.getProperty(Environment.DRIVER);

		this.autocommit = PropertiesHelper.getBoolean(Environment.AUTOCOMMIT, props);
		this.isolation = PropertiesHelper.getInteger(Environment.ISOLATION, props);
		if (driverClass==null) {
			LOGGER.warn("no JDBC Driver class was specified by property " + Environment.DRIVER);
		} else {
			try {
				Class.forName(driverClass);
			} catch (ClassNotFoundException cnfe) {
				try {
					ReflectHelper.classForName(driverClass);
				} catch (ClassNotFoundException e) {
					String msg = "JDBC Driver class not found: " + driverClass;
					LOGGER.fatal(msg, e);
					throw new HibernateException(msg, e);
				}
			}
		}

		this.url = props.getProperty(Environment.URL);
		if (this.url==null) {
			String msg = "JDBC URL was not specified by property " + Environment.URL;
			LOGGER.fatal(msg);
			throw new HibernateException(msg);
		}

		this.connectionProps = ConnectionProviderFactory.getConnectionProperties(props);
	}

	@Override
	public Connection getConnection() throws SQLException {

		if(this.connection == null || this.connection.isClosed()){
			Connection conn = DriverManager.getConnection(this.url, this.connectionProps);
			if (this.isolation!=null) {
				conn.setTransactionIsolation(this.isolation);
			}
			
			if ( conn.getAutoCommit()!= this.autocommit ) {
				conn.setAutoCommit(this.autocommit);
			}
	
			this.connection = conn;
		}
		return this.connection;
	}

	@Override
	public void closeConnection(Connection conn) throws SQLException {
		if(!conn.isClosed()){
			conn.clearWarnings();
			if(!conn.getAutoCommit()){
				conn.commit();
			}
			conn.close();
			
			if(conn == this.connection){
				this.connection = null;
			}
		}
	}

	protected void finalize() {
		close();
	}
	
	@Override
	public void close() {
		try {
			if(this.connection != null){
				this.connection.close();
			}
		}catch (SQLException sqle) {
			LOGGER.warn("problem closing connections", sqle);
		}
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}
}
