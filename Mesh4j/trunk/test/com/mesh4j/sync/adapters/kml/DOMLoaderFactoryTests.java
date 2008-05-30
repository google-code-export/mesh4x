package com.mesh4j.sync.adapters.kml;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.dom.IDOMLoader;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.security.NullIdentityProvider;

public class DOMLoaderFactoryTests {
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullFileName(){
		DOMLoaderFactory.createDOMLoader(null, NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptEmptyFileName(){
		DOMLoaderFactory.createDOMLoader("", NullIdentityProvider.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullSecurity(){
		DOMLoaderFactory.createDOMLoader("a.txt", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldOnlyAcceptKMLorKMZFileNameExtension(){  // valid extension are KML y KMZ
		
		DOMLoaderFactory.createDOMLoader("a.txt", NullIdentityProvider.INSTANCE);
	}
	
	@Test
	public void shouldCreateKMLLoader(){ 
		IDOMLoader loader = DOMLoaderFactory.createDOMLoader("a.kml", NullIdentityProvider.INSTANCE);
		
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof KMLDOMLoader);
		Assert.assertEquals(NullIdentityProvider.INSTANCE, loader.getIdentityProvider());
		
		KMLDOMLoader kmlLoader = (KMLDOMLoader)loader;		
		Assert.assertNotNull(kmlLoader.getXMLView());
		Assert.assertEquals(5, kmlLoader.getXMLView().getXMLViewElements().size());
		
		IXMLViewElement hierarchy = kmlLoader.getXMLView().getXMLViewElements().get(0);
		Assert.assertEquals(MeshNames.MESH_QNAME_HIERARCHY, hierarchy.getQName());
		Assert.assertSame(kmlLoader.getDOM(), ((HierarchyXMLViewElement) hierarchy).getKmlDOM());
		
		Assert.assertEquals(KmlNames.KML_QNAME_STYLE_MAP, kmlLoader.getXMLView().getXMLViewElements().get(1).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(1)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_STYLE, kmlLoader.getXMLView().getXMLViewElements().get(2).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(2)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_FOLDER, kmlLoader.getXMLView().getXMLViewElements().get(3).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(3)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_PLACEMARK, kmlLoader.getXMLView().getXMLViewElements().get(4).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(4)).getHierarchyElementView());
		
		Assert.assertEquals("a.kml", ((KMLDOMLoader)loader).getFile().getName());
	}
	
	@Test
	public void shouldCreateKMZLoader(){ 
		IDOMLoader loader = DOMLoaderFactory.createDOMLoader("a.kmz", NullIdentityProvider.INSTANCE);
		
		Assert.assertNotNull(loader);
		Assert.assertTrue(loader instanceof KMZDOMLoader);
		Assert.assertEquals(NullIdentityProvider.INSTANCE, loader.getIdentityProvider());

		KMZDOMLoader kmlLoader = (KMZDOMLoader)loader;		
		Assert.assertNotNull(kmlLoader.getXMLView());
		Assert.assertEquals(5, kmlLoader.getXMLView().getXMLViewElements().size());

		IXMLViewElement hierarchy = kmlLoader.getXMLView().getXMLViewElements().get(0);
		Assert.assertEquals(MeshNames.MESH_QNAME_HIERARCHY, hierarchy.getQName());
		Assert.assertSame(kmlLoader.getDOM(), ((HierarchyXMLViewElement) hierarchy).getKmlDOM());
		
		Assert.assertEquals(KmlNames.KML_QNAME_STYLE_MAP, kmlLoader.getXMLView().getXMLViewElements().get(1).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(1)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_STYLE, kmlLoader.getXMLView().getXMLViewElements().get(2).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(2)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_FOLDER, kmlLoader.getXMLView().getXMLViewElements().get(3).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(3)).getHierarchyElementView());
		
		Assert.assertEquals(KmlNames.KML_QNAME_PLACEMARK, kmlLoader.getXMLView().getXMLViewElements().get(4).getQName());
		Assert.assertSame(hierarchy, ((KMLViewElement)kmlLoader.getXMLView().getXMLViewElements().get(4)).getHierarchyElementView());

		
		Assert.assertEquals("a.kmz", ((KMZDOMLoader)loader).getFile().getName());
	}
}
