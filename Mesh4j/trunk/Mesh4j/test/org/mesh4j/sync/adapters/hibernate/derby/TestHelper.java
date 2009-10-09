package org.mesh4j.sync.adapters.hibernate.derby;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;

public class TestHelper {

	public static final Object[][] TEST_DATA = { 
		new Object[] { 1, "Hello" },
		new Object[] { 2, "World" }, };
	
	private static String thisPath;
	
	static {
		// use the bin dir where this class resides as the execution space
		thisPath = TestHelper.class.getResource(
				TestHelper.class.getSimpleName() + ".class").getPath();

		File f = new File(thisPath);
		thisPath = f.getParent();
	}

	/**
	 * Return the path where this file resides
	 */
	public static String getThisPath() {
		return thisPath;
	}
	
	/**
	 * Executes the creation script for the table over the given connection
	 */
	public static void createTestTable(Connection connection) throws SQLException {
		Statement stat = connection.createStatement();
		try {
			stat.executeUpdate("create table test (Id int primary key, Description varchar(255))");
		} finally {
			stat.close();
		}
	}

	/**
	 * Executes an sql and catch the exception. It is useful when needed to drop
	 * tables.
	 */
	public static void executeAndFailSilently(Statement stat, String sql) {
		try {
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes the scripts for dropping the tables
	 */
	public static void dropTestTables(Connection connection)
			throws ClassNotFoundException, SQLException {
		Statement stat = connection.createStatement();
		try {
			executeAndFailSilently(stat, "drop table test"); // we don't want to
																// fail if the
																// table does
																// not exists
			executeAndFailSilently(stat, "drop table test_sync");
		} finally {
			if (stat != null)
				stat.close();
		}
	}


	/**
	 * Recreates a directory. All the files contained in the directory are
	 * erased.
	 */
	public static void createNewDir(String path) throws IOException {
		File dir = new File(path);
		deleteDir(dir);
		dir.mkdir();
	}

	/** WARNING: its a cascade delete */
	public static void deleteDir(File dir) {
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory())
					deleteDir(files[i]);
				else
					files[i].delete();
			}
			dir.delete();
		}
	}

	public static void fillTestTableWithData(Connection connection) throws Exception {
		fillTestTableWithData(connection, TEST_DATA);
	}

	/**
	 * Insert data into the test table of the given connection.
	 */
	public static void fillTestTableWithData(Connection connection, Object[][] values)
			throws Exception {
		PreparedStatement statement = null;
		try {
			String sql = "INSERT INTO test VALUES (?, ?)";
			statement = connection.prepareStatement(sql);

			for (int i = 0; i < values.length; i++) {
				Object[] strings = values[i];

				statement.setInt(1, (Integer) strings[0]);
				statement.setString(2, (String) strings[1]);
				statement.execute();
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	
	/**
	 * Asserts the data in the test table matches the testData
	 */
	public static void assertDataWasInserted(Connection connection,
			Object[][] testData) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM test ORDER BY Id");
		
		for (Object[] row : testData) {
			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(row[0], resultSet.getInt("Id"));
			Assert.assertEquals(row[1], resultSet.getString("Description"));
		}
		Assert.assertFalse(resultSet.next());
	}


}
