package org.mesh4j.sync.adapters.rms.storage;

import java.util.Vector;

import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;

public interface IRmsStorage {

	public void delete(RecordFilter filter);
	
	public Object get(RecordFilter filter);
	
	public void saveOrUpdate(Object object, RecordFilter filterById);

	public String getStorageName();

	public Vector getAll(RecordFilter filter, RecordComparator comparator);

	public void deleteRecordStorage();

	public void deleteAll();

	public void deleteRecord(int recordID);
	public Object get(int recordID);
	public int getNumberOfRecords();
	public int save(Object object);
	public void update(int recordID, Object object);
}
