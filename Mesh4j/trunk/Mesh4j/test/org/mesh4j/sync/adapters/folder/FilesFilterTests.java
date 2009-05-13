package org.mesh4j.sync.adapters.folder;

import org.junit.Assert;
import org.junit.Test;

public class FilesFilterTests {

	@Test
	public void shouldFilterAllByDefault(){
		FilesFilter filter = new FilesFilter();
		
		Assert.assertTrue(filter.accept(null, "foo"));
		Assert.assertTrue(filter.accept(null, "foo.bar"));
		Assert.assertTrue(filter.accept(null, "foo.foobar"));
	}
	
	@Test
	public void shouldFilterIncludeByFileName(){
		FilesFilter filter = new FilesFilter();
		filter.includeFileName("foo");
		filter.includeFileName("bar.bar");
		
		Assert.assertTrue(filter.accept(null, "foo"));
		Assert.assertTrue(filter.accept(null, "bar.bar"));
		Assert.assertFalse(filter.accept(null, "foo.bar"));
		Assert.assertFalse(filter.accept(null, "foo.foobar"));
	}
	
	@Test
	public void shouldFilterIncludeByFileExtension(){
		FilesFilter filter = new FilesFilter();
		filter.includeFileExt("bar1");
		filter.includeFileExt("bar");
		
		Assert.assertTrue(filter.accept(null, "foo.bar"));
		Assert.assertTrue(filter.accept(null, "foo.bar1"));
		Assert.assertFalse(filter.accept(null, "foo"));
		Assert.assertFalse(filter.accept(null, "foo.foobar"));
	}
	
	@Test
	public void shouldFilterIncludeByFileNameAndFileExtension(){
		FilesFilter filter = new FilesFilter();
		filter.includeFileName("foo");
		filter.includeFileName("bar.bar");
		filter.includeFileExt("bar1");
		filter.includeFileExt("bar2");
		
		Assert.assertTrue(filter.accept(null, "foo"));
		Assert.assertTrue(filter.accept(null, "bar.bar"));
		Assert.assertTrue(filter.accept(null, "foo.bar1"));
		Assert.assertTrue(filter.accept(null, "foo.bar2"));
		Assert.assertFalse(filter.accept(null, "foo3"));
		Assert.assertFalse(filter.accept(null, "foo.foobar"));
		
	}
	
	@Test
	public void shouldFilterExcludeByFileName(){
		FilesFilter filter = new FilesFilter();
		filter.excludeFileName("foo");
		filter.excludeFileName("bar.bar");
		
		Assert.assertFalse(filter.accept(null, "foo"));
		Assert.assertFalse(filter.accept(null, "bar.bar"));
		Assert.assertTrue(filter.accept(null, "foo.bar"));
		Assert.assertTrue(filter.accept(null, "foo.foobar"));
	}
	
	@Test
	public void shouldFilterExcludeByFileExtension(){
		FilesFilter filter = new FilesFilter();
		filter.excludeFileExt("bar1");
		filter.excludeFileExt("bar");
		
		Assert.assertFalse(filter.accept(null, "foo.bar"));
		Assert.assertFalse(filter.accept(null, "foo.bar1"));
		Assert.assertTrue(filter.accept(null, "foo"));
		Assert.assertTrue(filter.accept(null, "foo.foobar"));
	}
	
	@Test
	public void shouldFilterExcludeByFileNameAndFileExtension(){
		FilesFilter filter = new FilesFilter();
		filter.excludeFileName("foo");
		filter.excludeFileName("bar.bar");
		filter.excludeFileExt("bar1");
		filter.excludeFileExt("bar2");
		
		Assert.assertFalse(filter.accept(null, "foo"));
		Assert.assertFalse(filter.accept(null, "bar.bar"));
		Assert.assertFalse(filter.accept(null, "foo.bar1"));
		Assert.assertFalse(filter.accept(null, "foo.bar2"));
		Assert.assertTrue(filter.accept(null, "foo3"));
		Assert.assertTrue(filter.accept(null, "foo.foobar"));
		
	}
	
	
	@Test
	public void shouldFilterIncludeExclude(){
		FilesFilter filter = new FilesFilter();
		filter.includeFileName("bar.bar");
		filter.includeFileExt("bar1");
		filter.excludeFileExt("bar");
		filter.excludeFileName("foo");
		
		Assert.assertTrue(filter.accept(null, "bar.bar"));
		Assert.assertTrue(filter.accept(null, "bar.bar1"));
		Assert.assertFalse(filter.accept(null, "bar2.bar"));
		Assert.assertFalse(filter.accept(null, "foo"));
		Assert.assertFalse(filter.accept(null, "foo3"));       // is excluded
		Assert.assertFalse(filter.accept(null, "foo.foobar"));// is excluded
		
	}
	
}
