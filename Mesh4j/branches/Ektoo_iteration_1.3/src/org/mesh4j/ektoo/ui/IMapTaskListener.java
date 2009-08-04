package org.mesh4j.ektoo.ui;

import org.mesh4j.ektoo.tasks.IErrorListener;

public interface IMapTaskListener extends IErrorListener {
	
	public void notifyProgress(String msg);

}
