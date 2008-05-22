package com.mesh4j.sync.security;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mesh4j.sync.validations.MeshException;

public class LoggedInIdentityProvider implements IIdentityProvider {

	private static final Log Logger = LogFactory.getLog(LoggedInIdentityProvider.class);
	
	@Override
	public String getAuthenticatedUser() {
		String hostName = obtainsHostName();
		String userName = System.getProperty("user.name");
		return hostName + "_" + userName;
	}
	
	private String obtainsHostName(){
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (UnknownHostException e) {
			Logger.error("Error trying to obtains host name.", e);
			throw new MeshException(e);
		}
	}
}