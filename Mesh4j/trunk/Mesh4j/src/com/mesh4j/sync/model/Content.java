package com.mesh4j.sync.model;

import org.dom4j.Element;

public interface Content extends Cloneable{

	Element getPayload();
	
	Content clone();
}
