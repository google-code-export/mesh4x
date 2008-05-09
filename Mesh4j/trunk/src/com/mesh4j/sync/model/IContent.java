package com.mesh4j.sync.model;

import org.dom4j.Element;

public interface IContent extends Cloneable{

	Element getPayload();
	
	IContent clone();
}
