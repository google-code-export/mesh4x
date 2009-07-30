package org.mesh4j.ektoo.tasks;

public interface ISynchronizeTaskListener 
{
	void notifySynchronizeTaskError(String error);
	void notifySynchronizeTaskSuccess(String success);
	void notifySynchronizeTaskConflict(String conflict);
}
