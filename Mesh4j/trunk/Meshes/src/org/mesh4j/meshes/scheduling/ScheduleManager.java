package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;

public class ScheduleManager {

	private static ScheduleManager instance;
	private Map<String, List<String>> scheduledTasksPerMesh;
	private Scheduler scheduler;
	
	private ScheduleManager() {
		scheduledTasksPerMesh = new HashMap<String, List<String>>();
		scheduler = new Scheduler();
		scheduler.start();
	}
	
	public synchronized static ScheduleManager getInstance() {
		if (instance == null) {
			instance = new ScheduleManager();
		}
		return instance;
	}
	
	public void initialize() throws Exception {
		ConfigurationManager configMgr = new ConfigurationManager();
		for (Mesh mesh : configMgr.getAllMeshes()) {
			scheduleMesh(mesh);
		}
	}
	
	public void scheduleMesh(Mesh mesh) {
		
		// Remove existing schedule for this mesh
		if (isScheduled(mesh))
			unscheduleMesh(mesh);
		
		// Schedule every data set in the mesh
		List<String> taskIds = new ArrayList<String>();
		for (DataSet dataSet : mesh.getDataSets()) {
			Task task = null; // TODO: Create task for synchronization
			Schedule schedule = dataSet.getSchedule();
			String pattern = getSchedulingPattern(schedule.getSchedulingOption());
			if (pattern != null) {
				String taskId = scheduler.schedule(pattern, task);
				taskIds.add(taskId);
			}
		}
		scheduledTasksPerMesh.put(mesh.getName(), taskIds);
	}
	
	public boolean isScheduled(Mesh mesh) {
		return scheduledTasksPerMesh.containsKey(mesh.getName());
	}
	
	public void unscheduleMesh(Mesh mesh) {
		List<String> taskIds = scheduledTasksPerMesh.get(mesh.getName());
		if (taskIds == null)
			return;
		
		for (String taskId : taskIds) {	
			scheduler.deschedule(taskId);
		}
		
		scheduledTasksPerMesh.remove(mesh.getName());
	}
	
	private String getSchedulingPattern(SchedulingOption schedulingOption) {
		
		if (schedulingOption == null)
			return null;
		
		switch (schedulingOption) {
		case AUTOMATIC:
			return null;
		case FIVE_MINUTES:
			return "*/5 * * * *"; // Every five minutes
		case MANUALLY:
			return null;
		case ONE_DAY:
			return "0 12 * * *"; // Every day at 12:00
		case ONE_HOUR:
			return "5 * * * *"; // Every hour, on the our
		case TEN_MINUTES:
			return "*/10 * * * *"; // Every ten minutes
		}
		
		return null;
	}
}
