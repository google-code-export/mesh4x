package org.mesh4j.sync.observer;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.model.Item;


public class ItemObservableTests {

	@Test(expected=NullPointerException.class)
	public void shouldValidateNullObserver(){
		ObservableItem io = new ObservableItem();
		
		io.addObserver(null);
	}
	
	@Test
	public void shouldAddObserver(){
		ObservableItem io = new ObservableItem();
		IObserverItem o = new IObserverItem(){
			@Override
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
	}
	
	@Test
	public void shouldRemoveObserver(){
		ObservableItem io = new ObservableItem();
		IObserverItem o = new IObserverItem(){
			@Override
			public void notifyItemNovelty(Item item) {
			}
		};
		
		io.addObserver(o);
		
		io.removeObserver(o);
	}
	
	@Test
	public void shouldRemoveNullObserver(){
		ObservableItem io = new ObservableItem();		
		io.removeObserver(null);
	}
	
	@Test
	public void shouldNotNotify(){
		ObservableItem io = new ObservableItem();
		io.notifyObservers(null);
	}
	
	@Test
	public void shouldNotify(){
		ObservableItem io = new ObservableItem();
		MyItemObserver o = new MyItemObserver();
		
		io.addObserver(o);
		io.notifyObservers(null);
		
		Assert.assertTrue(o.ok);
	}
	
	@Test
	public void shouldNotNotifyIfRemoveObserver(){
		ObservableItem io = new ObservableItem();
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
	
	private class MyItemObserver implements IObserverItem{
		public boolean ok = false;
		@Override
		public void notifyItemNovelty(Item item) {
			ok = true;
		}
	}
}
