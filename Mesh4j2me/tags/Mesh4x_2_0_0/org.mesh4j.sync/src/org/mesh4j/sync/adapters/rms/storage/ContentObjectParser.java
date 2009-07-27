package org.mesh4j.sync.adapters.rms.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class ContentObjectParser implements IObjectParser{

	// MODEL VARIABLEs
	private String entityName;
	
	// BUSINESS METHODS
	public ContentObjectParser(String entityName) {
		Guard.argumentNotNullOrEmptyString(entityName, "entityName");
		this.entityName = entityName;
	}	
	
	public Object bytesToObject(byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			String id = dis.readUTF();
			dis.readLong();  // skip last update time
			String payload = dis.readUTF();
			
			return new EntityContent(payload, this.entityName, id);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(dis != null){
				try{
					dis.close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public byte[] objectToBytes(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);	
		
		try{
			IContent content = (IContent) object;
			dos.writeUTF(content.getId());
			dos.writeLong(DateHelper.normalize(new Date()).getTime());
			dos.writeUTF(content.getPayload());
			dos.flush();
			
			byte[] data = baos.toByteArray();
			return data;
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(dos != null){
				try{
					dos.close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public boolean matchesByLastUpdateTime(byte[] data, Date sinceDate) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			dis.readUTF();	// skip id
			long lastUpdateTime = dis.readLong();
			boolean ok = sinceDate == null || (sinceDate != null && lastUpdateTime >= sinceDate.getTime());
			return ok;
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(dis != null){
				try{
					dis.close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public boolean matchesById(byte[] data, String id) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			String idData = dis.readUTF();
			return idData.equals(id);
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		} finally{
			if(dis != null){
				try{
					dis.close();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}