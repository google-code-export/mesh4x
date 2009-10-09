package org.mesh4j.sync.adapters.hibernate.derby;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DerbyHelper {

	/**
	 * Creates a new Derby DB
	 */
	public static void createDerbyDB() throws ClassNotFoundException,
			SQLException {
		// the connection will take care of the creation of the db.
		Connection derbyConnection = getDerbyConnection();
		derbyConnection.close();
	}

	/**
	 * Removes the database
	 */
	public static void deleteDerbyDB() {
		// the derby db is a directory with the name of the database.
		TestHelper.deleteDir(new File(getDerbyFile()));
	}

	
	/**
	 * @return the physical name of the derby db.
	 */
	public static String getDerbyFile() {
		return TestHelper.getThisPath() + File.separator + "test.ddb";
	}

	/**
	 * Creates a connection to the derby DB. If the db does not exists, it is
	 * created.
	 */
	public static Connection getDerbyConnection()
			throws ClassNotFoundException, SQLException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		Connection derbyDB = DriverManager.getConnection("jdbc:derby:"
				+ getDerbyFile() + ";create=true");
		return derbyDB;
	}
	

}
