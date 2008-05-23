package com.mesh4j.sync.adapters.kml.spli.content.adapter;

import org.junit.Test;

import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLContentAdapterTests {

	// TODO (JMT) test
	
	@Test
	public void spike() throws Exception{
		KMLContentAdapter.prepareKMLToSync(TestHelper.fileName(IdGenerator.newID()+".xml"));		
	}

}