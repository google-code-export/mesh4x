package org.mesh4j.sync.adapters.rms.storage;

import java.util.Vector;

import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;

import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class RmsStorage implements IRmsStorage {

	// MODEL VARIABLES
	private String storageName;
	private IObjectParser parser;
	
	// BUSINESS METHODS
	public RmsStorage(IObjectParser parser, String storageName) {
		Guard.argumentNotNullOrEmptyStringMaxSize(storageName, "storageName", 32);
		Guard.argumentNotNull(parser, "parser");
		
		this.storageName = storageName;
		this.parser = parser;
		this.initialize();
	}
	
	private void initialize() {
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(this.storageName, true);
		} catch(Exception e) {
			throw new MeshException(e.getMessage());
		} finally{
			if(rs != null){
				try{
					rs.closeRecordStore();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getStorageName() {
		return this.storageName;
	}

	public Object get(RecordFilter filter) {
		Object result = null;
		RecordStore rs = getRecordStore();
		RecordEnumeration records = null;
		try {
			records = rs.enumerateRecords(filter, null, false);
			if(records.hasNextElement()){
				byte[] data = records.nextRecord();
				result = this.parser.bytesToObject(data);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			if(records != null){
				records.destroy();
			}
			closeRecordStore(rs);
		}
		return result;
	}

	public void saveOrUpdate(Object object, RecordFilter filterById) {
		RecordStore rs = this.getRecordStore();
		RecordEnumeration records = null;
		try {
			records = rs.enumerateRecords(filterById, null, false);
			byte[] data = this.parser.objectToBytes(object);
			if(records.hasNextElement()){
				int recordId = records.nextRecordId();
				rs.setRecord(recordId, data, 0, data.length);
			} else {
				rs.addRecord(data, 0, data.length);		
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			if(records != null){
				records.destroy();
			}
			closeRecordStore(rs);
		}
	}

	public Vector getAll(RecordFilter filter, RecordComparator comparator) {		
		Vector<Object> result = new Vector<Object>();
		RecordStore rs = getRecordStore();
		RecordEnumeration records = null;
		try {
			records = rs.enumerateRecords(filter, comparator, false);
			byte[] data;
			while(records.hasNextElement()){
				data = records.nextRecord();
				result.addElement(this.parser.bytesToObject(data));
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			if(records != null){
				records.destroy();
			}
			closeRecordStore(rs);
		}
		return result;
	}

	public void delete(RecordFilter filter) {
		RecordStore rs = getRecordStore();
		RecordEnumeration records = null;
		try {
			records = rs.enumerateRecords(filter, null, false);
			int recordId;
			while(records.hasNextElement()){
				recordId = records.nextRecordId();
				rs.deleteRecord(recordId);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			if(records != null){
				records.destroy();
			}
			closeRecordStore(rs);
		}
	}	
	
	private void closeRecordStore(RecordStore rs) {
		if(rs != null){
			try {
				rs.closeRecordStore();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private RecordStore getRecordStore() {
		try{
			return RecordStore.openRecordStore(this.storageName, true);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public void deleteRecordStorage() {
		try{
//        	RecordStore recordStore = RecordStore.openRecordStore(this.storageName, true);
//       	recordStore.closeRecordStore();
			RecordStore.deleteRecordStore(this.storageName);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public void deleteAll() {
		RecordStore rs = getRecordStore();
		RecordEnumeration records = null;
		try {
			records = rs.enumerateRecords(null, null, false);
			int recordId;
			while(records.hasNextElement()){
				recordId = records.nextRecordId();
				rs.deleteRecord(recordId);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			if(records != null){
				records.destroy();
			}
			closeRecordStore(rs);
		}
	}


	// RecordID is given
	public void deleteRecord(int recordId) {
		RecordStore rs = getRecordStore();
		try {
			rs.deleteRecord(recordId);
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			closeRecordStore(rs);
		}
	}

	public Object get(int recordID) {
		Object result = null;
		RecordStore rs = getRecordStore();
		try {
			byte[] data = rs.getRecord(recordID);
			result = this.parser.bytesToObject(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			closeRecordStore(rs);
		}
		return result;
	}

	public int getNumberOfRecords() {
		RecordStore rs = getRecordStore();
		try{
			return rs.getNumRecords();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			closeRecordStore(rs);
		}
	}

	public int save(Object object) {
		RecordStore rs = this.getRecordStore();
		try {
			byte[] data = this.parser.objectToBytes(object);
			return rs.addRecord(data, 0, data.length);		
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			closeRecordStore(rs);
		}
	}

	public void update(int recordId, Object object) {
		RecordStore rs = this.getRecordStore();
		try {
			byte[] data = this.parser.objectToBytes(object);
			rs.setRecord(recordId, data, 0, data.length);
		} catch(Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}finally{
			closeRecordStore(rs);
		}
	}	
}
