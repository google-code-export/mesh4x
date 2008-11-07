package com.mesh4j.sync.adapter.S3.emulator;

public class ObjectData {

	// MODEL VARIABLES
	private String id;
	private byte[] data;

	// BUSINESS METHODs
	public ObjectData(String id, byte[] data){
		super();
		this.id = id;
		this.data = data;
	}
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
