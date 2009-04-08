package org.mesh4j.sync.security;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class LoggedInIdentityProvider implements IIdentityProvider {

	private static final Log Logger = LogFactory.getLog(LoggedInIdentityProvider.class);
	
	// MODEL VARIABLES
	private String dataSource;
	
	// BUSINESS METHODS
	public LoggedInIdentityProvider(){
		super();
	}

	public LoggedInIdentityProvider(String dataSource){
		Guard.argumentNotNullOrEmptyString(dataSource, "dataSource");
		this.dataSource = dataSource;
	}
	
	@Override
	public String getAuthenticatedUser() {
		String hostName = getHostName();
		String userName = getUserName();
		
		if(this.dataSource == null){
			return hostName + "_" + userName;
		} else {
			return hostName + "_" + userName  + "_" + dataSource;
		}
	}
	
	public static String getHostName(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			Logger.error("Error trying to obtains host name.", e);
			throw new MeshException(e);
		}
	}
	
	public static String getUserName(){
		return System.getProperty("user.name");
	}
}