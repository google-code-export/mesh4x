package org.mesh4j.sync.adapters.hibernate.msaccess;

public class HibernateWithMsAccessContentAdapterReplicationIDTests {

	// TODO (JMT) MsAccess ReplicationID (GUID):  waiting for response of HXTT driver
	
//	//@Test
//	public void testReplicationID()throws Exception{
//		String filename = TestHelper.baseDirectoryRootForTest() + "ms-access/multikey.mdb";
//		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
//		database+= filename.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 
// 
//		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
//		Assert.assertNotNull(driver);
//		
//		java.util.Properties prop = new java.util.Properties();
//	     //prop.put("charSet", "UTF-16");
//	       prop.put("user", "");
//	       prop.put("password", "");
//
//		Connection conn = DriverManager.getConnection(database, prop);
//		
//		Statement command = conn.createStatement();
//		ResultSet rs = command.executeQuery("select settlement0_.id as id2_, settlement0_.Field1 as Field2_2_, settlement0_.Field2 as Field3_2_, settlement0_.Field3 as Field4_2_, settlement0_.Field4 as Field5_2_, settlement0_.Field5 as Field6_2_, settlement0_.Field6 as Field7_2_, settlement0_.Field7 as Field8_2_, settlement0_.Field8 as Field9_2_, settlement0_.Field9 as Field10_2_, settlement0_.Field10 as Field11_2_, settlement0_.Field11 as Field12_2_, settlement0_.Field12 as Field13_2_ from Settlement_2_Query settlement0_");
//		while (rs.next())
//		{
//			System.out.println("2802E845-7DB0-412D-B318-820116F801EF");
//			byte[] bytes0 = "2802E8457DB0412DB318820116F801EF".getBytes();
//			printBytes(bytes0);
//			
//			byte[] bytesutf16 = "2802E845-7DB0-412D-B318-820116F801EF".getBytes(Charset.forName("UTF-16"));
//			printBytes(bytesutf16);
//
//			byte[] bytes1 = rs.getBytes("id2_");
//			printBytes(bytes1);
//			
//			InputStream is = rs.getBinaryStream("id2_");
//			byte[] bytes = readInputStream(is, 32);
//			printBytes(bytes);
//		}
//		
//	}
//
//	private void printBytes(byte[] bytesutf16) {
//		
//		System.out.print("[");
//		for (byte b : bytesutf16) {
//			System.out.print(b);
//			System.out.print(" ");
//		}
//		System.out.println("]");
//		
//	}
//	
//	public byte[] readInputStream(InputStream inputStream, int maxCycles) throws Exception{
//        byte[]    buffer = new byte[maxCycles];
//        int       bytesRead = 0;
//        int    cycle = 0;
//            
//        // read first byte
//
//        bytesRead = inputStream.read(buffer, cycle++, 1);
//        while (bytesRead != -1 && cycle < maxCycles) {
//                // read next byte.  give offset of previously read bytes.
//        	bytesRead = inputStream.read(buffer, cycle++, 1);
//        }
//        return buffer;
//	}
}
