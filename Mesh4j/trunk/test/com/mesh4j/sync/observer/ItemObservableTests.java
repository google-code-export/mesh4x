package com.mesh4j.sync.observer;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.model.Item;

public class ItemObservableTests {

	@Test(expected=NullPointerException.class)
	public void shouldValidateNullObserver(){
		ItemObservable io = new ItemObservable();
		
		io.addObserver(null);
	}
	
	@Test
	public void shouldAddObserver(){
		ItemObservable io = new ItemObservable();
		ItemObserver o = new ItemObserver(){
			@Override
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
	}
	
	@Test
	public void shouldRemoveObserver(){
		ItemObservable io = new ItemObservable();
		ItemObserver o = new ItemObserver(){
			@Override
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
		
		io.removeObserver(o);
	}
	
	@Test
	public void shouldRemoveNullObserver(){
		ItemObservable io = new ItemObservable();		
		io.removeObserver(null);
	}
	
	@Test
	public void shouldNotNotify(){
		ItemObservable io = new ItemObservable();
		io.notifyObservers(null);
	}
	
	@Test
	public void shouldNotify(){
		ItemObservable io = new ItemObservable();
		MyItemObserver o = new MyItemObserver();
		
		io.addObserver(o);
		io.notifyObservers(null);
		
		Assert.assertTrue(o.ok);
	}
	
	@Test
	public void shouldNotNotifyIfRemoveObserver(){
		ItemObservable io = new ItemObservable();
		MyItemObserver o = new MyItemObserver();
		
		io.addObserver(o);
		io.notifyObservers(null);
		
		Assert.assertTrue(o.ok);
		
		o.ok = false;
		Assert.assertFalse(o.ok);
		
		io.removeObserver(o);
		io.notifyObservers(null);
		
		Assert.assertFalse(o.ok);
	}
	
	private class MyItemObserver implements ItemObserver{
		public boolean ok = false;
		@Override
		public void notifyItemNovelty(Item item) {
			ok = true;
		}
	}
}
