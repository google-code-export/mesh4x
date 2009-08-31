package org.mesh4j.sync.utils;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.validations.MeshException;

import com.mysql.jdbc.Driver;

/**
 * Please provide your password(if you have any) to run all the test
 * case where password is necessary.
 */
public class SqlDBUtilsTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfDriverClassNameIsNull(){
		SqlDBUtils.getTableNames(null, "jdbc:mysql:///mesh4xdb", "root", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfUrlIsNull(){
		SqlDBUtils.getTableNames(Driver.class, null, "root", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfUrlIsEmpty(){
		SqlDBUtils.getTableNames(Driver.class, "", "root", "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfUserIsNull(){
		SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///mesh4xdb", null, "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfUserIsEmpty(){
		SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///mesh4xdb", "", "");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetTableNamesFailsIfPasswrodIsNull(){
		SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///mesh4xdb", "root", null);
	}
	
	@Test
	public void shouldObtainTables(){
		Set<String> tableNames = SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///mesh4xdb", "root", "");
		Assert.assertNotNull(tableNames);
		Assert.assertTrue(tableNames.size() > 0);
	}
	
	@Test(expected = MeshException.class)
	public void shouldGenerateRunTimeExceptionIfDatabaseIsNotAvailable(){
		//fake database, need to improve , create fake database automatically and
		//then delete.
		SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///tes34324242", "root", "");
	}
	
//	@Test
//	public void shouldReturnTableSizeZeroIfAnyTableIsNotAvailable(){
//		Set<String> tableNames = SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql:///empty", "root", "test1234");
//		Assert.assertNotNull(tableNames);
//		Assert.assertEquals(tableNames.size(),0);
//	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrlFailsIfSchemaIsNull(){
		SqlDBUtils.getMySqlConnectionUrl(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrlFailsIfSchemaIsEmpty(){
		SqlDBUtils.getMySqlConnectionUrl("");
	}
	
	@Test
	public void shouldGetMySqlUrl(){
		String url = SqlDBUtils.getMySqlConnectionUrl("mesh4xdb");
		Assert.assertNotNull(url);
		Assert.assertEquals("jdbc:mysql:///mesh4xdb", url);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrlFailsIfHostIsNull(){
		SqlDBUtils.getMySqlConnectionUrl(null, 4343, "mesh4xdb");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrlFailsIfHostIsEmpty(){
		SqlDBUtils.getMySqlConnectionUrl("", 4343, "mesh4xdb");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrl2FailsIfSchemaIsNull(){
		SqlDBUtils.getMySqlConnectionUrl("localhost", 4343, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldGetMySqlUrl2FailsIfSchemaIsEmpty(){
		SqlDBUtils.getMySqlConnectionUrl("localhost", 4343, "");
	}
	
	@Test
	public void shouldGetMySqlUrl2(){
		String url = SqlDBUtils.getMySqlConnectionUrl("localhost", 4343, "mesh4xdb");
		Assert.assertNotNull(url);
		Assert.assertEquals("jdbc:mysql://localhost:4343/mesh4xdb", url);
	}
	
//	@Test
	public void shouldExecuteSqlScript(){
		String sqlFileName = this.getClass().getResource("mesh4j_table_mysql.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdb2", "root", "admin", sqlFileName);	
		Set<String> tableNames = SqlDBUtils.getTableNames(Driver.class, "jdbc:mysql://localhost/mesh4xdb2", "root", "admin");
		Assert.assertNotNull(tableNames);
		Assert.assertTrue(tableNames.contains("mesh_example"));
		Assert.assertTrue(tableNames.contains("mesh_example_1"));
	}
}
