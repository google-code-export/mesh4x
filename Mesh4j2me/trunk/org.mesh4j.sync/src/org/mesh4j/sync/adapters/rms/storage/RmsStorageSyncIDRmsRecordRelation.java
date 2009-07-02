package org.mesh4j.sync.adapters.rms.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;

import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class RmsStorageSyncIDRmsRecordRelation {

	// MODEL VARIABLES
	private String storageName;
	
	// BUSINESS METHODS
	public RmsStorageSyncIDRmsRecordRelation(String storageName) {
		Guard.argumentNotNullOrEmptyString(storageName, "storageName");

		this.storageName = storageName;
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
	
	public int getInstanceID(String syncId) {
		RecordStore store = this.getRecordStore();
		RecordEnumeration records = null;
		try {
			records = store.enumerateRecords(null, null, false);
			int size = records.numRecords();
			if (size == 0) {
				return -1;
			}
	
			byte[] data = null;
			String syncIDSaved = null;
			int recordId = -1;
			while (records.hasNextElement()) {
					recordId = records.nextRecordId();
					data = store.getRecord(recordId);
					syncIDSaved = getSyncIDFromBytes(data);
					if (syncIDSaved.equals(syncId)) {
						return getInstanceIDFromBytes(data);
					}
			}
		} catch (InvalidRecordIDException ie){
			// nothing to do: ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(records != null){
				records.destroy();
			}
			this.closeRecordStore(store);
		}
		return -1;
	}

	public String getSyncID(int recordId){
		RecordStore store = this.getRecordStore();
		RecordEnumeration records = null;
		try {
			records = store.enumerateRecords(null, null, false);
			int size = records.numRecords();
			if (size == 0) {
				return null;
			}
	
			byte[] data = null;
			int recordIdSaved = -1;
			int localRecordId = -1;
			while (records.hasNextElement()) {
				localRecordId = records.nextRecordId();
				data = store.getRecord(localRecordId);
				recordIdSaved = getInstanceIDFromBytes(data);
				if (recordIdSaved == recordId) {
					return getSyncIDFromBytes(data);
				}
			}
		} catch (InvalidRecordIDException ie){
			// nothing to do: ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(records != null){
				records.destroy();
			}
			this.closeRecordStore(store);
		}
		return null;
	}

	private String getSyncIDFromBytes(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);

		dis.readInt(); // skip instance id
		String syncID = dis.readUTF();

		dis.close();
		bais.close();
		return syncID;
	}

	private int getInstanceIDFromBytes(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);

		int recordId = dis.readInt();

		dis.close();
		bais.close();
		return recordId;
	}

	public void addAssociation(String syncId, int recordId){
		byte[] data = this.associationAsBytes(syncId, recordId);
		RecordStore store = this.getRecordStore();
		try{
			store.addRecord(data, 0, data.length);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			this.closeRecordStore(store);
		}
	}

	private byte[] associationAsBytes(String syncId, int instanceId) {
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;

		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			dos.writeInt(instanceId);
			dos.writeUTF(syncId);
			dos.flush();
			baos.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void deleteAssociation(String syncId){
		int assocRecordId = this.getRecordID(syncId);

		if(assocRecordId != -1){
			RecordStore store = this.getRecordStore();
			try{			
				store.deleteRecord(assocRecordId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new MeshException(e.getMessage());
			} finally{
				this.closeRecordStore(store);
			}
		}
	}
	
	protected int getRecordID(String syncId) {
		RecordStore store = this.getRecordStore();
		RecordEnumeration records = null;
		try {
			records = store.enumerateRecords(null, null, false);
			int size = records.numRecords();
			if (size == 0) {
				return -1;
			}
	
			byte[] data = null;
			String syncIDSaved = null;
			int recordId = -1;
			while (records.hasNextElement()) {
				recordId = records.nextRecordId();
				data = store.getRecord(recordId);
				syncIDSaved = getSyncIDFromBytes(data);
	
				if (syncIDSaved.equals(syncId)) {
					return recordId;
				}
			}
		}catch (InvalidRecordIDException ie){
			// nothing to do: ie.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(records != null){
				records.destroy();
			}
			this.closeRecordStore(store);
		}
		return -1;
	}
	
	private RecordStore getRecordStore() {
		try{
			return RecordStore.openRecordStore(this.storageName, true);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}
	
	public void deleteRecordStorage(){
		try{
			RecordStore rs = getRecordStore();
			this.closeRecordStore(rs);
			RecordStore.deleteRecordStore(this.storageName);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
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
}
