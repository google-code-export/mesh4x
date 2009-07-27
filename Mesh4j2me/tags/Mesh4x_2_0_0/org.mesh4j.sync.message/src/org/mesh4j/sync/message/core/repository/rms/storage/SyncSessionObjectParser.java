package org.mesh4j.sync.message.core.repository.rms.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.mesh4j.sync.adapters.rms.storage.IObjectParser;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.utils.DiffUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class SyncSessionObjectParser implements IObjectParser {

	// MODEL VARIABLES
	private RmsStorageSyncSessionRepository repository;

	// BUSINESS METHODS
	
	public SyncSessionObjectParser(RmsStorageSyncSessionRepository repository) {
		Guard.argumentNotNull(repository, "repository");
		this.repository = repository;
	}
	
	public Object bytesToObject(byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			String sessionId = dis.readUTF();
			int version = dis.readInt();
			String sourceId = dis.readUTF();
			String endpointId = dis.readUTF();
			long lastSyncDateTime = dis.readLong();
			boolean open = dis.readBoolean();
			boolean fullProtocol = dis.readBoolean();
			boolean cancelled = dis.readBoolean();
			String acksAsString = dis.readUTF();
			String conflictsAsString = dis.readUTF();
			
			IMessageSyncAdapter syncAdapter = this.repository.getSource(sourceId);
			IEndpoint target = this.repository.getEndpoint(endpointId);
			
			RmsSyncSession syncSession = new RmsSyncSession(this.repository, sessionId, version, syncAdapter, target, fullProtocol);
			syncSession.setOpen(open);
			syncSession.setCancelled(cancelled);
			syncSession.setLastSyncDate(lastSyncDateTime == 0 ? null : new Date(lastSyncDateTime));
						
			if(!acksAsString.equals("NULL")){
				String[] acksArray = DiffUtils.split(acksAsString, "|");
				String ack;
				for (int i = 0; i < acksArray.length; i++) {
					ack = acksArray[i];
					ack.trim();
					if(ack.length() > 0){
						syncSession.waitForAck(ack);
					}
				}
			}
			
			if(!conflictsAsString.equals("NULL")){
				String[] conflictsArray = DiffUtils.split(conflictsAsString, "|");
				String conflict;
				for (int i = 0; i < conflictsArray.length; i++) {
					conflict = conflictsArray[i];
					conflict.trim();
					if(conflict.length() > 0){
						syncSession.addConflict(conflict);
					}
				}
			}
			
//System.out.println("READ Session: " + sessionId + " version: " +  version + " sourceId: " + sourceId + " endpoint: " + endpointId + " full: " + fullProtocol + " open: " + open + " cancelled: " + cancelled + " lastDateTime: " + lastSyncDateTime + " acks: " + acksAsString + " conflicts: " + conflictsAsString);
			return syncSession;
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
			RmsSyncSession syncSession = (RmsSyncSession) object;
			dos.writeUTF(syncSession.getSessionId());
			dos.writeInt(syncSession.getVersion());
			dos.writeUTF(syncSession.getSourceId());
			dos.writeUTF(syncSession.getTarget().getEndpointId());

			long dateTime = syncSession.getLastSyncDate() == null ? 0 : syncSession.getLastSyncDate().getTime(); 
			dos.writeLong(dateTime);
			dos.writeBoolean(syncSession.isOpen());
			dos.writeBoolean(syncSession.isFullProtocol());
			dos.writeBoolean(syncSession.isCancelled());
			
			String pendingsACKs = "NULL";
			if(syncSession.getAllPendingACKs().isEmpty()){
				dos.writeUTF(pendingsACKs);
			} else {
				StringBuffer sb = new StringBuffer();
				String ack;
				for (int i = 0; i < syncSession.getAllPendingACKs().size(); i++) {
					ack = (String) syncSession.getAllPendingACKs().elementAt(i);					
					sb.append(ack);
					if(i+1 != syncSession.getAllPendingACKs().size()){
						sb.append("|");
					}
				}
				pendingsACKs = sb.toString();
				dos.writeUTF(pendingsACKs);
			}
			
			String conflicts = "NULL";
			if(syncSession.getConflictsSyncIDs().isEmpty()){
				dos.writeUTF(conflicts);
			} else {
				StringBuffer sb = new StringBuffer();
				String conflictSyncID;
				for (int i = 0; i < syncSession.getConflictsSyncIDs().size(); i++) {
					conflictSyncID = (String) syncSession.getConflictsSyncIDs().elementAt(i);					
					sb.append(conflictSyncID);
					if(i+1 != syncSession.getConflictsSyncIDs().size()){
						sb.append("|");
					}
				}
				conflicts = sb.toString();
				dos.writeUTF(conflicts);
			}
			
			dos.flush();			
			byte[] data = baos.toByteArray();
//System.out.println("WRITE Session: " + syncSession.getSessionId() + " version: " +  syncSession.getVersion() + " sourceId: " + syncSession.getSourceId() + " endpoint: " + syncSession.getTarget().getEndpointId() + " full: " + syncSession.isFullProtocol() + " open: " + syncSession.isOpen() + " cancelled: " + syncSession.isCancelled() + " lastDateTime: " + dateTime + " acks: " + pendingsACKs + " conflicts: " + conflicts);
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

	public String getSessionId(byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			String sessionId = dis.readUTF();
			return sessionId;
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

	public boolean matchesBySourceIdAndEndpointId(String sourceId, String endpointId, byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));				

			dis.readUTF();		// skip sessionId 
			dis.readInt();		// skip version
			String sourceIdData = dis.readUTF();
			String endpointIdData = dis.readUTF();
			return sourceId.equals(sourceIdData) && endpointId.equals(endpointIdData);
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
