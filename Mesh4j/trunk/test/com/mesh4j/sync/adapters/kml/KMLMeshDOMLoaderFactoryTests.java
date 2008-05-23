package com.mesh4j.sync.adapters.kml;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.security.NullIdentityProvider;

public class KMLMeshDOMLoaderFactoryTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullFileName(){
		KMLMeshDOMLoaderFactory.createDOMLoader(null, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptEmptyFileName(){
		KMLMeshDOMLoaderFactory.createDOMLoader("", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullSecurity(){
		KMLMeshDOMLoaderFactory.createDOMLoader("a.txt", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldOnlyAcceptKMLorKMZFileNameExtension(){  // valid extension are KML y KMZ
		
		KMLMeshDOMLoaderFactory.createDOMLoader("a.txt", NullIdentityProvider.INSTANCE);
	}
	
	@Test
	public void shouldCreateKMLLoader(){ 
		IKMLMeshDomLoader loader = KMLMeshDOMLoaderFactory.createDOMLoader("a.kml", NullIdentityProvider.INSTANCE);
		
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof KMLDOMLoader);
		Assert.assertEquals(NullIdentityProvider.INSTANCE, loader.getIdentityProvider());
		
		Assert.assertEquals(KMLMeshDOMLoaderFactory.getDefaultXMLView(), ((KMLDOMLoader)loader).getXMLView());
		Assert.assertEquals("a.kml", ((KMLDOMLoader)loader).getFile().getName());
	}
	
	@Test
	public void shouldCreateKMZLoader(){ 
		IKMLMeshDomLoader loader = KMLMeshDOMLoaderFactory.createDOMLoader("a.kmz", NullIdentityProvider.INSTANCE);
		
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof KMZDOMLoader);
		Assert.assertEquals(NullIdentityProvider.INSTANCE, loader.getIdentityProvider());
		Assert.assertEquals(KMLMeshDOMLoaderFactory.getDefaultXMLView(), ((KMZDOMLoader)loader).getXMLView());
		Assert.assertEquals("a.kmz", ((KMZDOMLoader)loader).getFile().getName());
	}
}
