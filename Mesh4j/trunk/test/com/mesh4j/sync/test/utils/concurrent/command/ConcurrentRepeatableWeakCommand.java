package com.mesh4j.sync.test.utils.concurrent.command;

public abstract class ConcurrentRepeatableWeakCommand extends ConcurrentCommand {

	// MODEL VARIABLES
	private Long delayProcess;

	// BUSINESS METHODS
	public ConcurrentRepeatableWeakCommand(Object[] params, Long delay) {
		super(params, delay);
	}
	
	public Long getDelayProcess() {
		if(delayProcess == null){
			delayProcess = new Long(5000);
		}
		return delayProcess;
	}

	public void setDelayProcess(Long delay) {
		delayProcess = delay;
	}
	

	protected boolean mustBeExecuted() {
		return this.getExecutor().isExecuting();
	}

	public boolean isWeak() {
		return true;
	}

	public Object execute(Object[] objects) throws Exception {
		Object result;
		while(mustBeExecuted()){
			try {
				long start = System.currentTimeMillis();
				result = this.basicExecute(objects);
				long end = System.currentTimeMillis();
				System.out.println(getDescription(objects, result) + " time in millis: " + (end-start));
				Thread.sleep(getDelayProcess().longValue());
			} catch(Exception e){
				this.processException(objects, e);
			}		
		}
		return null;
	}

	protected abstract String getDescription(Object[] objects, Object result);
	protected abstract Object basicExecute(Object[] objects) throws Exception;
	
	protected void processException(Object[] objects, Exception e){
		e.printStackTrace();
		System.out.println(getDescription(objects, null) + "  EXECUTING ERROR");
		System.out.println(e.getMessage());				
	}
}
