package org.mesh4j.ektoo.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLDB
{
  private final static String driver = "com.mysql.jdbc.Driver";
  
  public static ArrayList<String> getTables(String user, String pass, String host, int port, String schema)
  {
    ArrayList<String> tables = new ArrayList();

    System.out.println("Listing all table name in Database!");
    Connection con = null;
    String url = "jdbc:mysql://" + host + ":" + port + "/";
    try
    {
      Class.forName(driver);
      con = DriverManager.getConnection(url+schema, user, pass);
      try
      {
        DatabaseMetaData dbm = con.getMetaData();
        String[] types = {"TABLE"};
        ResultSet rs = dbm.getTables(null,null,"%",types);
        while (rs.next())
        {
          String table = rs.getString("TABLE_NAME");
          if (table != null || table.trim().length() == 0)
            tables.add(table);
          
          System.out.println(table);
        }
        con.close();
      }
      catch (SQLException s){
        System.out.println("No any table in the database");
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
    
    return tables;
  }
}
