package org.mesh4j.sync.test.utils.concurrent.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ConcurrentCommandExecutor {

	// MODEL VARIABLES
	private boolean executing = false;

	// BUSINESS METHODS
	public ConcurrentCommandExecutor() {
		super();
		setExecuting(false);
	}

	public static List<Object> executeCommands(List<IConcurrentCommand> commands) throws InterruptedException {
		ConcurrentCommandExecutor executor = new ConcurrentCommandExecutor();
		List<Object> results = executor.execute(commands);
		
		return results;
	}
	
    public List<Object> execute(IConcurrentCommand... commands) {
    	List<IConcurrentCommand> all = new ArrayList<IConcurrentCommand>();
    	for (IConcurrentCommand concurrentCommand : commands) {
			all.add(concurrentCommand);
		}
    	return execute(all);
    }
    
    public List<Object> execute(List<IConcurrentCommand> commands) {
    	ArrayList<Object> results = new ArrayList<Object>();
		HashMap<IConcurrentCommand, Thread> threads = new HashMap<IConcurrentCommand, Thread>();

		setExecuting(true);

		// Start each command in a new thread
		Iterator<IConcurrentCommand> iter = commands.iterator();
		while (iter.hasNext()) {
			IConcurrentCommand command = iter.next();
			command.setExecutor(this);

			Thread t = new Thread(command);
			t.start();
			threads.put(command, t);
		}

		// wait until the mandatory threads have finished
		iter = threads.keySet().iterator();
		while (iter.hasNext()) {
			IConcurrentCommand command = iter.next();
			if (command.isWorker()) {
				Thread t = (Thread) threads.get(command);
				try {
					t.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} 
		}

		setExecuting(false);
		
		// wait until all the threads have finished
		iter = threads.keySet().iterator();
		while (iter.hasNext()) {
			IConcurrentCommand command = iter.next();
			Thread t = (Thread) threads.get(command);
			try {
				t.join();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		// Collect the results
		iter = commands.iterator();
		while (iter.hasNext()) {
			IConcurrentCommand command = iter.next();
			if(command.getResult()!= null){
				results.add(command.getResult());				
			}
			if(command.getException()!= null){
				ArrayList<Exception> errors = new ArrayList<Exception>();
				errors.add(command.getException()); 
			}
		}

    	return results;
    }

	public boolean isExecuting() {
		return executing;
	}

	public void setExecuting(boolean b) {
		executing = b;
	}

}
