package com.feed.sync.model;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.model.Feed;

public class FeedTests {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfTitleNull() {
		new Feed(null, "link", "description");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfTitleEmpty() {
		new Feed("", "link", "description");
	}

	@Test
	public void ShouldNotThrowIfDescriptionNull() {
		new Feed("title", "link", null);
	}

	@Test
	public void ShouldNotThrowIfDescriptionEmpty() {
		new Feed("title", "link", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfLinkNull() {
		new Feed("title", null, "description");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfLinkEmpty() {
		new Feed("title", "", "description");
	}

	@Test
	public void ShouldMatchConstructorWithProperties() {
		Feed f = new Feed("title", "link", "description");
		Assert.assertEquals("title", f.getTitle());
		Assert.assertEquals("link", f.getLink());
		Assert.assertEquals("description", f.getDescription());
	}
}
