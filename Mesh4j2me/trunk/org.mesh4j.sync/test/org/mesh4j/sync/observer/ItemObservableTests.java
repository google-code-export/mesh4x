package org.mesh4j.sync.observer;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import org.mesh4j.sync.model.Item;

public class ItemObservableTests extends TestCase{

	public ItemObservableTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public ItemObservableTests(String name) {
		super(name);
	}
	
	public ItemObservableTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new ItemObservableTests("shouldValidateNullObserver", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldValidateNullObserver();}})));
		suite.addTest((new ItemObservableTests("shouldAddObserver", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldAddObserver();}})));
		suite.addTest((new ItemObservableTests("shouldRemoveObserver", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldRemoveObserver();}})));
		suite.addTest((new ItemObservableTests("shouldRemoveNullObserver", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldRemoveNullObserver();}})));
		suite.addTest((new ItemObservableTests("shouldNotNotify", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldNotNotify();}})));
		suite.addTest((new ItemObservableTests("shouldNotify", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldNotify();}})));
		suite.addTest((new ItemObservableTests("shouldNotNotifyIfRemoveObserver", new TestMethod(){public void run(TestCase tc){((ItemObservableTests)tc).shouldNotNotifyIfRemoveObserver();}})));
		return suite;
	}

	public void shouldValidateNullObserver(){
		ObservableItem io = new ObservableItem();
		try{
			io.addObserver(null);
			fail("expected NullPointerException");
		} catch(NullPointerException e){
			// right test
		}
	}
	
	public void shouldAddObserver(){
		ObservableItem io = new ObservableItem();
		IObserverItem o = new IObserverItem(){
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
	}
	
	public void shouldRemoveObserver(){
		ObservableItem io = new ObservableItem();
		IObserverItem o = new IObserverItem(){
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
		
		io.removeObserver(o);
	}
	
	public void shouldRemoveNullObserver(){
		ObservableItem io = new ObservableItem();		
		io.removeObserver(null);
	}
	
	public void shouldNotNotify(){
		ObservableItem io = new ObservableItem();
		io.notifyObservers(null);
	}
	
	public void shouldNotify(){
		ObservableItem io = new ObservableItem();
		MyItemObserver o = new MyItemObserver();
		
		io.addObserver(o);
		io.notifyObservers(null);
		
		assertTrue(o.ok);
	}
	
	public void shouldNotNotifyIfRemoveObserver(){
		ObservableItem io = new ObservableItem();
		MyItemObserver o = new MyItemObserver();
		
		io.addObserver(o);
		io.notifyObservers(null);
		
		assertTrue(o.ok);
		
		o.ok = false;
		assertEquals(false, o.ok);
		
		io.removeObserver(o);
		io.notifyObservers(null);
		
		assertEquals(false, o.ok);
	}
	
	private class MyItemObserver implements IObserverItem{
		public boolean ok = false;

		public void notifyItemNovelty(Item item) {
			ok = true;
		}
	}
}
