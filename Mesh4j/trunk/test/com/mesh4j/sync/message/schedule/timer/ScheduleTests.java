package com.mesh4j.sync.message.schedule.timer;

import junit.framework.Assert;

import org.junit.Test;

public class ScheduleTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateTaskFailWhenTaskIDIsNull(){
		new MyTask(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateTaskFailWhenTaskIDIsEmpty(){
		new MyTask("");
	}
	
	@Test
	public void shouldGetAddedTask() throws Exception {
		String taskId = "1";
		
		TimerScheduler.INSTANCE.schedule(new MyTask(taskId), 5000);
		MyTask task = (MyTask) TimerScheduler.INSTANCE.getTask(taskId);
		Assert.assertNotNull(task);
	}

	@Test
	public void shouldGetTaskReturnsNullWhenTaskIsNotScheduled() {
		Assert.assertNull(TimerScheduler.INSTANCE.getTask("2"));
	}
	
	@Test
	public void shouldScheduleTask() throws Exception {
		String taskId = "3";
	
		TimerScheduler.INSTANCE.schedule(new MyTask(taskId), 70);
		Thread.sleep(300);
		
		MyTask task = (MyTask) TimerScheduler.INSTANCE.getTask(taskId);
		Assert.assertEquals(4, task.getExecutuions());
	}
	
	@Test
	public void shouldCancelNoFailsWhenTaskIsNotScheduled(){
		TimerScheduler.INSTANCE.cancelTask("4");
	}
	
	@Test
	public void shouldCancelTask() throws Exception {
		String taskId = "5";
	
		TimerScheduler.INSTANCE.schedule(new MyTask(taskId), 50);
		Thread.sleep(300);
		
		MyTask task = (MyTask) TimerScheduler.INSTANCE.getTask(taskId);
		int lastExecutions = task.getExecutuions();
		TimerScheduler.INSTANCE.cancelTask(taskId);
		Assert.assertNull(TimerScheduler.INSTANCE.getTask(taskId));
		
		Thread.sleep(300);
		
		Assert.assertEquals(lastExecutions, task.getExecutuions());
	}
	
	
	private class MyTask extends ScheduleTimerTask {

		private int executions = 0;
		
		public MyTask(String scheduleTaskId) {
			super(scheduleTaskId);
		}
		
		@Override
		public void run() {
			this.executions++;
			
		}

		public int getExecutuions() {
			return executions;
		}
		
	}
}
