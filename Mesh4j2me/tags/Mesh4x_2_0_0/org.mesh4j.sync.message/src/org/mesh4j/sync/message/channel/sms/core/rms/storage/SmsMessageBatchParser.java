package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

import org.mesh4j.sync.adapters.rms.storage.IObjectParser;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.validations.MeshException;

public class SmsMessageBatchParser implements IObjectParser{

	// BUSINESS METHODS
	public SmsMessageBatchParser() {
		super();
	}	
	
	public Object bytesToObject(byte[] data) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			String messageBatchId = dis.readUTF();
			String sessionId = dis.readUTF();
			String protocolHeader = dis.readUTF();
			int expectedMessageCount = dis.readInt();
			SmsEndpoint endpoint = new SmsEndpoint(dis.readUTF());
			boolean discarded = dis.readBoolean();
			boolean waitingForAck = dis.readBoolean();
			
			SmsMessageBatch batch = new SmsMessageBatch(sessionId, endpoint, protocolHeader, messageBatchId, expectedMessageCount);
			batch.setDiscarded(discarded);
			
			if(waitingForAck){
				batch.setWaitForACK();
			} else {
				batch.setNotWaitForACK();
			}
			
			SmsMessage message;
			String text;
			Date date;
			int sequence;
			int messagesSize = dis.readInt();
			for (int i = 0; i < messagesSize; i++) {
				sequence = dis.readInt();
				text = dis.readUTF();
				date = new Date(dis.readLong());
				message = new SmsMessage(text, date);
				batch.addMessage(sequence, message);
			}
			
			return batch;
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
			SmsMessageBatch batch = (SmsMessageBatch) object;
			dos.writeUTF(batch.getId());
			dos.writeUTF(batch.getSessionId());
			dos.writeUTF(batch.getProtocolHeader());
			dos.writeInt(batch.getExpectedMessageCount());
			dos.writeUTF(batch.getEndpoint().getEndpointId());
			dos.writeBoolean(batch.isDiscarded());
			dos.writeBoolean(batch.isWaitingForACK());
			
			int messagesSize = batch.getMessagesCount();
			dos.writeInt(messagesSize);

			SmsMessage message;
			for (int i = 0; i < batch.getExpectedMessageCount(); i++) {
				message = batch.getMessage(i);
				
				if(message != null){
					dos.writeInt(i);
					dos.writeUTF(message.getText());
					dos.writeLong(message.getLastModificationDate().getTime());
				}
			}
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

	public boolean matchByID(byte[] data, String id) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			String messageBatchId = dis.readUTF();
			return messageBatchId.equals(id);
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

	public boolean matchBySessionID(byte[] data, String sessionId) {
		DataInputStream dis = null;
		try{
			dis = new DataInputStream(new ByteArrayInputStream(data));
			
			dis.readUTF();  // skip messageBatchId
			String localSessionId = dis.readUTF();
			return localSessionId.equals(sessionId);
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