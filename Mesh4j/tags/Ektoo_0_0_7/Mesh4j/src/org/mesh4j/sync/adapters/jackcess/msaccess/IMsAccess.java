package org.mesh4j.sync.adapters.jackcess.msaccess;

import com.healthmarketscience.jackcess.Table;

public interface IMsAccess {

	public String getFileName();

	public Table getTable(String tableName);

	public void open() throws Exception;

	public void close() throws Exception;


}
