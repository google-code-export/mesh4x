package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;

public class UUIDStringToHexStringSchemaTypeFormat implements ISchemaTypeFormat {

	public static final UUIDStringToHexStringSchemaTypeFormat INSTANCE = new UUIDStringToHexStringSchemaTypeFormat();

	@Override
	public Object format(Object fieldValue) throws Exception {
		byte[] bytes = getBytes(fieldValue);
		return Hibernate.BINARY.toString(bytes);

	}

	public byte[] getBytes(Object fieldValue) throws IOException {
		String uuidString = (String) fieldValue;
		UUID uuid = UUID.fromString(uuidString);
		
		long mostSignificantBits = uuid.getMostSignificantBits();
		
		ByteArrayOutputStream mostSignificantBAOS = new ByteArrayOutputStream();
		DataOutputStream mostSignificantDOS = new DataOutputStream(mostSignificantBAOS);
		mostSignificantDOS.writeLong(mostSignificantBits);
		mostSignificantDOS.flush();
		
		byte[] mostSigBytes = mostSignificantBAOS.toByteArray();
		
		long leastSignificantBits = uuid.getLeastSignificantBits();
		ByteArrayOutputStream leastSignificantBAOS = new ByteArrayOutputStream();
		DataOutputStream leastSignificantDOS = new DataOutputStream(leastSignificantBAOS);
		leastSignificantDOS.writeByte(mostSigBytes[3]);
		leastSignificantDOS.writeByte(mostSigBytes[2]);
		leastSignificantDOS.writeByte(mostSigBytes[1]);
		leastSignificantDOS.writeByte(mostSigBytes[0]);
		leastSignificantDOS.writeByte(mostSigBytes[5]);
		leastSignificantDOS.writeByte(mostSigBytes[4]);
		leastSignificantDOS.writeByte(mostSigBytes[7]);
		leastSignificantDOS.writeByte(mostSigBytes[6]);
		leastSignificantDOS.writeLong(leastSignificantBits);
		leastSignificantDOS.flush();
		
		byte[] bytes = leastSignificantBAOS.toByteArray();
		return bytes;
	}

	@Override
	public Object parseObject(String fieldValue) throws Exception {
		String hexString = (String) fieldValue;
		byte[] bytes = (byte[])Hibernate.BINARY.fromStringValue(hexString);
		
		byte[] bytesMostSignificant = new byte[8];
		bytesMostSignificant[0] = bytes[3];
		bytesMostSignificant[1] = bytes[2];
		bytesMostSignificant[2] = bytes[1];
		bytesMostSignificant[3] = bytes[0];
		
		bytesMostSignificant[6] = bytes[7];
		bytesMostSignificant[7] = bytes[6];
		bytesMostSignificant[4] = bytes[5];
		bytesMostSignificant[5] = bytes[4];
		
		Long mostSignificant = ByteBuffer.wrap(bytesMostSignificant).getLong();
				
		byte[] bytesLestSignificant = new byte[8];
		bytesLestSignificant[0] = bytes[8];
		bytesLestSignificant[1] = bytes[9];
		bytesLestSignificant[2] = bytes[10];
		bytesLestSignificant[3] = bytes[11];
		bytesLestSignificant[4] = bytes[12];
		bytesLestSignificant[5] = bytes[13];
		bytesLestSignificant[6] = bytes[14];
		bytesLestSignificant[7] = bytes[15];
		
		Long leastSignificant = ByteBuffer.wrap(bytesLestSignificant).getLong();
		UUID uuid = new UUID(mostSignificant, leastSignificant);	
		return uuid.toString();
	}

}
