package org.mesh4j.sync.adapters.rms.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SyncInfoObjectParser implements IObjectParser{
	
	// MODEL VARIABLES
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	
	// BUSINESS METHODS
	public SyncInfoObjectParser(ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		this.feedReader = new FeedReader(syndicationFormat, identityProvider, idGenerator);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider);
	}
		
	public Object bytesToObject(byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			dis.readUTF();  // skip syncId
			String type = dis.readUTF();
			String id = dis.readUTF();
			int version = dis.readInt();
			String syncAsXml = dis.readUTF();
			Sync sync = this.feedReader.readSync(syncAsXml);
			
			SyncInfo syncInfo = new SyncInfo(sync, type, id, version);
			return syncInfo;
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
			SyncInfo syncInfo = (SyncInfo) object;
			dos.writeUTF(syncInfo.getSyncId());
			dos.writeUTF(syncInfo.getType());
			dos.writeUTF(syncInfo.getId());
			dos.writeInt(syncInfo.getVersion());
			
			String syncAsXml = this.feedWriter.writeSyncAsXml(syncInfo.getSync());			
			dos.writeUTF(syncAsXml);			
			
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

	public boolean matchesByType(byte[] data, String type) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			dis.readUTF();			// skip syncId
			String typeData = dis.readUTF();
			
			return type == null || (type !=null && type.equals(typeData));
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

	public boolean matchesBySyncId(byte[] data, String syncId) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			String syncIdData = dis.readUTF();
			return syncIdData.equals(syncId);
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
