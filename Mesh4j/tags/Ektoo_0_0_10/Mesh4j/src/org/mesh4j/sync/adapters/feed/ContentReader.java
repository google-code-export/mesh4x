package org.mesh4j.sync.adapters.feed;

import java.util.List;

import org.dom4j.Element;

public class ContentReader implements IContentReader {

	public static final ContentReader INSTANCE = new ContentReader();

	private ContentReader(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void readContent(String syncId, Element payload, Element contentElement) {
		if(payload == null || contentElement == null){
			return;
		}
		
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(contentElement.getName())){
			List<Element> contentElements = contentElement.elements();
			for (Element element : contentElements) {
				payload.add(element.createCopy());
			}
		} else {
			payload.add(contentElement);
		}
	}

}
