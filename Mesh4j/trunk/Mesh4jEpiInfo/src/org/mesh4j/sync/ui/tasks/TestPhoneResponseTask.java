package org.mesh4j.sync.ui.tasks;

import org.mesh4j.sync.ui.MeshCompactUI;

public class TestPhoneResponseTask extends TestPhoneTask {

	// MODEL VARIABLEs
	private String response;
	private String endpointId;
	
	// BUSINESS METHODS
	public TestPhoneResponseTask(MeshCompactUI ui, String endpointId, String response){
		super(ui);
		this.endpointId = endpointId;
		this.response = response;
	}
	
    public Void doInBackground() {
		this.sendSms(this.endpointId, this.response);
		return null;
    }
}
