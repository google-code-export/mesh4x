package com.mesh4j.sync.adapter.S3.emulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.mesh4j.sync.adapters.S3.IS3Service;
import org.mesh4j.sync.adapters.S3.ObjectData;


public class S3Service implements IS3Service {
	
	// MODEL VARIABLES
	private HashMap<String, Node> nodes = new HashMap<String, Node>(); 
	private long delay = 0;
	private int propagationProcess = 0;
	
	// BUSINESS METHODS
	public S3Service(){
		super();
	}
	
	public S3Service(long delay){
		super();
		this.delay = delay;
	}
	
	public void addNode(String nodeId){
		this.nodes.put(nodeId, new Node(nodeId));
	}
	
	public void write(String bucketId, String oid, byte[] data){
		WriteAction wa = new WriteAction(bucketId, oid, data);
		propagateChanges(wa);
	}
	
	public byte[] read(String bucketId, String oid){
		Node node = (Node)this.nodes.values().toArray()[randomIndex()];
		return node.read(bucketId, oid);
	}
	
	@Override
	public List<ObjectData> readObjectsStartsWith(String bucket, String oidPath) {
		Node node = (Node)this.nodes.values().toArray()[randomIndex()];
		return node.readObjectsStartsWith(bucket, oidPath);
	}
	
	public List<ObjectData> readObjectsStartsWith(String nodeId, String bucket, String oidPath) {
		Node node = this.nodes.get(nodeId);
		if(node == null){
			return new ArrayList<ObjectData>();
		} else {
			return node.readObjectsStartsWith(bucket, oidPath);
		}
	}
	
	private int randomIndex(){
		int index = 0;
		if(this.nodes.size() > 1){
			Random r = new Random();
			index = r.nextInt(this.nodes.size()-1);
		}
		return index;
	}
	
	private void propagateChanges(final IAction<Node> action){
		
		Runnable target = new Runnable(){
			@Override
			public void run() {
				S3Service.this.incrementPropagationProcess();
				for (Node node : S3Service.this.nodes.values()) {
					action.execute(node);	
					if(S3Service.this.delay > 0){
						try{
							Thread.sleep(S3Service.this.delay);
						} catch(Exception e){
							// nothing to do
						}
					}
				}
				S3Service.this.decrementPropagationProcess();
			}			
		};
		
		Thread t = new Thread(target);
		t.start();
	}

	protected synchronized void decrementPropagationProcess() {
		propagationProcess = propagationProcess -1;
	}

	protected synchronized void incrementPropagationProcess() {
		propagationProcess = propagationProcess +1;
	}

	public void writeAndFastReplicate(String bucketId, String oid, byte[] data) {
		for (Node  node : this.nodes.values()) {
			node.write(bucketId, oid, data);
		}
	}

	public byte[] read(String nodeId, String bucketId, String oid) {
		Node node = this.nodes.get(nodeId);
		if(node == null){
			return null;
		} else {
			return node.read(bucketId, oid);
		}
	}

	public void write(String nodeId, String bucketId, String oid, byte[] data) {
		Node node = this.nodes.get(nodeId);
		if(node == null){
			return;
		} else {
			node.write(bucketId, oid, data);
		}
	}
	
	@Override
	public void deleteObject(String bucket, String oid) {
		TreeSet<String> oids = new TreeSet<String>();
		oids.add(oid);
		
		DeleteAction da = new DeleteAction(bucket, oids);
		propagateChanges(da);		
	}

	@Override
	public void deleteObjects(String bucketId, Set<String> oids) {
		DeleteAction da = new DeleteAction(bucketId, oids);
		propagateChanges(da);		
	}
	
	public class WriteAction implements IAction<Node>{

		// MODEL VARIABLES
		private String bucketId;
		private String oid;
		private byte[] data;
		
		// BUISINESS METHODS
		
		public WriteAction(String bucketId, String oid, byte[] data) {
			super();
			this.bucketId = bucketId;
			this.data = data;
			this.oid = oid;
		}
		
		@Override
		public void execute(Node target) {
			target.write(bucketId, oid, data);			
		}		
	}
	
	public class DeleteAction implements IAction<Node>{

		// MODEL VARIABLES
		private String bucketId;
		private Set<String> oids;
		
		// BUISINESS METHODS
		
		public DeleteAction(String bucketId, Set<String> oids) {
			super();
			this.bucketId = bucketId;
			this.oids = oids;
		}
		
		@Override
		public void execute(Node target) {
			target.delete(bucketId, oids);			
		}		
	}

	public boolean isPropagatingChanges() {
		return propagationProcess >0;
	}

}
